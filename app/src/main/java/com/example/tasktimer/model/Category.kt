package com.example.tasktimer.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

data class Category(
    val id: String = "",
    val name: String = "",
    val color: Color = Color.Gray,
    val icon: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "color" to color.toArgb(),
            "icon" to icon,
            "createdAt" to createdAt
        )
    }

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Category {
            return Category(
                id = id,
                name = map["name"] as? String ?: "",
                color = (map["color"] as? Long)?.toInt()?.let { Color(it) } ?: Color.Gray,
                icon = map["icon"] as? String,
                createdAt = map["createdAt"] as? Long ?: System.currentTimeMillis()
            )
        }
    }
}
