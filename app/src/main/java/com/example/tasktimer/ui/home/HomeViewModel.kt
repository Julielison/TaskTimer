package com.example.tasktimer.ui.home

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.tasktimer.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _overdueTasks = MutableStateFlow<List<Task>>(emptyList())
    val overdueTasks: StateFlow<List<Task>> = _overdueTasks.asStateFlow()

    private val _todayTasks = MutableStateFlow<List<Task>>(emptyList())
    val todayTasks: StateFlow<List<Task>> = _todayTasks.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        _overdueTasks.value = listOf(
            Task(1, "Task1", "12 Nov", false, true, Color(0xFFD32F2F)),
            Task(2, "Task1", "12 Nov", false, true, Color(0xFFD32F2F)),
            Task(3, "Task1", "12 Nov", false, true, Color(0xFFD32F2F)),
            Task(4, "Task1", "12 Nov", false, true, Color(0xFFD32F2F))
        )

        _todayTasks.value = listOf(
            Task(5, "Task1", "6:00", false, false, Color(0xFFFFD600)),
            Task(6, "Task1", "7:00", false, false, Color(0xFFFFD600)),
            Task(7, "Task1", "8:00", false, false, Color(0xFF4285F4)),
            Task(8, "Task1", "9:00", false, false, Color(0xFF4285F4))
        )
    }

    fun toggleTaskCompletion(taskId: Int) {
        // Lógica de update
    }
}