package com.example.tasktimer.ui.home

import androidx.lifecycle.ViewModel
import com.example.tasktimer.data.MockTaskRepository
import com.example.tasktimer.model.Task
import com.example.tasktimer.model.Category
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Subtask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {

    private val _overdueTasks = MutableStateFlow<List<Task>>(emptyList())
    val overdueTasks: StateFlow<List<Task>> = _overdueTasks.asStateFlow()

    private val _todayTasks = MutableStateFlow<List<Task>>(emptyList())
    val todayTasks: StateFlow<List<Task>> = _todayTasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<List<Task>>(emptyList())
    val completedTasks: StateFlow<List<Task>> = _completedTasks.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _pomodoroPresets = MutableStateFlow<List<Pair<String, PomodoroConfig>>>(emptyList())
    val pomodoroPresets: StateFlow<List<Pair<String, PomodoroConfig>>> = _pomodoroPresets.asStateFlow()

    private val _selectedFilter = MutableStateFlow<TaskFilter>(TaskFilter.All)
    val selectedFilter: StateFlow<TaskFilter> = _selectedFilter.asStateFlow()
    
    private val _filterTitle = MutableStateFlow("Todas")
    val filterTitle: StateFlow<String> = _filterTitle.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _categories.value = MockTaskRepository.getCategories()
        _pomodoroPresets.value = MockTaskRepository.getPomodoroPresets()
        updateTaskLists()
    }

    fun selectFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
        _filterTitle.value = when (filter) {
            is TaskFilter.All -> "Todas"
            is TaskFilter.Today -> "Hoje"
            is TaskFilter.Category -> {
                MockTaskRepository.getCategories().find { it.id == filter.categoryId }?.name ?: "Categoria"
            }
        }
        updateTaskLists()
    }

    private fun updateTaskLists() {
        val filter = _selectedFilter.value
        
        val filteredTasks = when (filter) {
            is TaskFilter.All -> MockTaskRepository.getAllTasks()
            is TaskFilter.Today -> {
                val today = java.time.LocalDate.now()
                MockTaskRepository.getAllTasks().filter { 
                    it.dateTime.toLocalDate() == today 
                }
            }
            is TaskFilter.Category -> {
                MockTaskRepository.getAllTasks().filter { 
                    it.categoryId == filter.categoryId 
                }
            }
        }

        _overdueTasks.value = filteredTasks
            .filter { it.isOverdue && !it.isCompleted }
            .sortedBy { it.dateTime }

        _todayTasks.value = filteredTasks
            .filter { 
                val today = java.time.LocalDate.now()
                it.dateTime.toLocalDate() == today && !it.isCompleted 
            }
            .sortedBy { it.dateTime }

        _completedTasks.value = filteredTasks
            .filter {
                val today = java.time.LocalDate.now()
                it.isCompleted &&
                it.completedAt?.toLocalDate() == today
            }
            .sortedByDescending { it.completedAt }
    }

    fun toggleTaskCompletion(taskId: Int) {
        MockTaskRepository.toggleTaskCompletion(taskId)
        updateTaskLists()
    }

    fun updateTask(
        taskId: Int,
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: Int?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ) {
        MockTaskRepository.updateTask(taskId, title, description, dateTime, categoryId, subtasks, pomodoroConfig)
        updateTaskLists()
    }

    fun addTask(
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: Int?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ) {
        MockTaskRepository.addTask(title, description, dateTime, categoryId, subtasks, pomodoroConfig)
        updateTaskLists()
    }
}

sealed class TaskFilter {
    object All : TaskFilter()
    object Today : TaskFilter()
    data class Category(val categoryId: Int) : TaskFilter()
}