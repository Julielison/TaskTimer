package com.example.tasktimer.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.tasktimer.ui.theme.DarkBackground
import com.example.tasktimer.ui.theme.PrimaryBlue
import com.example.tasktimer.ui.theme.SurfaceDark
import com.example.tasktimer.ui.theme.TextGray
import com.example.tasktimer.ui.theme.TextWhite

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchContent(
    viewModel: SearchViewModel = viewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategoryIds by viewModel.selectedCategoryIds.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val pomodoroPresets by viewModel.pomodoroPresets.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
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
                selectedCategoriesCount = selectedCategoryIds.size
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Categorias selecionadas com FlowRow
            if (selectedCategoryIds.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedCategoryIds.forEach { categoryId ->
                        val category = categories.find { it.id == categoryId }
                        category?.let {
                            FilterChip(
                                category = it,
                                onRemove = { viewModel.removeCategory(categoryId) }
                            )
                        }
                    }
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
            } else if (searchQuery.isNotEmpty() || selectedCategoryIds.isNotEmpty()) {
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
            selectedCategoryIds = selectedCategoryIds,
            onDismiss = { showFilterDialog = false },
            onCategoryToggled = { categoryId ->
                viewModel.toggleCategory(categoryId)
            }
        )
    }

    // Dialog de editar tarefa
    if (taskToEdit != null) {
        AddTaskDialog(
            onDismiss = { taskToEdit = null },
            onSave = { title, description, dateTime, categoryId, subtasks, pomodoroConfig ->
                viewModel.updateTask(
                    taskToEdit!!.id,
                    title,
                    description,
                    dateTime,
                    categoryId,
                    subtasks,
                    pomodoroConfig
                )
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
    selectedCategoriesCount: Int
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

            Box {
                IconButton(onClick = onFilterClick) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtrar",
                        tint = TextWhite
                    )
                }
                
                if (selectedCategoriesCount > 0) {
                    Badge(
                        containerColor = PrimaryBlue,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                    ) {
                        Text(
                            text = selectedCategoriesCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
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
    selectedCategoryIds: Set<Int>,
    onDismiss: () -> Unit,
    onCategoryToggled: (Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar por categoria", color = TextWhite) },
        text = {
            Column {
                Text(
                    text = "Selecione uma ou mais categorias",
                    color = TextGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Categorias
                categories.forEach { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCategoryToggled(category.id) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedCategoryIds.contains(category.id),
                            onCheckedChange = { onCategoryToggled(category.id) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryBlue,
                                uncheckedColor = TextGray
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

