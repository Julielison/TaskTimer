package com.example.tasktimer.ui.dashboard

import com.example.tasktimer.model.FocusStats
import com.example.tasktimer.model.Task
import java.time.LocalDate

object FocusStatsMapper {

    fun fromTasks(tasks: List<Task>): FocusStats {
        val today = LocalDate.now()

        val todayTasks = tasks.count {
            it.dateTime.toLocalDate() == today
        }

        val completedToday = tasks.filter {
            it.isCompleted && it.completedAt?.toLocalDate() == today
        }


        val todayFocusMinutes = completedToday.size * 25

        val totalTasks = tasks.size

        val totalFocusMinutes = tasks.count { it.isCompleted } * 25

        return FocusStats(
            todayTasks = todayTasks,
            todayFocusMinutes = todayFocusMinutes,
            totalTasks = totalTasks,
            totalFocusMinutes = totalFocusMinutes
        )
    }
}
