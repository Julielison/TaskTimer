package com.example.tasktimer.model


import androidx.compose.ui.graphics.Color

data class Task(
    val id: Int,
    val title: String,
    val timeOrDate: String,
    val isCompleted: Boolean = false,
    val isOverdue: Boolean = false,
    val timeColor: Color
)