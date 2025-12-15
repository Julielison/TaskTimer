package com.example.tasktimer.ui.calendar

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktimer.model.CalendarDay
import com.example.tasktimer.model.Task
import com.example.tasktimer.ui.components.AddTaskDialog
import com.example.tasktimer.ui.theme.DarkBackground
import com.example.tasktimer.ui.theme.PrimaryBlue
import com.example.tasktimer.ui.theme.SurfaceDark
import com.example.tasktimer.ui.theme.TextGray
import com.example.tasktimer.ui.theme.TextWhite
import java.time.LocalDate

@Composable
fun CalendarContent(
    viewModel: CalendarViewModel = viewModel(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val calendarDays: List<CalendarDay> by viewModel.calendarDays.collectAsState()
    val tasksForSelectedDate by viewModel.tasksForSelectedDate.collectAsState()
    val monthYearText by viewModel.monthYearText.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val pomodoroPresets by viewModel.pomodoroPresets.collectAsState()
    val scrollState = rememberScrollState()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    Scaffold(
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            CalendarHeader(monthYearText = monthYearText)
            
            WeekCalendar(
                days = calendarDays,
                onDayClick = { day -> viewModel.selectDay(day.dayOfMonth) },
                onSwipe = { direction -> viewModel.navigateWeek(direction) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TasksList(
                tasks = tasksForSelectedDate,
                selectedDate = selectedDate,
                onTaskClick = { task -> taskToEdit = task },
                onTaskToggle = { taskId -> viewModel.toggleTaskCompletion(taskId) }
            )
        }
    }

    // Dialog de adicionar/editar tarefa
    if (showAddTaskDialog || taskToEdit != null) {
        AddTaskDialog(
            onDismiss = { 
                showAddTaskDialog = false
                taskToEdit = null
            },
            onSave = { title, description, dateTime, categoryId, subtasks, pomodoroConfig ->
                if (taskToEdit != null) {
                    viewModel.updateTask(
                        taskToEdit!!.id,
                        title,
                        description,
                        dateTime,
                        categoryId,
                        subtasks,
                        pomodoroConfig
                    )
                } else {
                    viewModel.addTask(title, description, dateTime, categoryId, subtasks, pomodoroConfig)
                }
                showAddTaskDialog = false
                taskToEdit = null
            },
            categories = categories,
            pomodoroPresets = pomodoroPresets,
            initialDate = selectedDate,
            existingTask = taskToEdit
        )
    }
}

@Composable
fun CalendarHeader(monthYearText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = monthYearText,
            color = TextWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun WeekCalendar(
    days: List<CalendarDay>,
    onDayClick: (CalendarDay) -> Unit,
    onSwipe: (Int) -> Unit
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            dragOffset > 100 -> onSwipe(-1) // Swipe right -> semana anterior
                            dragOffset < -100 -> onSwipe(1) // Swipe left -> próxima semana
                        }
                        dragOffset = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            },
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        days.forEach { day ->
            CalendarDayItem(
                day = day,
                onClick = { onDayClick(day) }
            )
        }
    }
}

@Composable
fun CalendarDayItem(
    day: CalendarDay,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = day.dayOfWeek,
            color = if (day.isSelected) TextWhite else TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = when {
                        day.isSelected -> PrimaryBlue
                        day.isToday -> SurfaceDark
                        else -> Color.Transparent
                    },
                    shape = CircleShape
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = day.dayOfMonth.toString(),
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = if (day.isSelected) FontWeight.Bold else FontWeight.Normal
                )
                
                // Indicador de tasks
                if (day.taskCount > 0 && !day.isSelected) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(
                                color = if (day.hasOverdueTasks) Color(0xFFD32F2F) 
                                       else if (day.completionPercentage == 1f) Color(0xFF4CAF50)
                                       else PrimaryBlue,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun TasksList(
    tasks: List<Task>,
    selectedDate: LocalDate,
    onTaskClick: (Task) -> Unit,
    onTaskToggle: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val today = LocalDate.now()
            val title = when (selectedDate) {
                today -> "Hoje"
                today.plusDays(1) -> "Amanhã"
                today.minusDays(1) -> "Ontem"
                else -> "" // Não mostra título para outras datas
            }
            
            if (title.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    
                    if (tasks.isNotEmpty()) {
                        Text(
                            text = "${tasks.count { it.isCompleted }}/${tasks.size}",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            } else if (tasks.isNotEmpty()) {
                // Para outras datas, mostra apenas o contador
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${tasks.count { it.isCompleted }}/${tasks.size}",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (tasks.isEmpty()) {
                Text(
                    text = "Nenhuma tarefa para este dia",
                    color = TextGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                Column {
                    tasks.forEach { task ->
                        CalendarTaskItem(
                            task = task,
                            onClick = { onTaskClick(task) },
                            onToggle = { onTaskToggle(task.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarTaskItem(
    task: Task,
    onClick: () -> Unit,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    uncheckedColor = TextGray,
                    checkedColor = PrimaryBlue
                )
            )
            
            Column {
                Text(
                    text = task.title,
                    color = if (task.isCompleted) TextGray.copy(alpha = 0.6f) else TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                
                if (!task.description.isNullOrBlank()) {
                    Text(
                        text = task.description,
                        color = if (task.isCompleted) TextGray.copy(alpha = 0.4f) else TextGray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                
                // Mostra subtasks se houver
                if (task.subtasks.isNotEmpty()) {
                    Text(
                        text = "${task.subtasks.count { it.isCompleted }}/${task.subtasks.size} subtarefas",
                        color = if (task.isCompleted) TextGray.copy(alpha = 0.4f) else TextGray,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = task.formattedTime,
                color = when {
                    task.isCompleted -> TextGray.copy(alpha = 0.5f)
                    task.isOverdue -> Color(0xFFD32F2F)
                    task.isTimePassed -> Color(0xFFFFD600) // Amarelo quando o horário passou
                    else -> PrimaryBlue
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Indicador de pomodoro
            if (task.pomodoroConfig != null) {
                Text(
                    text = "🍅 ${task.pomodoroConfig.totalPomodoros}x",
                    color = if (task.isCompleted) TextGray.copy(alpha = 0.4f) else TextGray,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
