package com.example.tasktimer.model

import java.time.LocalDateTime
import java.time.ZoneOffset

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
    val id: String = "",
    val taskId: String = "",
    val startTime: LocalDateTime = LocalDateTime.now(),
    val endTime: LocalDateTime? = null,
    val durationMinutes: Int = 0,
    val type: PomodoroType = PomodoroType.WORK,
    val completed: Boolean = false
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "taskId" to taskId,
            "startTime" to startTime.toEpochSecond(ZoneOffset.UTC),
            "endTime" to endTime?.toEpochSecond(ZoneOffset.UTC),
            "durationMinutes" to durationMinutes,
            "type" to type.name,
            "completed" to completed
        )
    }

    companion object {
        fun fromMap(map: Map<String, Any?>): PomodoroSession {
            return PomodoroSession(
                id = map["id"] as? String ?: "",
                taskId = map["taskId"] as? String ?: "",
                startTime = (map["startTime"] as? Long)?.let {
                    LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
                } ?: LocalDateTime.now(),
                endTime = (map["endTime"] as? Long)?.let {
                    LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
                },
                durationMinutes = (map["durationMinutes"] as? Long)?.toInt() ?: 0,
                type = (map["type"] as? String)?.let { 
                    PomodoroType.valueOf(it) 
                } ?: PomodoroType.WORK,
                completed = map["completed"] as? Boolean ?: false
            )
        }
    }
}

enum class PomodoroType {
    WORK,
    SHORT_BREAK,
    LONG_BREAK
}
