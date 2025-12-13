package com.example.tasktimer.ui.calendar

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.tasktimer.model.CalendarDay
import com.example.tasktimer.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*

class CalendarViewModel : ViewModel() {

    private val _calendarDays = MutableStateFlow<List<CalendarDay>>(emptyList())
    val calendarDays: StateFlow<List<CalendarDay>> = _calendarDays.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _currentWeekStart = MutableStateFlow<LocalDate>(getWeekStart(LocalDate.now()))
    val currentWeekStart: StateFlow<LocalDate> = _currentWeekStart.asStateFlow()

    private val _tasksForSelectedDate = MutableStateFlow<List<Task>>(emptyList())
    val tasksForSelectedDate: StateFlow<List<Task>> = _tasksForSelectedDate.asStateFlow()

    private val _monthYearText = MutableStateFlow("")
    val monthYearText: StateFlow<String> = _monthYearText.asStateFlow()

    init {
        loadWeekDays(_currentWeekStart.value)
        selectDate(LocalDate.now())
    }

    private fun getWeekStart(date: LocalDate): LocalDate {
        val weekFields = WeekFields.of(Locale.getDefault())
        return date.with(weekFields.dayOfWeek(), 1)
    }

    private fun loadWeekDays(weekStart: LocalDate) {
        val today = LocalDate.now()
        val days = mutableListOf<CalendarDay>()
        
        for (i in 0..6) {
            val date = weekStart.plusDays(i.toLong())
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
            
            days.add(
                CalendarDay(
                    dayOfMonth = date.dayOfMonth,
                    dayOfWeek = dayOfWeek.take(3).capitalize(),
                    isToday = date == today,
                    isSelected = date == _selectedDate.value,
                    fullDate = date
                )
            )
        }
        
        _calendarDays.value = days
        updateMonthYearText(weekStart)
    }

    private fun updateMonthYearText(weekStart: LocalDate) {
        val today = LocalDate.now()
        val weekEnd = weekStart.plusDays(6)
        
        val monthName = weekStart.month.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
            .capitalize()
        
        val text = if (weekStart <= today && weekEnd >= today) {
            "$monthName, Hoje"
        } else {
            monthName
        }
        
        _monthYearText.value = text
    }

    fun selectDay(dayOfMonth: Int) {
        val date = _calendarDays.value.find { it.dayOfMonth == dayOfMonth }?.fullDate
        date?.let { selectDate(it) }
    }

    private fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        
        _calendarDays.value = _calendarDays.value.map {
            it.copy(isSelected = it.fullDate == date)
        }
        
        loadTasksForDate(date)
    }

    fun navigateWeek(direction: Int) {
        val newWeekStart = _currentWeekStart.value.plusWeeks(direction.toLong())
        _currentWeekStart.value = newWeekStart
        loadWeekDays(newWeekStart)
        
        // Se a data selecionada não está na nova semana, seleciona o primeiro dia
        val selectedInNewWeek = _calendarDays.value.any { it.fullDate == _selectedDate.value }
        if (!selectedInNewWeek) {
            selectDate(newWeekStart)
        }
    }

    private fun loadTasksForDate(date: LocalDate) {
        // Mock data - em produção, carregar do repositório baseado na data
        _tasksForSelectedDate.value = listOf(
            Task(1, "Task1", "6:00", false, false, Color(0xFFFFD600)),
            Task(2, "Task1", "7:00", false, false, Color(0xFFFFD600)),
            Task(3, "Task1", "8:00", false, false, Color(0xFF4285F4)),
            Task(4, "Task1", "9:00", false, false, Color(0xFF4285F4))
        )
    }
}
