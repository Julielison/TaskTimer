package com.example.tasktimer.ui.home

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktimer.model.Task
import com.example.tasktimer.ui.components.AddTaskDialog
import com.example.tasktimer.ui.components.DrawerContent
import com.example.tasktimer.ui.theme.DarkBackground
import com.example.tasktimer.ui.theme.PrimaryBlue
import com.example.tasktimer.ui.theme.SurfaceDark
import com.example.tasktimer.ui.theme.TextGray
import com.example.tasktimer.ui.theme.TextWhite
import kotlinx.coroutines.launch

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel()
) {
    val overdueTasks by viewModel.overdueTasks.collectAsState()
    val todayTasks by viewModel.todayTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val pomodoroPresets by viewModel.pomodoroPresets.collectAsState()
    val filterTitle by viewModel.filterTitle.collectAsState()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    val scrollState = rememberScrollState()
    var isOverdueExpanded by remember { mutableStateOf(true) }
    var isTodayExpanded by remember { mutableStateOf(true) }
    var isCompletedExpanded by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            DrawerContent(
                categories = categories,
                selectedFilter = viewModel.selectedFilter.collectAsState().value,
                onFilterSelected = { filter ->
                    viewModel.selectFilter(filter)
                    scope.launch { drawerState.close() }
                }   
            )
        }
    ) {
        Scaffold(
            containerColor = DarkBackground,
            topBar = { 
                HomeTopBar(
                    title = filterTitle,
                    onMenuClick = { 
                        scope.launch { drawerState.open() } 
                    }
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
            },
            modifier = modifier
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
fun HomeTopBar(
    title: String,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = TextWhite,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }
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