package com.example.tasktimer.data

import com.example.tasktimer.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()
    private val tasksCollection = db.collection("tasks")
    private val categoriesCollection = db.collection("categories")

    // Tasks
    fun getTasksFlow(): Flow<List<Task>> = callbackFlow {
        val listener = tasksCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val tasks = snapshot?.documents?.mapNotNull { doc ->
                doc.data?.let { Task.fromMap(doc.id, it) }
            } ?: emptyList()
            trySend(tasks)
        }
        awaitClose { listener.remove() }
    }

    suspend fun getTasksByDate(date: LocalDate): List<Task> {
        val startOfDay = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        val endOfDay = date.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        
        return tasksCollection
            .whereGreaterThanOrEqualTo("dateTime", startOfDay)
            .whereLessThan("dateTime", endOfDay)
            .get()
            .await()
            .documents
            .mapNotNull { doc -> doc.data?.let { Task.fromMap(doc.id, it) } }
    }

    suspend fun getTasksByDateRange(startDate: LocalDate, endDate: LocalDate): List<Task> {
        val start = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        val end = endDate.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        
        return tasksCollection
            .whereGreaterThanOrEqualTo("dateTime", start)
            .whereLessThan("dateTime", end)
            .get()
            .await()
            .documents
            .mapNotNull { doc -> doc.data?.let { Task.fromMap(doc.id, it) } }
    }

    suspend fun getCompletedTasksByDateRange(startDate: LocalDate, endDate: LocalDate): List<Task> {
        val start = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        val end = endDate.plusDays(1).atStartOfDay().toEpochSecond(ZoneOffset.UTC)
        
        // Buscar todas as tasks e filtrar em memória para evitar necessidade de índice composto
        return tasksCollection
            .whereEqualTo("isCompleted", true)
            .get()
            .await()
            .documents
            .mapNotNull { doc -> doc.data?.let { Task.fromMap(doc.id, it) } }
            .filter { task ->
                val completedAt = task.completedAt?.toEpochSecond(ZoneOffset.UTC) ?: return@filter false
                completedAt >= start && completedAt < end
            }
    }

    suspend fun addTask(
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: String?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ): String {
        val task = Task(
            title = title,
            description = description,
            dateTime = dateTime,
            categoryId = categoryId,
            subtasks = subtasks,
            pomodoroConfig = pomodoroConfig
        )
        val docRef = tasksCollection.add(task.toMap()).await()
        return docRef.id
    }

    suspend fun addTaskWithSessions(task: Task): String {
        val docRef = tasksCollection.add(task.toMap()).await()
        return docRef.id
    }

    suspend fun updateTask(
        taskId: String,
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: String?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ) {
        val updates = mapOf(
            "title" to title,
            "description" to description,
            "dateTime" to dateTime.toEpochSecond(ZoneOffset.UTC),
            "categoryId" to categoryId,
            "subtasks" to subtasks.map { it.toMap() },
            "pomodoroConfig" to pomodoroConfig?.toMap()
        )
        tasksCollection.document(taskId).update(updates).await()
    }

    suspend fun toggleTaskCompletion(taskId: String) {
        val doc = tasksCollection.document(taskId).get().await()
        val task = doc.data?.let { Task.fromMap(doc.id, it) } ?: return
        
        val updates = mapOf(
            "isCompleted" to !task.isCompleted,
            "completedAt" to if (!task.isCompleted) 
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) 
            else null
        )
        tasksCollection.document(taskId).update(updates).await()
    }

    // Categories
    fun getCategoriesFlow(): Flow<List<Category>> = callbackFlow {
        val listener = categoriesCollection
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val categories = snapshot?.documents?.mapNotNull { doc ->
                    doc.data?.let { Category.fromMap(doc.id, it) }
                } ?: emptyList()
                trySend(categories)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getCategoriesMap(): Map<String, String> {
        return categoriesCollection
            .get()
            .await()
            .documents
            .mapNotNull { doc ->
                doc.data?.let { data ->
                    doc.id to (data["name"] as? String ?: "Sem nome")
                }
            }
            .toMap()
    }

    // Adicione a palavra-chave "suspend"
    suspend fun addCategory(name: String, color: androidx.compose.ui.graphics.Color): String {
        val category = Category(name = name, color = color)
        val docRef = categoriesCollection.add(category.toMap()).await()
        return docRef.id
    }


    // Pomodoro Presets (hardcoded, não precisa Firestore)
    fun getPomodoroPresets(): List<Pair<String, PomodoroConfig>> {
        return listOf(
            "Padrão" to PomodoroConfig(),
            "Curto" to PomodoroConfig(workDurationMinutes = 15, breakDurationMinutes = 3),
            "Longo" to PomodoroConfig(workDurationMinutes = 50, breakDurationMinutes = 10)
        )
    }
}
