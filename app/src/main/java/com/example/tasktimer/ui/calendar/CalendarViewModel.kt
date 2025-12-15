package com.example.tasktimer.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktimer.data.FirestoreRepository
import com.example.tasktimer.model.CalendarDay
import com.example.tasktimer.model.Category
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Subtask
import com.example.tasktimer.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

class CalendarViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    private val _calendarDays = MutableStateFlow<List<CalendarDay>>(emptyList())
    val calendarDays: StateFlow<List<CalendarDay>> = _calendarDays.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _currentWeekStart = MutableStateFlow<LocalDate>(getWeekStart(LocalDate.now()))

    private val _tasksForSelectedDate = MutableStateFlow<List<Task>>(emptyList())
    val tasksForSelectedDate: StateFlow<List<Task>> = _tasksForSelectedDate.asStateFlow()

    private val _monthYearText = MutableStateFlow("")
    val monthYearText: StateFlow<String> = _monthYearText.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _pomodoroPresets = MutableStateFlow<List<Pair<String, PomodoroConfig>>>(emptyList())
    val pomodoroPresets: StateFlow<List<Pair<String, PomodoroConfig>>> = _pomodoroPresets.asStateFlow()

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
                loadTasksForDate(_selectedDate.value)
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getCategoriesFlow().collect { categories ->
                _categories.value = categories
            }
        }
        _pomodoroPresets.value = repository.getPomodoroPresets()
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
                val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("pt", "BR"))
                
                val tasksForDay = repository.getTasksByDate(date)
                val completedCount = tasksForDay.count { it.isCompleted }
                val hasOverdue = tasksForDay.any { it.isOverdue }
                
                days.add(
                    CalendarDay(
                        dayOfMonth = date.dayOfMonth,
                        dayOfWeek = dayOfWeek.take(3).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
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
    }

    private fun updateMonthYearText(weekStart: LocalDate) {
        val monthName = weekStart.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        val year = weekStart.year
        
        _monthYearText.value = "$monthName de $year"
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
        viewModelScope.launch {
            _tasksForSelectedDate.value = repository.getTasksByDate(date)
                .sortedBy { it.dateTime }
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
