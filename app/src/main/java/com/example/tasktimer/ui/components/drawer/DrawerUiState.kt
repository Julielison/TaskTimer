package com.example.tasktimer.ui.components.drawer

import com.example.tasktimer.model.Category

data class DrawerUiState(
    val categories: List<Category> = emptyList(),
    val showAddCategoryDialog: Boolean = false,
    val showEditCategoryDialog: Boolean = false,
    val showDeleteCategoryDialog: Boolean = false,
    val categoryToEdit: Category? = null,
    val categoryToDelete: Category? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
