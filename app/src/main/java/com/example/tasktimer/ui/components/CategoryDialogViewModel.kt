package com.example.tasktimer.ui.components

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasktimer.data.RoomRepository
import com.example.tasktimer.model.Category
import com.example.tasktimer.ui.components.drawer.CategoryColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CategoryDialogState(
    val categoryName: String = "",
    val selectedColor: Color = CategoryColors.defaultColor,
    val nameError: Boolean = false
)

class CategoryDialogViewModel(
    private val repository: RoomRepository
) : ViewModel() {
    private val _state = MutableStateFlow(CategoryDialogState())
    val state: StateFlow<CategoryDialogState> = _state.asStateFlow()

    fun initializeForEdit(category: Category) {
        _state.value = CategoryDialogState(
            categoryName = category.name,
            selectedColor = category.color
        )
    }

    fun updateCategoryName(name: String) {
        _state.value = _state.value.copy(categoryName = name, nameError = false)
    }

    fun updateSelectedColor(color: Color) {
        _state.value = _state.value.copy(selectedColor = color)
    }

    fun addCategory(onSuccess: () -> Unit) {
        if (_state.value.categoryName.isBlank()) {
            _state.value = _state.value.copy(nameError = true)
            return
        }

        viewModelScope.launch {
            try {
                repository.addCategory(
                    _state.value.categoryName.trim(),
                    _state.value.selectedColor
                )
                onSuccess()
            } catch (e: Exception) {
                android.util.Log.e("CategoryDialogViewModel", "Erro ao adicionar categoria", e)
            }
        }
    }

    fun updateCategory(categoryId: String, onSuccess: () -> Unit) {
        if (_state.value.categoryName.trim().isBlank()) {
            _state.value = _state.value.copy(nameError = true)
            return
        }

        viewModelScope.launch {
            try {
                repository.updateCategory(
                    categoryId,
                    _state.value.categoryName.trim(),
                    _state.value.selectedColor
                )
                onSuccess()
            } catch (e: Exception) {
                android.util.Log.e("CategoryDialogViewModel", "Erro ao atualizar categoria", e)
            }
        }
    }

    fun reset() {
        _state.value = CategoryDialogState()
    }
}
