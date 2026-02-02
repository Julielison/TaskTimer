package com.example.tasktimer.data.local

import com.example.tasktimer.data.local.dao.TaskDao
import com.example.tasktimer.data.local.entity.TaskEntity
import com.example.tasktimer.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

class RoomTaskRepository(
    private val taskDao: TaskDao,
    private val categoryRepository: RoomCategoryRepository
) {
    
    fun getTasksFlow(): Flow<List<Task>> {
        return taskDao.getAllFlow().map { entities ->
            entities.map { it.toTask() }
        }
    }

    suspend fun getAllTasksOnce(): List<Task> {
        return taskDao.getAll().map { it.toTask() }
    }

    suspend fun getTaskById(id: String): Task? {
        return taskDao.getById(id)?.toTask()
    }

    suspend fun getTasksByDate(date: LocalDate): List<Task> {
        val startOfDay = date.atStartOfDay()
        val endOfDay = date.plusDays(1).atStartOfDay()
        return taskDao.getByDateRange(startOfDay, endOfDay).map { it.toTask() }
    }

    suspend fun getTasksByDateRange(startDate: LocalDate, endDate: LocalDate): List<Task> {
        val start = startDate.atStartOfDay()
        val end = endDate.plusDays(1).atStartOfDay()
        return taskDao.getByDateRange(start, end).map { it.toTask() }
    }

    suspend fun getCompletedTasksByDateRange(startDate: LocalDate, endDate: LocalDate): List<Task> {
        val start = startDate.atStartOfDay()
        val end = endDate.plusDays(1).atStartOfDay()
        return taskDao.getCompletedByDateRange(start, end).map { it.toTask() }
    }

    suspend fun addTask(
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: String?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ): String {
        val id = UUID.randomUUID().toString()
        val task = Task(
            id = id,
            title = title,
            description = description,
            dateTime = dateTime,
            categoryId = categoryId,
            subtasks = subtasks,
            pomodoroConfig = pomodoroConfig
        )
        val entity = TaskEntity.fromTask(task)
        taskDao.insert(entity)
        return id
    }

    suspend fun addTaskWithSessions(task: Task): String {
        val id = if (task.id.isEmpty()) UUID.randomUUID().toString() else task.id
        val taskWithId = task.copy(id = id)
        val entity = TaskEntity.fromTask(taskWithId)
        taskDao.insert(entity)
        return id
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
        val existing = taskDao.getById(taskId) ?: return
        val updated = existing.copy(
            title = title,
            description = description,
            dateTime = dateTime,
            categoryId = categoryId,
            subtasks = subtasks,
            pomodoroConfig = pomodoroConfig
        )
        taskDao.update(updated)
    }

    suspend fun toggleTaskCompletion(taskId: String) {
        val task = taskDao.getById(taskId) ?: return
        val updated = task.copy(
            isCompleted = !task.isCompleted,
            completedAt = if (!task.isCompleted) LocalDateTime.now() else null
        )
        taskDao.update(updated)
    }

    suspend fun deleteTask(taskId: String) {
        taskDao.deleteById(taskId)
    }

    suspend fun removeCategoryFromTasks(categoryId: String) {
        taskDao.removeCategoryFromTasks(categoryId)
    }

    fun searchTasks(query: String): Flow<List<Task>> {
        return taskDao.searchTasks(query).map { entities ->
            entities.map { it.toTask() }
        }
    }

    // Presets de Pomodoro (hardcoded)
    fun getPomodoroPresets(): List<Pair<String, PomodoroConfig>> {
        return listOf(
            "Padrão" to PomodoroConfig(),
            "Curto" to PomodoroConfig(workDurationMinutes = 15, breakDurationMinutes = 3),
            "Longo" to PomodoroConfig(workDurationMinutes = 50, breakDurationMinutes = 10)
        )
    }
}
