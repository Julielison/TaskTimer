package com.example.tasktimer.ui.home

import com.example.tasktimer.model.Category
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Task

data class HomeUiState(
    val overdueTasks: List<Task> = emptyList(),
    val todayTasks: List<Task> = emptyList(),
    val tomorrowTasks: List<Task> = emptyList(),
    val laterTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val categories: List<Category> = emptyList(),
    val pomodoroPresets: List<Pair<String, PomodoroConfig>> = emptyList(),
    val selectedFilter: TaskFilter = TaskFilter.All,
    val filterTitle: String = "Todas",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class TaskFilter {
    object All : TaskFilter()
    object Today : TaskFilter()
    data class Category(val categoryId: String) : TaskFilter()
}
