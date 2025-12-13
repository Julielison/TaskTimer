package com.example.tasktimer.model

import androidx.compose.ui.graphics.Color

data class Category(
    val id: Int,
    val name: String,
    val color: Color,
    val icon: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
