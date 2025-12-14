package com.example.tasktimer.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktimer.model.Task
import com.example.tasktimer.ui.components.AddTaskDialog
import com.example.tasktimer.ui.theme.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    onNavigateToHome: () -> Unit = {},
    onNavigateToCalendar: () -> Unit = {}
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val pomodoroPresets by viewModel.pomodoroPresets.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            SearchTopBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onSearch = { viewModel.performSearch() },
                onFilterClick = { showFilterDialog = true },
                hasActiveFilter = selectedCategoryId != null
            )
        },
        bottomBar = {
            SearchBottomBar(
                onHomeClick = onNavigateToHome,
                onCalendarClick = onNavigateToCalendar
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
            // Categoria selecionada
            if (selectedCategoryId != null) {
                val category = categories.find { it.id == selectedCategoryId }
                category?.let {
                    FilterChip(
                        category = it,
                        onRemove = { viewModel.selectCategory(null) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Resultados
            if (searchResults.isNotEmpty()) {
                Text(
                    text = "${searchResults.size} resultado${if (searchResults.size > 1) "s" else ""} encontrado${if (searchResults.size > 1) "s" else ""}",
                    color = TextGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                searchResults.forEach { task ->
                    SearchTaskItem(
                        task = task,
                        onClick = { taskToEdit = task },
                        onToggle = { viewModel.toggleTaskCompletion(task.id) },
                        categories = categories
                    )
                }
            } else if (searchQuery.isNotEmpty() || selectedCategoryId != null) {
                // Mensagem quando não há resultados
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhuma tarefa encontrada",
                            color = TextGray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Mensagem inicial
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🔍", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pesquise suas tarefas",
                            color = TextGray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Digite o nome ou use o filtro",
                            color = TextGray.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // Diálogo de filtro
    if (showFilterDialog) {
        FilterDialog(
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onDismiss = { showFilterDialog = false },
            onCategorySelected = { categoryId ->
                viewModel.selectCategory(categoryId)
                showFilterDialog = false
            }
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onFilterClick: () -> Unit,
    hasActiveFilter: Boolean
) {
    Surface(
        color = SurfaceDark,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Pesquisar tarefas...", color = TextGray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextGray)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpar", tint = TextGray)
                        }
                    }
                },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = TextGray,
                    cursorColor = PrimaryBlue
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearch() })
            )

            Badge(
                containerColor = if (hasActiveFilter) PrimaryBlue else Color.Transparent
            ) {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtrar",
                        tint = if (hasActiveFilter) PrimaryBlue else TextWhite
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    category: com.example.tasktimer.model.Category,
    onRemove: () -> Unit
) {
    Surface(
        color = category.color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(category.color, CircleShape)
            )
            Text(
                text = category.name,
                color = TextWhite,
                fontSize = 14.sp
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remover filtro",
                    tint = TextWhite,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun FilterDialog(
    categories: List<com.example.tasktimer.model.Category>,
    selectedCategoryId: Int?,
    onDismiss: () -> Unit,
    onCategorySelected: (Int?) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar por categoria", color = TextWhite) },
        text = {
            Column {
                // Opção "Todas"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategorySelected(null) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedCategoryId == null,
                        onClick = { onCategorySelected(null) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = PrimaryBlue,
                            unselectedColor = TextGray
                        )
                    )
                    Text(
                        text = "Todas as categorias",
                        color = TextWhite,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Categorias
                categories.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCategorySelected(category.id) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategoryId == category.id,
                            onClick = { onCategorySelected(category.id) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = PrimaryBlue,
                                unselectedColor = TextGray
                            )
                        )
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .padding(start = 8.dp)
                                .background(category.color, CircleShape)
                        )
                        Text(
                            text = category.name,
                            color = TextWhite,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fechar", color = PrimaryBlue)
            }
        },
        containerColor = SurfaceDark
    )
}

@Composable
fun SearchTaskItem(
    task: Task,
    onClick: () -> Unit,
    onToggle: () -> Unit,
    categories: List<com.example.tasktimer.model.Category>
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    uncheckedColor = TextGray,
                    checkedColor = PrimaryBlue
                )
            )

            Column(modifier = Modifier.weight(1f)) {
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
                        maxLines = 2,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Data
                    Text(
                        text = task.formattedDate,
                        color = when {
                            task.isCompleted -> TextGray.copy(alpha = 0.5f)
                            task.isOverdue -> Color(0xFFD32F2F)
                            else -> PrimaryBlue
                        },
                        fontSize = 11.sp
                    )

                    // Categoria
                    task.categoryId?.let { categoryId ->
                        categories.find { it.id == categoryId }?.let { category ->
                            Surface(
                                color = category.color.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(category.color, CircleShape)
                                    )
                                    Text(
                                        text = category.name,
                                        color = TextWhite,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBottomBar(
    onHomeClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    NavigationBar(
        containerColor = SurfaceDark,
        contentColor = TextGray
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Check, contentDescription = null) },
            label = { Text("Tasks") },
            selected = false,
            onClick = onHomeClick,
            colors = NavigationBarItemDefaults.colors(
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
    }
}
