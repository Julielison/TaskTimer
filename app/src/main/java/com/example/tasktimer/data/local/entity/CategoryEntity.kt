package com.example.tasktimer.data.local.entity

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tasktimer.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val colorArgb: Int,
    val icon: String?,
    val createdAt: Long
) {
    fun toCategory(): Category {
        return Category(
            id = id,
            name = name,
            color = Color(colorArgb),
            icon = icon,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromCategory(category: Category): CategoryEntity {
            return CategoryEntity(
                id = category.id,
                name = category.name,
                colorArgb = category.color.toArgb(),
                icon = category.icon,
                createdAt = category.createdAt
            )
        }
    }
}
