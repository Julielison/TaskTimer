package com.example.tasktimer.data

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.example.tasktimer.data.local.RoomCategoryRepository
import com.example.tasktimer.data.local.RoomFocusStatsRepository
import com.example.tasktimer.data.local.RoomTaskRepository
import com.example.tasktimer.model.*
import com.example.tasktimer.ui.dashboard.StatsPeriod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Repositório unificado que usa Room como fonte de dados local com sincronização Firebase.
 * Implementa estratégia "local-first" com sincronização bidirecional:
 * 1. Todas as operações de escrita são executadas primeiro no Room (offline-first)
 * 2. Após sucesso local, sincroniza com Firebase em background (Room → Firebase)
 * 3. Monitora mudanças do Firebase e atualiza Room (Firebase → Room)
 * 4. Falhas de sincronização não afetam a operação local
 */
class RoomRepository(
    private val taskRepository: RoomTaskRepository,
    private val categoryRepository: RoomCategoryRepository,
    private val focusStatsRepository: RoomFocusStatsRepository,
    private val firestoreRepository: FirestoreRepository
) {
    private val TAG = "RoomRepository"
    private val syncScope = CoroutineScope(Dispatchers.IO)
    private var tasksSyncJob: Job? = null
    private var categoriesSyncJob: Job? = null

    init {
        // Inicia sincronização bidirecional automaticamente
        startFirebaseSync()
    }

    /**
     * Sincroniza operação de escrita com Firebase em background (Room → Firebase).
     * Não bloqueia a operação local se falhar.
     */
    private fun syncToFirebase(operation: suspend () -> Unit) {
        syncScope.launch {
            try {
                operation()
            } catch (e: Exception) {
                Log.w(TAG, "Falha na sincronização Room → Firebase: ${e.message}", e)
                // Não propaga o erro - a operação local já foi bem-sucedida
            }
        }
    }

    /**
     * Inicia sincronização contínua do Firebase para o Room (Firebase → Room).
     * Monitora mudanças em tempo real e atualiza o banco local.
     */
    private fun startFirebaseSync() {
        // Sincronizar Tasks do Firebase para Room
        tasksSyncJob = syncScope.launch {
            try {
                firestoreRepository.getTasksFlow().collect { firebaseTasks ->
                    try {
                        // Obter tasks locais
                        val localTasks = taskRepository.getAllTasksOnce()
                        val localTaskIds = localTasks.map { it.id }.toSet()
                        
                        // Atualizar/adicionar tasks do Firebase no Room
                        firebaseTasks.forEach { firebaseTask ->
                            if (firebaseTask.id !in localTaskIds) {
                                // Nova task do Firebase - adicionar ao Room
                                taskRepository.addTaskWithSessions(firebaseTask)
                                Log.d(TAG, "Task sincronizada do Firebase: ${firebaseTask.id}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Erro ao sincronizar tasks Firebase → Room: ${e.message}", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao iniciar sincronização de tasks: ${e.message}", e)
            }
        }

        // Sincronizar Categories do Firebase para Room
        categoriesSyncJob = syncScope.launch {
            try {
                firestoreRepository.getCategoriesFlow().collect { firebaseCategories ->
                    try {
                        // Obter categorias locais
                        val localCategories = categoryRepository.getCategories()
                        val localCategoryIds = localCategories.map { it.id }.toSet()
                        
                        // Atualizar/adicionar categorias do Firebase no Room
                        firebaseCategories.forEach { firebaseCategory ->
                            if (firebaseCategory.id !in localCategoryIds) {
                                // Nova categoria do Firebase - adicionar ao Room mantendo o ID
                                categoryRepository.addCategoryWithId(
                                    firebaseCategory.id,
                                    firebaseCategory.name,
                                    firebaseCategory.color
                                )
                                Log.d(TAG, "Categoria sincronizada do Firebase: ${firebaseCategory.id}")
                            }
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Erro ao sincronizar categorias Firebase → Room: ${e.message}", e)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao iniciar sincronização de categorias: ${e.message}", e)
            }
        }
    }

    /**
     * Sincroniza manualmente todos os dados do Firebase para o Room.
     * Útil para sincronização inicial ou recuperação de dados.
     */
    suspend fun syncAllFromFirebase() {
        syncScope.launch {
            try {
                Log.d(TAG, "Iniciando sincronização completa Firebase → Room")
                
                // Buscar dados do Firebase manualmente
                val firebaseCategories = mutableListOf<Category>()
                val firebaseTasks = mutableListOf<Task>()
                
                // Coletar categorias
                firestoreRepository.getCategoriesFlow().collect { categories ->
                    firebaseCategories.clear()
                    firebaseCategories.addAll(categories)
                    return@collect // Sair após primeira coleta
                }
                
                // Coletar tasks
                firestoreRepository.getTasksFlow().collect { tasks ->
                    firebaseTasks.clear()
                    firebaseTasks.addAll(tasks)
                    return@collect // Sair após primeira coleta
                }
                
                // Sincronizar categorias primeiro (tasks dependem delas)
                firebaseCategories.forEach { category ->
                    try {
                        categoryRepository.addCategoryWithId(
                            category.id,
                            category.name,
                            category.color
                        )
                    } catch (e: Exception) {
                        Log.w(TAG, "Erro ao sincronizar categoria ${category.id}: ${e.message}")
                    }
                }
                
                // Sincronizar tasks
                firebaseTasks.forEach { task ->
                    try {
                        taskRepository.addTaskWithSessions(task)
                    } catch (e: Exception) {
                        Log.w(TAG, "Erro ao sincronizar task ${task.id}: ${e.message}")
                    }
                }
                
                Log.d(TAG, "Sincronização completa finalizada: ${firebaseCategories.size} categorias, ${firebaseTasks.size} tasks")
            } catch (e: Exception) {
                Log.e(TAG, "Erro na sincronização completa: ${e.message}", e)
            }
        }
    }

    /**
     * Para a sincronização contínua do Firebase.
     * Útil para economizar recursos quando não é necessária.
     */
    fun stopFirebaseSync() {
        tasksSyncJob?.cancel()
        categoriesSyncJob?.cancel()
        Log.d(TAG, "Sincronização Firebase pausada")
    }

    /**
     * Reinicia a sincronização contínua do Firebase.
     */
    fun resumeFirebaseSync() {
        stopFirebaseSync()
        startFirebaseSync()
        Log.d(TAG, "Sincronização Firebase retomada")
    }
    // ========== Tasks ==========
    
    fun getTasksFlow(): Flow<List<Task>> = taskRepository.getTasksFlow()

    suspend fun getTasksByDate(date: LocalDate): List<Task> =
        taskRepository.getTasksByDate(date)

    suspend fun getTasksByDateRange(startDate: LocalDate, endDate: LocalDate): List<Task> =
        taskRepository.getTasksByDateRange(startDate, endDate)

    suspend fun getCompletedTasksByDateRange(startDate: LocalDate, endDate: LocalDate): List<Task> =
        taskRepository.getCompletedTasksByDateRange(startDate, endDate)

    suspend fun addTask(
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: String?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ): String {
        // 1. Salvar localmente no Room
        val taskId = taskRepository.addTask(title, description, dateTime, categoryId, subtasks, pomodoroConfig)
        
        // 2. Sincronizar com Firebase em background usando o mesmo ID
        syncToFirebase {
            val task = Task(
                id = taskId,
                title = title,
                description = description,
                dateTime = dateTime,
                categoryId = categoryId,
                subtasks = subtasks,
                pomodoroConfig = pomodoroConfig
            )
            firestoreRepository.addTaskWithId(task)
        }
        
        return taskId
    }

    suspend fun addTaskWithSessions(task: Task): String {
        // 1. Salvar localmente no Room
        val taskId = taskRepository.addTaskWithSessions(task)
        
        // 2. Sincronizar com Firebase em background usando o mesmo ID
        val taskWithId = task.copy(id = taskId)
        syncToFirebase {
            firestoreRepository.addTaskWithId(taskWithId)
        }
        
        return taskId
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
        // 1. Atualizar localmente no Room
        taskRepository.updateTask(taskId, title, description, dateTime, categoryId, subtasks, pomodoroConfig)
        
        // 2. Sincronizar com Firebase em background
        syncToFirebase {
            firestoreRepository.updateTask(taskId, title, description, dateTime, categoryId, subtasks, pomodoroConfig)
        }
    }

    suspend fun toggleTaskCompletion(taskId: String) {
        // 1. Atualizar localmente no Room
        taskRepository.toggleTaskCompletion(taskId)
        
        // 2. Sincronizar com Firebase em background
        syncToFirebase {
            firestoreRepository.toggleTaskCompletion(taskId)
        }
    }

    suspend fun deleteTask(taskId: String) {
        // 1. Deletar localmente no Room
        taskRepository.deleteTask(taskId)
        
        // 2. Sincronizar com Firebase em background
        syncToFirebase {
            firestoreRepository.deleteTask(taskId)
        }
    }

    // ========== Categories ==========
    
    fun getCategoriesFlow(): Flow<List<Category>> =
        categoryRepository.getCategoriesFlow()

    suspend fun getCategoriesMap(): Map<String, String> =
        categoryRepository.getCategoriesMap()

    suspend fun addCategory(name: String, color: Color): String {
        // 1. Salvar localmente no Room
        val categoryId = categoryRepository.addCategory(name, color)
        
        // 2. Sincronizar com Firebase em background usando o mesmo ID
        syncToFirebase {
            firestoreRepository.addCategoryWithId(categoryId, name, color)
        }
        
        return categoryId
    }

    suspend fun updateCategory(categoryId: String, name: String, color: Color) {
        // 1. Atualizar localmente no Room
        categoryRepository.updateCategory(categoryId, name, color)
        
        // 2. Sincronizar com Firebase em background
        syncToFirebase {
            firestoreRepository.updateCategory(categoryId, name, color)
        }
    }

    suspend fun deleteCategory(categoryId: String) {
        // 1. Remover a categoria de todas as tasks que a usam (Room)
        taskRepository.removeCategoryFromTasks(categoryId)
        
        // 2. Deletar a categoria localmente no Room
        categoryRepository.deleteCategory(categoryId)
        
        // 3. Sincronizar com Firebase em background
        syncToFirebase {
            firestoreRepository.deleteCategory(categoryId)
        }
    }

    // ========== Pomodoro Presets ==========
    
    fun getPomodoroPresets(): List<Pair<String, PomodoroConfig>> =
        taskRepository.getPomodoroPresets()

    // ========== Focus Stats ==========
    
    suspend fun getStats(period: StatsPeriod): FocusStats =
        focusStatsRepository.getStats(period)
}
