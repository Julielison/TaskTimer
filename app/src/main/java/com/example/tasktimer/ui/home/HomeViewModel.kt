package com.example.tasktimer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktimer.data.FirestoreRepository
import com.example.tasktimer.data.SampleDataInserter
import com.example.tasktimer.model.Task
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

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getCategoriesFlow().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
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
            _uiState.collect {
                updateTaskLists(allTasks)
            }
        }
        
        _uiState.value = _uiState.value.copy(pomodoroPresets = repository.getPomodoroPresets())
    }

    fun selectFilter(filter: TaskFilter) {
        val filterTitle = when (filter) {
            is TaskFilter.All -> "Todas"
            is TaskFilter.Today -> "Hoje"
            is TaskFilter.Category -> {
                _uiState.value.categories.find { it.id == filter.categoryId }?.name ?: "Categoria"
            }
        }
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            filterTitle = filterTitle
        )
    }
    
    fun refreshCategories() {
        viewModelScope.launch {
            repository.getCategoriesFlow().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    private fun updateTaskLists(allTasks: List<Task> = emptyList()) {
        val filter = _uiState.value.selectedFilter
        
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

        val overdue = filteredTasks
            .filter { it.isOverdue && !it.isCompleted }
            .sortedBy { it.dateTime }

        val todayList = filteredTasks
            .filter { 
                it.dateTime.toLocalDate() == today && !it.isCompleted 
            }
            .sortedBy { it.dateTime }

        val tomorrowList = filteredTasks
            .filter { 
                it.dateTime.toLocalDate() == tomorrow && !it.isCompleted 
            }
            .sortedBy { it.dateTime }

        val laterList = filteredTasks
            .filter { 
                it.dateTime.toLocalDate().isAfter(tomorrow) && !it.isCompleted 
            }
            .sortedBy { it.dateTime }

        val completed = filteredTasks
            .filter {
                it.isCompleted &&
                it.completedAt?.toLocalDate() == today
            }
            .sortedByDescending { it.completedAt }
        
        _uiState.value = _uiState.value.copy(
            overdueTasks = overdue,
            todayTasks = todayList,
            tomorrowTasks = tomorrowList,
            laterTasks = laterList,
            completedTasks = completed
        )
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
}