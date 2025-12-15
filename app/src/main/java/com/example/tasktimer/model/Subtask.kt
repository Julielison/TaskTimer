package com.example.tasktimer.model

data class Subtask(
    val id: String = "",
    val taskId: String = "",
    val title: String = "",
    val isCompleted: Boolean = false,
    val order: Int = 0
) {
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "taskId" to taskId,
            "title" to title,
            "isCompleted" to isCompleted,
            "order" to order
        )
    }

    companion object {
        fun fromMap(id: String, map: Map<String, Any?>): Subtask {
            return Subtask(
                id = id,
                taskId = map["taskId"] as? String ?: "",
                title = map["title"] as? String ?: "",
                isCompleted = map["isCompleted"] as? Boolean ?: false,
                order = (map["order"] as? Long)?.toInt() ?: 0
            )
        }
    }
}
