package com.example.tasktimer.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktimer.model.CalendarDay
import com.example.tasktimer.model.Task
import com.example.tasktimer.ui.theme.*

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {}
) {
    val calendarDays by viewModel.calendarDays.collectAsState()
    val tasksForSelectedDate by viewModel.tasksForSelectedDate.collectAsState()
    val monthYearText by viewModel.monthYearText.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = { CalendarTopBar() },
        bottomBar = { 
            CalendarBottomBar(
                onHomeClick = onNavigateToHome,
                onSearchClick = onNavigateToSearch
            ) 
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(32.dp))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header: Nov, Hoje
            CalendarHeader(monthYearText = monthYearText)
            
            // Calendário Semanal com gesture de arraste
            WeekCalendar(
                days = calendarDays,
                onDayClick = { day -> viewModel.selectDay(day.dayOfMonth) },
                onSwipe = { direction -> viewModel.navigateWeek(direction) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista de Tasks
            TasksList(tasks = tasksForSelectedDate)
        }
    }
}

@Composable
fun CalendarTopBar() {
    Text(
        text = "Calendário",
        color = TextWhite,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
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
    var dragOffset by remember { mutableStateOf(0f) }
    
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
            Text(
                text = day.dayOfMonth.toString(),
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = if (day.isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun TasksList(tasks: List<Task>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hoje",
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column {
                tasks.forEach { task ->
                    CalendarTaskItem(task)
                }
            }
        }
    }
}

@Composable
fun CalendarTaskItem(task: Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = {},
                colors = CheckboxDefaults.colors(
                    uncheckedColor = TextGray,
                    checkedColor = PrimaryBlue
                )
            )
            Text(
                text = task.title,
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Text(
            text = task.timeOrDate,
            color = task.timeColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CalendarBottomBar(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    NavigationBar(
        containerColor = SurfaceDark,
        contentColor = TextGray
    ) {
        NavigationBarItem(
            icon = { Icon(androidx.compose.material.icons.Icons.Default.Check, contentDescription = null) },
            label = { Text("Tasks") },
            selected = false,
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
        NavigationBarItem(
            icon = { Icon(androidx.compose.material.icons.Icons.Default.DateRange, contentDescription = null) },
            label = { Text("Calendário") },
            selected = true,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = TextWhite,
                selectedTextColor = TextWhite,
                indicatorColor = SelectedNav,
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
        NavigationBarItem(
            icon = { Icon(androidx.compose.material.icons.Icons.Default.Search, contentDescription = null) },
            label = { Text("Pesquisar") },
            selected = false,
            onClick = onSearchClick,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
    }
}
