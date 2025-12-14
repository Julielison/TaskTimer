package com.example.tasktimer.model

import java.time.LocalDateTime

data class Task(
    val id: Int,
    val title: String,
    val description: String? = null,
    val dateTime: LocalDateTime,
    val isCompleted: Boolean = false,
    val categoryId: Int? = null,
    val pomodoroConfig: PomodoroConfig? = null,
    val subtasks: List<Subtask> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
) {
    val isOverdue: Boolean
        get() = !isCompleted && dateTime.toLocalDate().isBefore(java.time.LocalDate.now())

    val isTimePassed: Boolean
        get() {
            val now = LocalDateTime.now()
            return !isCompleted && 
                   dateTime.toLocalDate() == now.toLocalDate() && 
                   dateTime.isBefore(now)
        }

    val formattedTime: String
        get() = String.format("%02d:%02d", dateTime.hour, dateTime.minute)

    val formattedDate: String
        get() = String.format("%02d/%02d/%04d", dateTime.dayOfMonth, dateTime.monthValue, dateTime.year)
}