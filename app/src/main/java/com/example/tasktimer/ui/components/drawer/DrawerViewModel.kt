package com.example.tasktimer.ui.components.drawer

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktimer.data.FirestoreRepository
import com.example.tasktimer.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DrawerViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    private val _uiState = MutableStateFlow(DrawerUiState())
    val uiState: StateFlow<DrawerUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCategoriesFlow().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    fun showAddCategoryDialog() {
        _uiState.value = _uiState.value.copy(showAddCategoryDialog = true)
    }

    fun hideAddCategoryDialog() {
        _uiState.value = _uiState.value.copy(showAddCategoryDialog = false)
    }

    fun showEditCategoryDialog(category: Category) {
        _uiState.value = _uiState.value.copy(
            categoryToEdit = category,
            showEditCategoryDialog = true
        )
    }

    fun hideEditCategoryDialog() {
        _uiState.value = _uiState.value.copy(
            showEditCategoryDialog = false,
            categoryToEdit = null
        )
    }

    fun showDeleteCategoryDialog(category: Category) {
        _uiState.value = _uiState.value.copy(
            categoryToDelete = category,
            showDeleteCategoryDialog = true
        )
    }

    fun hideDeleteCategoryDialog() {
        _uiState.value = _uiState.value.copy(
            showDeleteCategoryDialog = false,
            categoryToDelete = null
        )
    }

    fun addCategory(name: String, color: Color) {
        viewModelScope.launch {
            try {
                repository.addCategory(name, color)
                hideAddCategoryDialog()
            } catch (e: Exception) {
                android.util.Log.e("DrawerViewModel", "Erro ao adicionar categoria", e)
            }
        }
    }

    fun updateCategory(categoryId: String, name: String, color: Color) {
        viewModelScope.launch {
            try {
                repository.updateCategory(categoryId, name, color)
                hideEditCategoryDialog()
            } catch (e: Exception) {
                android.util.Log.e("DrawerViewModel", "Erro ao atualizar categoria", e)
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                repository.deleteCategory(categoryId)
                hideDeleteCategoryDialog()
            } catch (e: Exception) {
                android.util.Log.e("DrawerViewModel", "Erro ao deletar categoria", e)
            }
        }
    }
}
