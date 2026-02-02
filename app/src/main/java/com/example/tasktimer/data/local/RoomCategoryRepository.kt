package com.example.tasktimer.data.local

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.tasktimer.data.local.dao.CategoryDao
import com.example.tasktimer.data.local.entity.CategoryEntity
import com.example.tasktimer.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class RoomCategoryRepository(private val categoryDao: CategoryDao) {
    
    fun getCategoriesFlow(): Flow<List<Category>> {
        return categoryDao.getAllFlow().map { entities ->
            entities.map { it.toCategory() }
        }
    }

    suspend fun getCategories(): List<Category> {
        return categoryDao.getAll().map { it.toCategory() }
    }

    suspend fun getCategoryById(id: String): Category? {
        return categoryDao.getById(id)?.toCategory()
    }

    suspend fun getCategoriesMap(): Map<String, String> {
        return categoryDao.getAll().associate { it.id to it.name }
    }

    suspend fun addCategory(name: String, color: Color): String {
        val id = UUID.randomUUID().toString()
        val category = CategoryEntity(
            id = id,
            name = name,
            colorArgb = color.toArgb(),
            icon = null,
            createdAt = System.currentTimeMillis()
        )
        categoryDao.insert(category)
        return id
    }

    suspend fun updateCategory(categoryId: String, name: String, color: Color) {
        val existing = categoryDao.getById(categoryId) ?: return
        val updated = existing.copy(name = name, colorArgb = color.toArgb())
        categoryDao.update(updated)
    }

    suspend fun deleteCategory(categoryId: String) {
        categoryDao.deleteById(categoryId)
    }
}
