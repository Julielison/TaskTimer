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

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _showAddCategoryDialog = MutableStateFlow(false)
    val showAddCategoryDialog: StateFlow<Boolean> = _showAddCategoryDialog.asStateFlow()

    private val _showEditCategoryDialog = MutableStateFlow(false)
    val showEditCategoryDialog: StateFlow<Boolean> = _showEditCategoryDialog.asStateFlow()

    private val _showDeleteCategoryDialog = MutableStateFlow(false)
    val showDeleteCategoryDialog: StateFlow<Boolean> = _showDeleteCategoryDialog.asStateFlow()

    private val _categoryToEdit = MutableStateFlow<Category?>(null)
    val categoryToEdit: StateFlow<Category?> = _categoryToEdit.asStateFlow()

    private val _categoryToDelete = MutableStateFlow<Category?>(null)
    val categoryToDelete: StateFlow<Category?> = _categoryToDelete.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCategoriesFlow().collect { categories ->
                _categories.value = categories
            }
        }
    }

    fun showAddCategoryDialog() {
        _showAddCategoryDialog.value = true
    }

    fun hideAddCategoryDialog() {
        _showAddCategoryDialog.value = false
    }

    fun showEditCategoryDialog(category: Category) {
        _categoryToEdit.value = category
        _showEditCategoryDialog.value = true
    }

    fun hideEditCategoryDialog() {
        _showEditCategoryDialog.value = false
        _categoryToEdit.value = null
    }

    fun showDeleteCategoryDialog(category: Category) {
        _categoryToDelete.value = category
        _showDeleteCategoryDialog.value = true
    }

    fun hideDeleteCategoryDialog() {
        _showDeleteCategoryDialog.value = false
        _categoryToDelete.value = null
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
