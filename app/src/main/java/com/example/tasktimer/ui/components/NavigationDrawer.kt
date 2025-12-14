package com.example.tasktimer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktimer.data.MockTaskRepository
import com.example.tasktimer.model.Category
import com.example.tasktimer.ui.home.TaskFilter
import com.example.tasktimer.ui.theme.*

@Composable
fun DrawerContent(
    categories: List<Category>,
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
    onCategoryAdded: () -> Unit = {} // Callback para atualizar lista
) {
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    
    ModalDrawerSheet(
        drawerContainerColor = SurfaceDark
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Header do Drawer
                Text(
                    text = "TaskTimer",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                HorizontalDivider(
                    Modifier,
                    DividerDefaults.Thickness,
                    color = TextGray.copy(alpha = 0.2f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Todas as Tasks
                DrawerItem(
                    title = "Todas",
                    icon = "📋",
                    isSelected = selectedFilter is TaskFilter.All,
                    onClick = { onFilterSelected(TaskFilter.All) }
                )

                // Tasks de Hoje
                DrawerItem(
                    title = "Hoje",
                    icon = "📅",
                    isSelected = selectedFilter is TaskFilter.Today,
                    onClick = { onFilterSelected(TaskFilter.Today) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de categorias
                categories.forEach { category ->
                    DrawerItem(
                        title = category.name,
                        icon = getCategoryIcon(category.name),
                        color = category.color,
                        isSelected = selectedFilter is TaskFilter.Category && 
                                     selectedFilter.categoryId == category.id,
                        onClick = { onFilterSelected(TaskFilter.Category(category.id)) }
                    )
                }
            }
            
            // Botão Nova Categoria fixo na parte inferior
            HorizontalDivider(
                color = TextGray.copy(alpha = 0.2f)
            )
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAddCategoryDialog = true }
                    .padding(16.dp),
                color = Color.Transparent
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Nova categoria",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Text(
                        text = "Nova categoria",
                        color = PrimaryBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
    
    // Dialog de adicionar categoria
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { name, color ->
                MockTaskRepository.addCategory(name, color)
                onCategoryAdded() // Notifica para atualizar
                showAddCategoryDialog = false
            }
        )
    }
}

@Composable
fun DrawerItem(
    title: String,
    icon: String,
    color: Color? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        color = if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = icon,
                fontSize = 24.sp
            )
            
            Text(
                text = title,
                color = if (isSelected) PrimaryBlue else TextWhite,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            
            if (color != null) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color, CircleShape)
                )
            }
        }
    }
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Color) -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color(0xFF4285F4)) }
    
    val predefinedColors = listOf(
        Color(0xFF4285F4), // Azul
        Color(0xFF34A853), // Verde
        Color(0xFFEA4335), // Vermelho
        Color(0xFFFBBC04), // Amarelo
        Color(0xFF9C27B0), // Roxo
        Color(0xFFFF6D00), // Laranja
        Color(0xFF00897B), // Turquesa
        Color(0xFFD81B60), // Rosa
        Color(0xFF546E7A), // Cinza azulado
        Color(0xFF6D4C41)  // Marrom
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nova Categoria",
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo de nome
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Nome da categoria", color = TextGray) },
                    placeholder = { Text("Ex: Trabalho", color = TextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = TextGray,
                        cursorColor = PrimaryBlue
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                // Seletor de cor
                Text(
                    text = "Escolha uma cor",
                    color = TextGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    predefinedColors.chunked(5).forEach { rowColors ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowColors.forEach { color ->
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            color = color,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColor = color },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selectedColor == color) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selecionado",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onConfirm(categoryName.trim(), selectedColor)
                    }
                },
                enabled = categoryName.isNotBlank()
            ) {
                Text(
                    text = "Criar",
                    color = if (categoryName.isNotBlank()) PrimaryBlue else TextGray
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextGray)
            }
        },
        containerColor = SurfaceDark
    )
}

fun getCategoryIcon(categoryName: String): String {
    return when (categoryName.lowercase()) {
        "trabalho" -> "💼"
        "desenvolvimento" -> "💻"
        "pessoal" -> "🏠"
        "estudos" -> "📚"
        "saúde" -> "💊"
        else -> "📌"
    }
}
