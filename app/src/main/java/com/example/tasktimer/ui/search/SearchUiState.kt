package com.example.tasktimer.ui.search

import com.example.tasktimer.model.Category
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Task

data class SearchUiState(
    val searchQuery: String = "",
    val selectedCategoryIds: Set<String> = emptySet(),
    val searchResults: List<Task> = emptyList(),
    val categories: List<Category> = emptyList(),
    val pomodoroPresets: List<Pair<String, PomodoroConfig>> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
