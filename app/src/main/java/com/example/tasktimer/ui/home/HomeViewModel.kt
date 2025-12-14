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

    init {
        loadData()
    }

    private fun loadData() {
        _categories.value = MockTaskRepository.getCategories()
        _pomodoroPresets.value = MockTaskRepository.getPomodoroPresets()
        updateTaskLists()
    }

    private fun updateTaskLists() {
        _overdueTasks.value = MockTaskRepository.getOverdueTasks()
            .sortedBy { it.dateTime }

        _todayTasks.value = MockTaskRepository.getTodayTasks()
            .sortedBy { it.dateTime }

        _completedTasks.value = MockTaskRepository.getCompletedTasksToday()
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

    fun getTaskById(taskId: Int): Task? {
        return MockTaskRepository.getTaskById(taskId)
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