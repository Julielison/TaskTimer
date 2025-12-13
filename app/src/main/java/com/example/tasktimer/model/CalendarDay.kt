package com.example.tasktimer.model

import java.time.LocalDate

data class CalendarDay(
    val dayOfMonth: Int,
    val dayOfWeek: String,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
    val fullDate: LocalDate,
    val taskCount: Int = 0,
    val completedTaskCount: Int = 0,
    val hasOverdueTasks: Boolean = false
) {
    val completionPercentage: Float
        get() = if (taskCount > 0) completedTaskCount.toFloat() / taskCount else 0f
}
