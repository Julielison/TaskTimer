package com.example.tasktimer.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktimer.data.FirestoreRepository
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Subtask
import com.example.tasktimer.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class SearchViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())

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
                _allTasks.value = tasks
                performSearch()
            }
        }

        _uiState.value = _uiState.value.copy(pomodoroPresets = repository.getPomodoroPresets())
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun toggleCategory(categoryId: String) {
        val currentIds = _uiState.value.selectedCategoryIds.toMutableSet()
        if (currentIds.contains(categoryId)) {
            currentIds.remove(categoryId)
        } else {
            currentIds.add(categoryId)
        }
        _uiState.value = _uiState.value.copy(selectedCategoryIds = currentIds)
    }

    fun removeCategory(categoryId: String) {
        _uiState.value = _uiState.value.copy(
            selectedCategoryIds = _uiState.value.selectedCategoryIds - categoryId
        )
    }

    fun performSearch() {
        val query = _uiState.value.searchQuery.trim()
        val categoryIds = _uiState.value.selectedCategoryIds

        if (query.isEmpty() && categoryIds.isEmpty()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
            return
        }

        val allTasks = _allTasks.value

        val results = allTasks.filter { task ->
            val matchesQuery = if (query.isEmpty()) {
                true
            } else {
                task.title.contains(query, ignoreCase = true) ||
                task.description?.contains(query, ignoreCase = true) == true
            }

            val matchesCategory = if (categoryIds.isEmpty()) {
                true
            } else {
                task.categoryId in categoryIds
            }

            matchesQuery && matchesCategory
        }.sortedByDescending { it.dateTime }
        
        _uiState.value = _uiState.value.copy(searchResults = results)
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
}
