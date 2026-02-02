package com.example.tasktimer.data

import androidx.compose.ui.graphics.Color
import com.example.tasktimer.data.local.RoomCategoryRepository
import com.example.tasktimer.data.local.RoomFocusStatsRepository
import com.example.tasktimer.data.local.RoomTaskRepository
import com.example.tasktimer.model.*
import com.example.tasktimer.ui.dashboard.StatsPeriod
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Repositório unificado que usa Room como fonte de dados local.
 * Esta classe serve como ponte entre o código existente e a nova implementação com Room.
 */
class RoomRepository(
    private val taskRepository: RoomTaskRepository,
    private val categoryRepository: RoomCategoryRepository,
    private val focusStatsRepository: RoomFocusStatsRepository
) {
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
    ): String = taskRepository.addTask(title, description, dateTime, categoryId, subtasks, pomodoroConfig)

    suspend fun addTaskWithSessions(task: Task): String =
        taskRepository.addTaskWithSessions(task)

    suspend fun updateTask(
        taskId: String,
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: String?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ) = taskRepository.updateTask(taskId, title, description, dateTime, categoryId, subtasks, pomodoroConfig)

    suspend fun toggleTaskCompletion(taskId: String) =
        taskRepository.toggleTaskCompletion(taskId)

    suspend fun deleteTask(taskId: String) =
        taskRepository.deleteTask(taskId)

    // ========== Categories ==========
    
    fun getCategoriesFlow(): Flow<List<Category>> =
        categoryRepository.getCategoriesFlow()

    suspend fun getCategoriesMap(): Map<String, String> =
        categoryRepository.getCategoriesMap()

    suspend fun addCategory(name: String, color: Color): String =
        categoryRepository.addCategory(name, color)

    suspend fun updateCategory(categoryId: String, name: String, color: Color) =
        categoryRepository.updateCategory(categoryId, name, color)

    suspend fun deleteCategory(categoryId: String) {
        // Primeiro, remover a categoria de todas as tasks que a usam
        taskRepository.removeCategoryFromTasks(categoryId)
        // Depois deletar a categoria
        categoryRepository.deleteCategory(categoryId)
    }

    // ========== Pomodoro Presets ==========
    
    fun getPomodoroPresets(): List<Pair<String, PomodoroConfig>> =
        taskRepository.getPomodoroPresets()

    // ========== Focus Stats ==========
    
    suspend fun getStats(period: StatsPeriod): FocusStats =
        focusStatsRepository.getStats(period)
}
