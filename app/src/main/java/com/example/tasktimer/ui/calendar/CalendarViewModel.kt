package com.example.tasktimer.ui.calendar

import androidx.lifecycle.ViewModel
import com.example.tasktimer.data.MockTaskRepository
import com.example.tasktimer.model.CalendarDay
import com.example.tasktimer.model.Task
import com.example.tasktimer.model.Category
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.Subtask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.*
import java.util.Locale

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

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _pomodoroPresets = MutableStateFlow<List<Pair<String, PomodoroConfig>>>(emptyList())
    val pomodoroPresets: StateFlow<List<Pair<String, PomodoroConfig>>> = _pomodoroPresets.asStateFlow()

    init {
        loadData()
        loadWeekDays(_currentWeekStart.value)
        selectDate(LocalDate.now())
    }

    private fun loadData() {
        _categories.value = MockTaskRepository.getCategories()
        _pomodoroPresets.value = MockTaskRepository.getPomodoroPresets()
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
            val tasksForDay = MockTaskRepository.getTasksByDate(date)
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

    fun goToToday() {
        val today = LocalDate.now()
        val todayWeekStart = getWeekStart(today)
        
        if (_currentWeekStart.value != todayWeekStart) {
            _currentWeekStart.value = todayWeekStart
            loadWeekDays(todayWeekStart)
        }
        
        selectDate(today)
    }

    private fun loadTasksForDate(date: LocalDate) {
        _tasksForSelectedDate.value = MockTaskRepository.getTasksByDate(date)
            .sortedBy { it.dateTime }
    }

    fun toggleTaskCompletion(taskId: Int) {
        MockTaskRepository.toggleTaskCompletion(taskId)
        loadTasksForDate(_selectedDate.value)
        loadWeekDays(_currentWeekStart.value)
    }

    fun addTask(
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: Int?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ) {
        MockTaskRepository.addTask(title, description, dateTime, categoryId, subtasks, pomodoroConfig)
        
        if (dateTime.toLocalDate() == _selectedDate.value) {
            loadTasksForDate(_selectedDate.value)
        }
        loadWeekDays(_currentWeekStart.value)
    }

    fun updateTask(
        taskId: Int,
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        categoryId: Int?,
        subtasks: List<Subtask>,
        pomodoroConfig: PomodoroConfig?
    ) {
        MockTaskRepository.updateTask(taskId, title, description, dateTime, categoryId, subtasks, pomodoroConfig)
        loadTasksForDate(_selectedDate.value)
        loadWeekDays(_currentWeekStart.value)
    }

    fun getTaskById(taskId: Int): Task? {
        return MockTaskRepository.getTaskById(taskId)
    }
}
