package com.example.tasktimer.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.tasktimer.model.PomodoroConfig
import com.example.tasktimer.model.PomodoroSession
import com.example.tasktimer.model.Subtask
import com.example.tasktimer.model.Task
import java.time.LocalDateTime

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["dateTime"]),
        Index(value = ["isCompleted"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val title: String,
    val description: String?,
    val dateTime: LocalDateTime,
    val isCompleted: Boolean,
    val categoryId: String?,
    val pomodoroConfig: PomodoroConfig?,
    val pomodoroSessions: List<PomodoroSession>,
    val subtasks: List<Subtask>,
    val createdAt: LocalDateTime,
    val completedAt: LocalDateTime?
) {
    fun toTask(): Task {
        return Task(
            id = id,
            title = title,
            description = description,
            dateTime = dateTime,
            isCompleted = isCompleted,
            categoryId = categoryId,
            pomodoroConfig = pomodoroConfig,
            pomodoroSessions = pomodoroSessions,
            subtasks = subtasks,
            createdAt = createdAt,
            completedAt = completedAt
        )
    }

    companion object {
        fun fromTask(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                title = task.title,
                description = task.description,
                dateTime = task.dateTime,
                isCompleted = task.isCompleted,
                categoryId = task.categoryId,
                pomodoroConfig = task.pomodoroConfig,
                pomodoroSessions = task.pomodoroSessions,
                subtasks = task.subtasks,
                createdAt = task.createdAt,
                completedAt = task.completedAt
            )
        }
    }
}
