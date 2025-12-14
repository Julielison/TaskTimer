package com.example.tasktimer.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Info // Trocado Assessment por Info (nativo)
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktimer.model.Task
import com.example.tasktimer.ui.theme.*
import com.example.tasktimer.ui.components.AddTaskDialog

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToCalendar: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {}
) {
    val overdueTasks by viewModel.overdueTasks.collectAsState()
    val todayTasks by viewModel.todayTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val pomodoroPresets by viewModel.pomodoroPresets.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    // Estado para rolagem da tela inteira
    val scrollState = rememberScrollState()

    var isOverdueExpanded by remember { mutableStateOf(true) }
    var isTodayExpanded by remember { mutableStateOf(true) }
    var isCompletedExpanded by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = DarkBackground,
        topBar = { HomeTopBar() },
        bottomBar = { 
            HomeBottomBar(
                onCalendarClick = onNavigateToCalendar,
                onSearchClick = onNavigateToSearch
            ) 
        },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Vencidas
            if (overdueTasks.isNotEmpty()) {
                TaskSection(
                    title = "Vencidas",
                    taskCount = overdueTasks.size,
                    tasks = overdueTasks,
                    isExpanded = isOverdueExpanded,
                    onToggleExpand = { isOverdueExpanded = !isOverdueExpanded },
                    onTaskClick = { task -> taskToEdit = task },
                    onTaskToggle = { taskId -> viewModel.toggleTaskCompletion(taskId) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Hoje
            if (todayTasks.isNotEmpty()) {
                TaskSection(
                    title = "Hoje",
                    taskCount = todayTasks.size,
                    tasks = todayTasks,
                    isExpanded = isTodayExpanded,
                    onToggleExpand = { isTodayExpanded = !isTodayExpanded },
                    onTaskClick = { task -> taskToEdit = task },
                    onTaskToggle = { taskId -> viewModel.toggleTaskCompletion(taskId) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Concluídas
            if (completedTasks.isNotEmpty()) {
                TaskSection(
                    title = "Concluídas",
                    taskCount = completedTasks.size,
                    tasks = completedTasks,
                    isExpanded = isCompletedExpanded,
                    onToggleExpand = { isCompletedExpanded = !isCompletedExpanded },
                    onTaskClick = { task -> taskToEdit = task },
                    onTaskToggle = { taskId -> viewModel.toggleTaskCompletion(taskId) }
                )
            }
            
            // Mensagem quando não há tasks
            if (overdueTasks.isEmpty() && todayTasks.isEmpty() && completedTasks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🎉",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhuma tarefa para hoje",
                            color = TextGray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Clique no + para adicionar uma nova tarefa",
                            color = TextGray.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
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
            existingTask = taskToEdit
        )
    }
}

@Composable
fun TaskSection(
    title: String,
    taskCount: Int,
    tasks: List<Task>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onTaskToggle: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
                    .padding(bottom = if (isExpanded) 8.dp else 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = TextGray.copy(alpha = 0.2f),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            text = taskCount.toString(),
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Recolher" else "Expandir",
                    tint = TextGray,
                    modifier = Modifier.size(24.dp)
                )
            }

            if (isExpanded) {
                Column {
                    tasks.forEach { task ->
                        TaskItem(
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
fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = TextWhite,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Hoje",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
        // Usei Icons.Default.Info para garantir compatibilidade
        // Se quiser Assessment, adicione a dependência: material-icons-extended
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Stats",
            tint = TextWhite,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun TaskItem(
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
            }
        }
        
        Text(
            text = if (task.isOverdue) task.formattedDate else task.formattedTime,
            color = when {
                task.isCompleted -> TextGray.copy(alpha = 0.5f)
                task.isOverdue -> Color(0xFFD32F2F)
                task.isTimePassed -> Color(0xFFFFD600) // Amarelo quando o horário passou
                else -> PrimaryBlue
            },
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HomeBottomBar(
    onCalendarClick: () -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = SurfaceDark,
        contentColor = TextGray
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Check, contentDescription = null) },
            label = { Text("Tasks") },
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
            icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            label = { Text("Calendário") },
            selected = false,
            onClick = onCalendarClick,
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
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