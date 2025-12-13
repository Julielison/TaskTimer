package com.example.tasktimer.model

import java.time.LocalDate

data class CalendarDay(
    val dayOfMonth: Int,
    val dayOfWeek: String,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
    val fullDate: LocalDate
)
