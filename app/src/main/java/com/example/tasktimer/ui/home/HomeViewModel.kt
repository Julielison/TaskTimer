package com.example.tasktimer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktimer.data.FirestoreRepository
import com.example.tasktimer.data.SampleDataInserter
import com.example.tasktimer.model.Task
import com.example.tasktimer.model.Category
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Subtask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {
    private val repository = FirestoreRepository()
    private val sampleDataInserter = SampleDataInserter(repository)

    // Adicione esta variável para armazenar todas as tasks
    private var allTasks = listOf<Task>()

    private val _overdueTasks = MutableStateFlow<List<Task>>(emptyList())
    val overdueTasks: StateFlow<List<Task>> = _overdueTasks.asStateFlow()

    private val _todayTasks = MutableStateFlow<List<Task>>(emptyList())
    val todayTasks: StateFlow<List<Task>> = _todayTasks.asStateFlow()

    private val _tomorrowTasks = MutableStateFlow<List<Task>>(emptyList())
    val tomorrowTasks: StateFlow<List<Task>> = _tomorrowTasks.asStateFlow()

    private val _laterTasks = MutableStateFlow<List<Task>>(emptyList())
    val laterTasks: StateFlow<List<Task>> = _laterTasks.asStateFlow()

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
        viewModelScope.launch {
            repository.getCategoriesFlow().collect { categories ->
                _categories.value = categories
            }
        }
        
        viewModelScope.launch {
            repository.getTasksFlow().collect { tasks ->
                allTasks = tasks
                updateTaskLists(tasks)
            }
        }
        
        // Adicione um listener para mudanças no filtro
        viewModelScope.launch {
            _selectedFilter.collect {
                updateTaskLists(allTasks)
            }
        }
        
        _pomodoroPresets.value = repository.getPomodoroPresets()
    }

    fun selectFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
        _filterTitle.value = when (filter) {
            is TaskFilter.All -> "Todas"
            is TaskFilter.Today -> "Hoje"
            is TaskFilter.Category -> {
                _categories.value.find { it.id == filter.categoryId }?.name ?: "Categoria"
            }
        }
    }
    
    fun refreshCategories() {
        viewModelScope.launch {
            repository.getCategoriesFlow().collect { categories ->
                _categories.value = categories
            }
        }
    }

    private fun updateTaskLists(allTasks: List<Task> = emptyList()) {
        val filter = _selectedFilter.value
        
        val filteredTasks = when (filter) {
            is TaskFilter.All -> allTasks
            is TaskFilter.Today -> {
                val today = java.time.LocalDate.now()
                allTasks.filter { it.dateTime.toLocalDate() == today }
            }
            is TaskFilter.Category -> {
                allTasks.filter { it.categoryId == filter.categoryId }
            }
        }

        val today = java.time.LocalDate.now()
        val tomorrow = today.plusDays(1)

        _overdueTasks.value = filteredTasks
            .filter { it.isOverdue && !it.isCompleted }
            .sortedBy { it.dateTime }

        _todayTasks.value = filteredTasks
            .filter { 
                it.dateTime.toLocalDate() == today && !it.isCompleted 
            }
            .sortedBy { it.dateTime }

        _tomorrowTasks.value = filteredTasks
            .filter { 
                it.dateTime.toLocalDate() == tomorrow && !it.isCompleted 
            }
            .sortedBy { it.dateTime }

        _laterTasks.value = filteredTasks
            .filter { 
                it.dateTime.toLocalDate().isAfter(tomorrow) && !it.isCompleted 
            }
            .sortedBy { it.dateTime }

        _completedTasks.value = filteredTasks
            .filter {
                it.isCompleted &&
                it.completedAt?.toLocalDate() == today
            }
            .sortedByDescending { it.completedAt }
    }

    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId)
        }
    }

    fun updateTask(
        taskId: String,
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: String?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ) {
        viewModelScope.launch {
            repository.updateTask(taskId, title, description, dateTime, categoryId, subtasks, pomodoroConfig)
        }
    }

    fun addTask(
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: String?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ) {
        viewModelScope.launch {
            repository.addTask(title, description, dateTime, categoryId, subtasks, pomodoroConfig)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            try {
                repository.deleteTask(taskId)
            } catch (e: Exception) {
                println("Erro ao deletar task: ${e.message}")
            }
        }
    }

    fun insertSampleData() {
        viewModelScope.launch {
            try {
                sampleDataInserter.insertSampleData()
            } catch (e: Exception) {
                // Handle error
                println("Erro ao inserir dados de exemplo: ${e.message}")
            }
        }
    }

    fun addCategory(name: String, color: androidx.compose.ui.graphics.Color) {
        viewModelScope.launch {
            try {
                repository.addCategory(name, color)
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    fun updateCategory(categoryId: String, name: String, color: androidx.compose.ui.graphics.Color) {
        viewModelScope.launch {
            try {
                repository.updateCategory(categoryId, name, color)
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(categoryId)
            } catch (e: Exception) {
                // Log error
            }
        }
    }
}

sealed class TaskFilter {
    object All : TaskFilter()
    object Today : TaskFilter()
    data class Category(val categoryId: String) : TaskFilter()
}