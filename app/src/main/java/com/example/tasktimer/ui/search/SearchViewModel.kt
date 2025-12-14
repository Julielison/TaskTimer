package com.example.tasktimer.ui.search

import androidx.lifecycle.ViewModel
import com.example.tasktimer.data.MockTaskRepository
import com.example.tasktimer.model.Category
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Subtask
import com.example.tasktimer.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class SearchViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedCategoryIds: StateFlow<Set<Int>> = _selectedCategoryIds.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Task>>(emptyList())
    val searchResults: StateFlow<List<Task>> = _searchResults.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _pomodoroPresets = MutableStateFlow<List<Pair<String, PomodoroConfig>>>(emptyList())
    val pomodoroPresets: StateFlow<List<Pair<String, PomodoroConfig>>> = _pomodoroPresets.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _categories.value = MockTaskRepository.getCategories()
        _pomodoroPresets.value = MockTaskRepository.getPomodoroPresets()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleCategory(categoryId: Int) {
        val currentIds = _selectedCategoryIds.value.toMutableSet()
        if (currentIds.contains(categoryId)) {
            currentIds.remove(categoryId)
        } else {
            currentIds.add(categoryId)
        }
        _selectedCategoryIds.value = currentIds
    }

    fun removeCategory(categoryId: Int) {
        _selectedCategoryIds.value = _selectedCategoryIds.value - categoryId
    }

    fun performSearch() {
        val query = _searchQuery.value.trim()
        val categoryIds = _selectedCategoryIds.value

        if (query.isEmpty() && categoryIds.isEmpty()) {
            _searchResults.value = emptyList()
            return
        }

        val allTasks = MockTaskRepository.getAllTasks()

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

    fun toggleTaskCompletion(taskId: Int) {
        MockTaskRepository.toggleTaskCompletion(taskId)
        performSearch() // Atualiza resultados
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
        performSearch()
    }
}
