package com.example.tasktimer.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktimer.data.RoomRepository
import com.example.tasktimer.model.CalendarDay
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Subtask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

class CalendarViewModel(
    private val repository: RoomRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val _currentWeekStart = MutableStateFlow<LocalDate>(getWeekStart(LocalDate.now()))

    init {
        loadData()
        loadWeekDays(_currentWeekStart.value)
        selectDate(LocalDate.now())
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            repository.getTasksFlow().collect {
                loadWeekDays(_currentWeekStart.value)
                loadTasksForDate(_uiState.value.selectedDate)
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getCategoriesFlow().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
        _uiState.value = _uiState.value.copy(pomodoroPresets = repository.getPomodoroPresets())
    }

    private fun getWeekStart(date: LocalDate): LocalDate {
        val weekFields = WeekFields.of(Locale.getDefault())
        return date.with(weekFields.dayOfWeek(), 1)
    }

    private fun loadWeekDays(weekStart: LocalDate) {
        viewModelScope.launch {
            val today = LocalDate.now()
            val days = mutableListOf<CalendarDay>()
            
            for (i in 0..6) {
                val date = weekStart.plusDays(i.toLong())
                val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("pt-BR"))
                
                val tasksForDay = repository.getTasksByDate(date)
                val completedCount = tasksForDay.count { it.isCompleted }
                val hasOverdue = tasksForDay.any { it.isOverdue }
                
                days.add(
                    CalendarDay(
                        dayOfMonth = date.dayOfMonth,
                        dayOfWeek = dayOfWeek.take(3).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                        isToday = date == today,
                        isSelected = date == _uiState.value.selectedDate,
                        fullDate = date,
                        taskCount = tasksForDay.size,
                        completedTaskCount = completedCount,
                        hasOverdueTasks = hasOverdue
                    )
                )
            }
            
            _uiState.value = _uiState.value.copy(calendarDays = days)
            updateMonthYearText(weekStart)
        }
    }

    private fun updateMonthYearText(weekStart: LocalDate) {
        val monthName = weekStart.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR"))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val year = weekStart.year
        
        _uiState.value = _uiState.value.copy(monthYearText = "$monthName de $year")
    }

    fun selectDay(dayOfMonth: Int) {
        val date = _uiState.value.calendarDays.find { it.dayOfMonth == dayOfMonth }?.fullDate
        date?.let { selectDate(it) }
    }

    private fun selectDate(date: LocalDate) {
        val updatedDays = _uiState.value.calendarDays.map {
            it.copy(isSelected = it.fullDate == date)
        }
        
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            calendarDays = updatedDays
        )
        
        loadTasksForDate(date)
    }

    fun navigateWeek(direction: Int) {
        val newWeekStart = _currentWeekStart.value.plusWeeks(direction.toLong())
        _currentWeekStart.value = newWeekStart
        loadWeekDays(newWeekStart)
        
        // Se a data selecionada não está na nova semana, seleciona o primeiro dia
        val selectedInNewWeek = _uiState.value.calendarDays.any { it.fullDate == _uiState.value.selectedDate }
        if (!selectedInNewWeek) {
            selectDate(newWeekStart)
        }
    }

    private fun loadTasksForDate(date: LocalDate) {
        viewModelScope.launch {
            val tasks = repository.getTasksByDate(date).sortedBy { it.dateTime }
            _uiState.value = _uiState.value.copy(tasksForSelectedDate = tasks)
        }
    }

    fun toggleTaskCompletion(taskId: String) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(taskId)
        }
    }

    fun addTask(
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: String?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ) {
        viewModelScope.launch {
            repository.addTask(title, description, dateTime, categoryId, subtasks, pomodoroConfig)
        }
    }

    fun updateTask(
        taskId: String,
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: String?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ) {
        viewModelScope.launch {
            repository.updateTask(taskId, title, description, dateTime, categoryId, subtasks, pomodoroConfig)
        }
    }
}
