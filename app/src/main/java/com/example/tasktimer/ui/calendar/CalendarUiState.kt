package com.example.tasktimer.ui.calendar

import com.example.tasktimer.model.CalendarDay
import com.example.tasktimer.model.Category
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Task
import java.time.LocalDate

data class CalendarUiState(
    val calendarDays: List<CalendarDay> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val tasksForSelectedDate: List<Task> = emptyList(),
    val monthYearText: String = "",
    val categories: List<Category> = emptyList(),
    val pomodoroPresets: List<Pair<String, PomodoroConfig>> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
