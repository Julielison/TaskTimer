package com.example.tasktimer.model


data class FocusStats(
    val todayTasks: Int = 0,
    val todayFocusMinutes: Int = 0,
    val totalTasks: Int = 0,
    val totalFocusMinutes: Int = 0,
    val focusByCategory: Map<String, Int> = emptyMap()
)