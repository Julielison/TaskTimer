package com.example.tasktimer.model

data class PomodoroConfig(
    val workDurationMinutes: Int = 25,
    val breakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,
    val pomodorosUntilLongBreak: Int = 4,
    val totalPomodoros: Int = 4
)

data class PomodoroSession(
    val id: Int,
    val taskId: Int,
    val startTime: Long,
    val endTime: Long? = null,
    val durationMinutes: Int,
    val type: PomodoroType,
    val completed: Boolean = false
)

enum class PomodoroType {
    WORK,
    SHORT_BREAK,
    LONG_BREAK
}
