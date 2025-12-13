package com.example.tasktimer.ui.calendar

import androidx.lifecycle.ViewModel
import com.example.tasktimer.model.CalendarDay
import com.example.tasktimer.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
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

    // Simulação de todas as tasks - em produção, vir do repositório
    private val allTasks = mutableListOf<Task>()

    init {
        loadMockTasks()
        loadWeekDays(_currentWeekStart.value)
        selectDate(LocalDate.now())
    }

    private fun loadMockTasks() {
        val today = LocalDate.now()
        
        // Tasks para hoje
        allTasks.addAll(
            listOf(
                Task(1, "Reunião de equipe", "Discutir projeto X", 
                    LocalDateTime.of(today.year, today.month, today.dayOfMonth, 9, 0), 
                    categoryId = 1),
                Task(2, "Desenvolver feature", null,
                    LocalDateTime.of(today.year, today.month, today.dayOfMonth, 14, 30),
                    categoryId = 2),
                Task(3, "Code review", "Revisar PR #123",
                    LocalDateTime.of(today.year, today.month, today.dayOfMonth, 16, 0),
                    isCompleted = true, categoryId = 2)
            )
        )
        
        // Tasks para amanhã
        val tomorrow = today.plusDays(1)
        allTasks.addAll(
            listOf(
                Task(4, "Dentista", null,
                    LocalDateTime.of(tomorrow.year, tomorrow.month, tomorrow.dayOfMonth, 10, 0),
                    categoryId = 3),
                Task(5, "Estudar Kotlin", "Coroutines avançadas",
                    LocalDateTime.of(tomorrow.year, tomorrow.month, tomorrow.dayOfMonth, 19, 0),
                    categoryId = 4)
            )
        )
        
        // Tasks para daqui 2 dias
        val afterTomorrow = today.plusDays(2)
        allTasks.add(
            Task(6, "Academia", null,
                LocalDateTime.of(afterTomorrow.year, afterTomorrow.month, afterTomorrow.dayOfMonth, 7, 0),
                categoryId = 5)
        )
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
            
            // Calcula estatísticas de tasks para o dia
            val tasksForDay = getTasksForDate(date)
            val completedCount = tasksForDay.count { it.isCompleted }
            val hasOverdue = tasksForDay.any { it.isOverdue }
            
            days.add(
                CalendarDay(
                    dayOfMonth = date.dayOfMonth,
                    dayOfWeek = dayOfWeek.take(3).capitalize(),
                    isToday = date == today,
                    isSelected = date == _selectedDate.value,
                    fullDate = date,
                    taskCount = tasksForDay.size,
                    completedTaskCount = completedCount,
                    hasOverdueTasks = hasOverdue
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
        _tasksForSelectedDate.value = getTasksForDate(date)
            .sortedBy { it.dateTime }
    }

    private fun getTasksForDate(date: LocalDate): List<Task> {
        return allTasks.filter { task ->
            task.dateTime.toLocalDate() == date
        }
    }

    fun toggleTaskCompletion(taskId: Int) {
        val taskIndex = allTasks.indexOfFirst { it.id == taskId }
        if (taskIndex != -1) {
            val task = allTasks[taskIndex]
            allTasks[taskIndex] = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) LocalDateTime.now() else null
            )
            
            // Recarrega as tasks para a data selecionada
            loadTasksForDate(_selectedDate.value)
            
            // Recarrega os dias da semana para atualizar as estatísticas
            loadWeekDays(_currentWeekStart.value)
        }
    }
}
