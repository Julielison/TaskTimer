package com.example.tasktimer.model

import java.time.LocalDate

data class DashboardStats(
    val date: LocalDate,
    val completedTasksCount: Int,
    val totalPomodoroMinutes: Int,
    val tasksByCategory: Map<Int, Int>,
    val pomodorosByCategory: Map<Int, Int>
)

data class WeeklyStats(
    val weekStart: LocalDate,
    val dailyStats: List<DashboardStats>,
    val totalCompletedTasks: Int,
    val totalPomodoroHours: Float
) {
    val averageTasksPerDay: Float
        get() = totalCompletedTasks / 7f
    
    val averagePomodoroHoursPerDay: Float
        get() = totalPomodoroHours / 7f
}

data class CategoryStats(
    val categoryId: Int,
    val categoryName: String,
    val completedTasksCount: Int,
    val totalPomodoroMinutes: Int,
    val taskList: List<Task>
)
