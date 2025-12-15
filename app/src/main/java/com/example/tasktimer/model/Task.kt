package com.example.tasktimer.model

import java.time.LocalDateTime
import java.time.ZoneOffset

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String? = null,
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val isCompleted: Boolean = false,
    val categoryId: String? = null,
    val pomodoroConfig: PomodoroConfig? = null,
    val pomodoroSessions: List<PomodoroSession> = emptyList(),
    val subtasks: List<Subtask> = emptyList(),
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val completedAt: LocalDateTime? = null
) {
    val isOverdue: Boolean
        get() = !isCompleted && dateTime.toLocalDate().isBefore(java.time.LocalDate.now())

    val isTimePassed: Boolean
        get() {
            val now = LocalDateTime.now()
            return !isCompleted && 
                   dateTime.toLocalDate() == now.toLocalDate() && 
                   dateTime.isBefore(now)
        }

    val formattedTime: String
        get() = String.format("%02d:%02d", dateTime.hour, dateTime.minute)

    val formattedDate: String
        get() = String.format("%02d/%02d/%04d", dateTime.dayOfMonth, dateTime.monthValue, dateTime.year)

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "title" to title,
            "description" to description,
            "dateTime" to dateTime.toEpochSecond(ZoneOffset.UTC),
            "isCompleted" to isCompleted,
            "categoryId" to categoryId,
            "pomodoroConfig" to pomodoroConfig?.toMap(),
            "pomodoroSessions" to pomodoroSessions.map { it.toMap() },
            "subtasks" to subtasks.map { it.toMap() },
            "createdAt" to createdAt.toEpochSecond(ZoneOffset.UTC),
            "completedAt" to completedAt?.toEpochSecond(ZoneOffset.UTC)
        )
    }

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Task {
            return Task(
                id = id,
                title = map["title"] as? String ?: "",
                description = map["description"] as? String,
                dateTime = (map["dateTime"] as? Long)?.let { 
                    LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
                } ?: LocalDateTime.now(),
                isCompleted = map["isCompleted"] as? Boolean ?: false,
                categoryId = map["categoryId"] as? String,
                pomodoroConfig = (map["pomodoroConfig"] as? Map<String, Any?>)?.let {
                    PomodoroConfig.fromMap(it)
                },
                pomodoroSessions = (map["pomodoroSessions"] as? List<Map<String, Any?>>)?.map { m ->
                    PomodoroSession.fromMap(m)
                } ?: emptyList(),
                subtasks = (map["subtasks"] as? List<Map<String, Any?>>)?.mapIndexed { index, m ->
                    Subtask.fromMap(index.toString(), m)
                } ?: emptyList(),
                createdAt = (map["createdAt"] as? Long)?.let {
                    LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
                } ?: LocalDateTime.now(),
                completedAt = (map["completedAt"] as? Long)?.let {
                    LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC)
                }
            )
        }
    }
}