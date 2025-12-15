package com.example.tasktimer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.tasktimer.model.Category
import com.example.tasktimer.ui.components.drawer.*
import com.example.tasktimer.ui.home.HomeViewModel
import com.example.tasktimer.ui.home.TaskFilter
import com.example.tasktimer.ui.theme.PrimaryBlue
import com.example.tasktimer.ui.theme.SurfaceDark
import kotlinx.coroutines.launch

@Composable
fun DrawerContent(
    categories: List<Category>,
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
    onCategoryAdded: () -> Unit = {},
    viewModel: HomeViewModel? = null
) {
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showDeleteCategoryDialog by remember { mutableStateOf(false) }
    var showCategoryOptionsModal by remember { mutableStateOf(false) }
    var showEditCategoryDialog by remember { mutableStateOf(false) }
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    val scrollState = rememberScrollState()
    
    val menuItems = remember(categories, selectedFilter) {
        buildDrawerMenuItems(categories, selectedFilter)
    }
    
    ModalDrawerSheet(drawerContainerColor = SurfaceDark) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                DrawerHeader()
                Spacer(modifier = Modifier.height(16.dp))
                
                DrawerMenuItems(
                    items = menuItems,
                    onItemClick = { item ->
                        when (item) {
                            is DrawerMenuItem.FilterItem -> onFilterSelected(item.filter)
                            is DrawerMenuItem.CategoryItem -> onFilterSelected(
                                TaskFilter.Category(item.category.id)
                            )
                        }
                    },
                    onCategoryLongPress = { categoryId ->
                        selectedCategory = categories.find { it.id == categoryId }
                        showCategoryOptionsModal = true
                    }
                )
            }
            
            DrawerFooter(onAddCategoryClick = { showAddCategoryDialog = true })
        }
    }
    
    // Modal de opções da categoria
    if (showCategoryOptionsModal && selectedCategory != null) {
        AlertDialog(
            onDismissRequest = { 
                showCategoryOptionsModal = false
                selectedCategory = null
            },
            title = { 
                Text(
                    "Opções da categoria", 
                    color = androidx.compose.ui.graphics.Color.White
                ) 
            },
            text = {
                Column {
                    Text(
                        "O que deseja fazer com '${selectedCategory!!.name}'?",
                        color = androidx.compose.ui.graphics.Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Botão Editar
                    Button(
                        onClick = {
                            categoryToEdit = selectedCategory
                            showEditCategoryDialog = true
                            showCategoryOptionsModal = false
                            selectedCategory = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar categoria")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Botão Excluir
                    Button(
                        onClick = {
                            categoryToDelete = selectedCategory
                            showDeleteCategoryDialog = true
                            showCategoryOptionsModal = false
                            selectedCategory = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                            contentColor = androidx.compose.ui.graphics.Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Excluir categoria")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(
                    onClick = { 
                        showCategoryOptionsModal = false
                        selectedCategory = null
                    }
                ) {
                    Text("Cancelar", color = androidx.compose.ui.graphics.Color.Gray)
                }
            },
            containerColor = SurfaceDark
        )
    }
    
    // Dialog para adicionar categoria
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { name, color ->
                viewModel?.addCategory(name, color)
                showAddCategoryDialog = false
                onCategoryAdded()
            }
        )
    }
    
    // Dialog para editar categoria
    if (showEditCategoryDialog && categoryToEdit != null) {
        EditCategoryDialog(
            category = categoryToEdit!!,
            onDismiss = { 
                showEditCategoryDialog = false
                categoryToEdit = null
            },
            onConfirm = { name, color ->
                viewModel?.updateCategory(categoryToEdit!!.id, name, color)
                showEditCategoryDialog = false
                categoryToEdit = null
                onCategoryAdded()
            }
        )
    }
    
    // Dialog para confirmar exclusão de categoria
    if (showDeleteCategoryDialog && categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteCategoryDialog = false
                categoryToDelete = null
            },
            title = { 
                Text(
                    "Excluir Categoria", 
                    color = androidx.compose.ui.graphics.Color.White
                ) 
            },
            text = { 
                Text(
                    "Tem certeza que deseja excluir a categoria '${categoryToDelete!!.name}'? Esta ação não pode ser desfeita e a categoria será removida de todas as tarefas.",
                    color = androidx.compose.ui.graphics.Color.Gray
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel?.deleteCategory(categoryToDelete!!.id)
                        showDeleteCategoryDialog = false
                        categoryToDelete = null
                        onCategoryAdded()
                    }
                ) {
                    Text("Excluir", color = androidx.compose.ui.graphics.Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteCategoryDialog = false
                        categoryToDelete = null
                    }
                ) {
                    Text("Cancelar", color = androidx.compose.ui.graphics.Color.Gray)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
