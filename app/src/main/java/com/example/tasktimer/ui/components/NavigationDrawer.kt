package com.example.tasktimer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tasktimer.data.FirestoreRepository
import com.example.tasktimer.model.Category
import com.example.tasktimer.ui.components.drawer.AddCategoryDialog
import com.example.tasktimer.ui.components.drawer.DrawerFooter
import com.example.tasktimer.ui.components.drawer.DrawerHeader
import com.example.tasktimer.ui.components.drawer.DrawerMenuItem
import com.example.tasktimer.ui.components.drawer.DrawerMenuItems
import com.example.tasktimer.ui.components.drawer.buildDrawerMenuItems
import com.example.tasktimer.ui.home.TaskFilter
import com.example.tasktimer.ui.theme.SurfaceDark
import kotlinx.coroutines.launch


@Composable
fun DrawerContent(
    categories: List<Category>,
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
    onCategoryAdded: () -> Unit = {}
) {
    val repository = FirestoreRepository()
    val scope = rememberCoroutineScope()
    var showAddCategoryDialog by remember { mutableStateOf(false) }
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
                    }
                )
            }
            
            DrawerFooter(onAddCategoryClick = { showAddCategoryDialog = true })
        }
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { name, color ->
                scope.launch {
                    repository.addCategory(name, color)
                    showAddCategoryDialog = false
                    onCategoryAdded()
                }
            }
        )
    }
}
