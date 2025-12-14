package com.example.tasktimer.ui.home

import androidx.lifecycle.ViewModel
import com.example.tasktimer.model.Task
import com.example.tasktimer.model.Category
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Subtask
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

    private val _completedTasks = MutableStateFlow<List<Task>>(emptyList())
    val completedTasks: StateFlow<List<Task>> = _completedTasks.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _pomodoroPresets = MutableStateFlow<List<Pair<String, PomodoroConfig>>>(emptyList())
    val pomodoroPresets: StateFlow<List<Pair<String, PomodoroConfig>>> = _pomodoroPresets.asStateFlow()

    // Simulação de todas as tasks
    private val allTasks = mutableListOf<Task>()
    private var nextTaskId = 8 // Próximo ID disponível
    private var nextSubtaskId = 1

    init {
        loadMockData()
        loadMockCategories()
        loadPomodoroPresets()
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

    private fun loadMockCategories() {
        _categories.value = listOf(
            Category(1, "Trabalho", androidx.compose.ui.graphics.Color(0xFF4285F4)),
            Category(2, "Desenvolvimento", androidx.compose.ui.graphics.Color(0xFF34A853)),
            Category(3, "Pessoal", androidx.compose.ui.graphics.Color(0xFFEA4335)),
            Category(4, "Estudos", androidx.compose.ui.graphics.Color(0xFFFBBC04)),
            Category(5, "Saúde", androidx.compose.ui.graphics.Color(0xFF9C27B0))
        )
    }

    private fun loadPomodoroPresets() {
        _pomodoroPresets.value = listOf(
            "Clássico" to PomodoroConfig(25, 5, 15, 4, 4),
            "Curto" to PomodoroConfig(15, 3, 10, 4, 6),
            "Longo" to PomodoroConfig(50, 10, 30, 2, 4),
            "Intenso" to PomodoroConfig(90, 20, 30, 3, 3)
        )
    }

    private fun updateTaskLists() {
        val today = LocalDate.now()

        // Tasks vencidas (não concluídas e anteriores a hoje)
        _overdueTasks.value = allTasks
            .filter { it.isOverdue && !it.isCompleted }
            .sortedBy { it.dateTime }

        // Tasks de hoje (não concluídas)
        _todayTasks.value = allTasks
            .filter { it.dateTime.toLocalDate() == today && !it.isCompleted }
            .sortedBy { it.dateTime }

        // Tasks concluídas hoje (independente da data original da task)
        _completedTasks.value = allTasks
            .filter {
                it.isCompleted &&
                it.completedAt?.toLocalDate() == today
            }
            .sortedByDescending { it.completedAt }
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

    fun updateTask(
        taskId: Int,
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: Int?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ) {
        val taskIndex = allTasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            val originalTask = allTasks[taskIndex]

            val finalSubtasks = subtasks.map { subtask ->
                if (subtask.id > 1000) { // Novo subtask
                    subtask.copy(
                        id = nextSubtaskId++,
                        taskId = taskId
                    )
                } else {
                    subtask.copy(taskId = taskId)
                }
            }

            allTasks[taskIndex] = originalTask.copy(
                title = title,
                description = description,
                dateTime = dateTime,
                categoryId = categoryId,
                subtasks = finalSubtasks,
                pomodoroConfig = pomodoroConfig
            )

            updateTaskLists()
        }
    }

    fun getTaskById(taskId: Int): Task? {
        return allTasks.find { it.id == taskId }
    }

    fun addTask(
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: Int?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ) {
        val taskId = nextTaskId++

        val finalSubtasks = subtasks.map { subtask ->
            subtask.copy(
                id = nextSubtaskId++,
                taskId = taskId
            )
        }

        val newTask = Task(
            id = taskId,
            title = title,
            description = description,
            dateTime = dateTime,
            categoryId = categoryId,
            subtasks = finalSubtasks,
            pomodoroConfig = pomodoroConfig
        )

        allTasks.add(newTask)
        updateTaskLists()
    }
}