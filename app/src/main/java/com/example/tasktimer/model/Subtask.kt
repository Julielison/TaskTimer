package com.example.tasktimer.model

data class Subtask(
    val id: Int,
    val taskId: Int,
    val title: String,
    val isCompleted: Boolean = false,
    val order: Int = 0
)
