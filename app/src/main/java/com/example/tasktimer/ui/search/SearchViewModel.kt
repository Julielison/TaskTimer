package com.example.tasktimer.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktimer.data.FirestoreRepository
import com.example.tasktimer.model.Category
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

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedCategoryIds: StateFlow<Set<String>> = _selectedCategoryIds.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Task>>(emptyList())
    val searchResults: StateFlow<List<Task>> = _searchResults.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _pomodoroPresets = MutableStateFlow<List<Pair<String, PomodoroConfig>>>(emptyList())
    val pomodoroPresets: StateFlow<List<Pair<String, PomodoroConfig>>> = _pomodoroPresets.asStateFlow()

    private val _allTasks = MutableStateFlow<List<Task>>(emptyList())

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
                _allTasks.value = tasks
                performSearch()
            }
        }

        _pomodoroPresets.value = repository.getPomodoroPresets()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleCategory(categoryId: String) {
        val currentIds = _selectedCategoryIds.value.toMutableSet()
        if (currentIds.contains(categoryId)) {
            currentIds.remove(categoryId)
        } else {
            currentIds.add(categoryId)
        }
        _selectedCategoryIds.value = currentIds
    }

    fun removeCategory(categoryId: String) {
        _selectedCategoryIds.value = _selectedCategoryIds.value - categoryId
    }

    fun performSearch() {
        val query = _searchQuery.value.trim()
        val categoryIds = _selectedCategoryIds.value

        if (query.isEmpty() && categoryIds.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }

        val allTasks = _allTasks.value

        _searchResults.value = allTasks.filter { task ->
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
