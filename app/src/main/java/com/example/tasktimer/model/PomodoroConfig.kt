package com.example.tasktimer.model

data class PomodoroConfig(
    val workDurationMinutes: Int = 25,
    val breakDurationMinutes: Int = 5,
    val longBreakDurationMinutes: Int = 15,
    val pomodorosUntilLongBreak: Int = 4,
    val totalPomodoros: Int = 4
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "workDurationMinutes" to workDurationMinutes,
            "breakDurationMinutes" to breakDurationMinutes,
            "longBreakDurationMinutes" to longBreakDurationMinutes,
            "pomodorosUntilLongBreak" to pomodorosUntilLongBreak,
            "totalPomodoros" to totalPomodoros
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): PomodoroConfig {
            return PomodoroConfig(
                workDurationMinutes = (map["workDurationMinutes"] as? Long)?.toInt() ?: 25,
                breakDurationMinutes = (map["breakDurationMinutes"] as? Long)?.toInt() ?: 5,
                longBreakDurationMinutes = (map["longBreakDurationMinutes"] as? Long)?.toInt() ?: 15,
                pomodorosUntilLongBreak = (map["pomodorosUntilLongBreak"] as? Long)?.toInt() ?: 4,
                totalPomodoros = (map["totalPomodoros"] as? Long)?.toInt() ?: 4
            )
        }
    }
}

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
