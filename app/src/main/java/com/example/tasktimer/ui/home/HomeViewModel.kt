package com.example.tasktimer.ui.home

import androidx.lifecycle.ViewModel
import com.example.tasktimer.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalDateTime

class HomeViewModel : ViewModel() {

    private val _overdueTasks = MutableStateFlow<List<Task>>(emptyList())
    val overdueTasks: StateFlow<List<Task>> = _overdueTasks.asStateFlow()

    private val _todayTasks = MutableStateFlow<List<Task>>(emptyList())
    val todayTasks: StateFlow<List<Task>> = _todayTasks.asStateFlow()

    // Simulação de todas as tasks
    private val allTasks = mutableListOf<Task>()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        // Tasks vencidas
        allTasks.addAll(
            listOf(
                Task(
                    1, "Entregar relatório", "Relatório mensal de vendas",
                    LocalDateTime.of(yesterday.year, yesterday.month, yesterday.dayOfMonth, 14, 0),
                    categoryId = 1
                ),
                Task(
                    2, "Ligar para cliente", null,
                    LocalDateTime.of(yesterday.year, yesterday.month, yesterday.dayOfMonth, 16, 30),
                    categoryId = 1
                ),
                Task(
                    3, "Revisar código", "PR #456",
                    LocalDateTime.of(
                        today.minusDays(2).year, today.minusDays(2).month,
                        today.minusDays(2).dayOfMonth, 10, 0
                    ),
                    categoryId = 2
                )
            )
        )

        // Tasks de hoje
        allTasks.addAll(
            listOf(
                Task(
                    4, "Reunião de equipe", "Discutir projeto X",
                    LocalDateTime.of(today.year, today.month, today.dayOfMonth, 9, 0),
                    categoryId = 1
                ),
                Task(
                    5, "Desenvolver feature", null,
                    LocalDateTime.of(today.year, today.month, today.dayOfMonth, 14, 30),
                    categoryId = 2
                ),
                Task(
                    6, "Code review", "Revisar PR #123",
                    LocalDateTime.of(today.year, today.month, today.dayOfMonth, 16, 0),
                    categoryId = 2
                ),
                Task(
                    7, "Estudar Compose", "Navigation e States",
                    LocalDateTime.of(today.year, today.month, today.dayOfMonth, 19, 0),
                    isCompleted = true, categoryId = 4
                )
            )
        )

        updateTaskLists()
    }

    private fun updateTaskLists() {
        val today = LocalDate.now()

        _overdueTasks.value = allTasks
            .filter { it.isOverdue }
            .sortedBy { it.dateTime }

        _todayTasks.value = allTasks
            .filter { it.dateTime.toLocalDate() == today }
            .sortedBy { it.dateTime }
    }

    fun toggleTaskCompletion(taskId: Int) {
        val taskIndex = allTasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            val task = allTasks[taskIndex]
            allTasks[taskIndex] = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) LocalDateTime.now() else null
            )
            updateTaskLists()
        }
    }
}