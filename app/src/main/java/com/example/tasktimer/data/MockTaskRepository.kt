package com.example.tasktimer.data

import com.example.tasktimer.model.Category
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Subtask
import com.example.tasktimer.model.Task
import java.time.LocalDate
import java.time.LocalDateTime

object MockTaskRepository {
    
    private val allTasks = mutableListOf<Task>()
    private var nextTaskId = 8
    private var nextSubtaskId = 1
    
    private val categories = mutableListOf(
        Category(1, "Trabalho", androidx.compose.ui.graphics.Color(0xFF4285F4)),
        Category(2, "Desenvolvimento", androidx.compose.ui.graphics.Color(0xFF34A853)),
        Category(3, "Pessoal", androidx.compose.ui.graphics.Color(0xFFEA4335)),
        Category(4, "Estudos", androidx.compose.ui.graphics.Color(0xFFFBBC04)),
        Category(5, "Saúde", androidx.compose.ui.graphics.Color(0xFF9C27B0))
    )
    private var nextCategoryId = 6
    
    private val pomodoroPresets = listOf(
        "Clássico" to PomodoroConfig(25, 5, 15, 4, 4),
        "Curto" to PomodoroConfig(15, 3, 10, 4, 6),
        "Longo" to PomodoroConfig(50, 10, 30, 2, 4),
        "Intenso" to PomodoroConfig(90, 20, 30, 3, 3)
    )
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
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
        
        // Tasks para amanhã
        val tomorrow = today.plusDays(1)
        allTasks.addAll(
            listOf(
                Task(
                    8, "Dentista", null,
                    LocalDateTime.of(tomorrow.year, tomorrow.month, tomorrow.dayOfMonth, 10, 0),
                    categoryId = 3
                ),
                Task(
                    9, "Estudar Kotlin", "Coroutines avançadas",
                    LocalDateTime.of(tomorrow.year, tomorrow.month, tomorrow.dayOfMonth, 19, 0),
                    categoryId = 4
                )
            )
        )
        
        // Tasks para daqui 2 dias
        val afterTomorrow = today.plusDays(2)
        allTasks.add(
            Task(
                10, "Academia", null,
                LocalDateTime.of(afterTomorrow.year, afterTomorrow.month, afterTomorrow.dayOfMonth, 7, 0),
                categoryId = 5
            )
        )
        
        nextTaskId = 11
    }
    
    // Getter para todas as tasks
    fun getAllTasks(): List<Task> = allTasks.toList()
    
    // Getter para categorias
    fun getCategories(): List<Category> = categories.toList()
    
    // Getter para presets de pomodoro
    fun getPomodoroPresets(): List<Pair<String, PomodoroConfig>> = pomodoroPresets
    
    // Buscar task por ID
    fun getTaskById(taskId: Int): Task? = allTasks.find { it.id == taskId }
    
    // Adicionar nova task
    fun addTask(
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: Int?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ): Task {
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
        return newTask
    }
    
    // Atualizar task existente
    fun updateTask(
        taskId: Int,
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: Int?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ): Boolean {
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
            
            return true
        }
        return false
    }
    
    // Toggle conclusão de task
    fun toggleTaskCompletion(taskId: Int): Boolean {
        val taskIndex = allTasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            val task = allTasks[taskIndex]
            allTasks[taskIndex] = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) LocalDateTime.now() else null
            )
            return true
        }
        return false
    }
    
    // Deletar task
    fun deleteTask(taskId: Int): Boolean {
        return allTasks.removeIf { it.id == taskId }
    }
    
    // Buscar tasks por data
    fun getTasksByDate(date: LocalDate): List<Task> {
        return allTasks.filter { it.dateTime.toLocalDate() == date }
    }
    
    // Buscar tasks vencidas
    fun getOverdueTasks(): List<Task> {
        return allTasks.filter { it.isOverdue && !it.isCompleted }
    }
    
    // Buscar tasks de hoje
    fun getTodayTasks(): List<Task> {
        val today = LocalDate.now()
        return allTasks.filter { it.dateTime.toLocalDate() == today && !it.isCompleted }
    }
    
    // Buscar tasks completadas hoje
    fun getCompletedTasksToday(): List<Task> {
        val today = LocalDate.now()
        return allTasks.filter { 
            it.isCompleted && 
            it.completedAt?.toLocalDate() == today 
        }
    }
    
    // Adicionar nova categoria
    fun addCategory(name: String, color: androidx.compose.ui.graphics.Color): Category {
        val newCategory = Category(
            id = nextCategoryId++,
            name = name,
            color = color
        )
        categories.add(newCategory)
        return newCategory
    }
    
    // Atualizar categoria
    fun updateCategory(categoryId: Int, name: String, color: androidx.compose.ui.graphics.Color): Boolean {
        val index = categories.indexOfFirst { it.id == categoryId }
        if (index != -1) {
            categories[index] = Category(categoryId, name, color)
            return true
        }
        return false
    }
    
    // Deletar categoria
    fun deleteCategory(categoryId: Int): Boolean {
        return categories.removeIf { it.id == categoryId }
    }
}
