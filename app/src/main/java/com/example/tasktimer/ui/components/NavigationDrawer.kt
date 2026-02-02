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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktimer.model.Category
import com.example.tasktimer.ui.components.drawer.*
import com.example.tasktimer.ui.home.TaskFilter
import com.example.tasktimer.ui.theme.PrimaryBlue
import com.example.tasktimer.ui.theme.SurfaceDark

@Composable
fun DrawerContent(
    categories: List<Category>,
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
    onCategoryAdded: () -> Unit = {},
    drawerViewModel: DrawerViewModel = viewModel()
) {
    val uiState = drawerViewModel.uiState.collectAsState().value
    
    var showCategoryOptionsModal by remember { mutableStateOf(false) }
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
            
            DrawerFooter(onAddCategoryClick = { drawerViewModel.showAddCategoryDialog() })
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
                            selectedCategory?.let { 
                                drawerViewModel.showEditCategoryDialog(it)
                            }
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
                            selectedCategory?.let { 
                                drawerViewModel.showDeleteCategoryDialog(it)
                            }
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
    if (uiState.showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { drawerViewModel.hideAddCategoryDialog() },
            onConfirm = { name, color ->
                drawerViewModel.addCategory(name, color)
                onCategoryAdded()
            }
        )
    }
    
    // Dialog para editar categoria
    if (uiState.showEditCategoryDialog && uiState.categoryToEdit != null) {
        EditCategoryDialog(
            category = uiState.categoryToEdit!!,
            onDismiss = { 
                drawerViewModel.hideEditCategoryDialog()
            },
            onConfirm = { name, color ->
                android.util.Log.d("NavigationDrawer", "Atualizando categoria: $name")
                drawerViewModel.updateCategory(uiState.categoryToEdit!!.id, name, color)
                onCategoryAdded()
            }
        )
    }
    
    // Dialog para confirmar exclusão de categoria
    if (uiState.showDeleteCategoryDialog && uiState.categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                drawerViewModel.hideDeleteCategoryDialog()
            },
            title = { 
                Text(
                    "Excluir Categoria", 
                    color = androidx.compose.ui.graphics.Color.White
                ) 
            },
            text = { 
                Text(
                    "Tem certeza que deseja excluir a categoria '${uiState.categoryToDelete!!.name}'? Esta ação não pode ser desfeita e a categoria será removida de todas as tarefas.",
                    color = androidx.compose.ui.graphics.Color.Gray
                ) 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        android.util.Log.d("NavigationDrawer", "Deletando categoria: ${uiState.categoryToDelete!!.name}")
                        drawerViewModel.deleteCategory(uiState.categoryToDelete!!.id)
                        onCategoryAdded()
                    }
                ) {
                    Text("Excluir", color = androidx.compose.ui.graphics.Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        drawerViewModel.hideDeleteCategoryDialog()
                    }
                ) {
                    Text("Cancelar", color = androidx.compose.ui.graphics.Color.Gray)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
