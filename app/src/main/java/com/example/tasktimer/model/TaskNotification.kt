package com.example.tasktimer.model

data class TaskNotification(
    val taskId: Int,
    val notificationId: Int,
    val scheduledTime: Long,
    val isSent: Boolean = false,
    val isDismissed: Boolean = false,
    val isPersistent: Boolean = true
)
