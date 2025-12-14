package com.example.tasktimer.data

import com.example.tasktimer.model.FocusStats
import com.example.tasktimer.ui.dashboard.StatsPeriod

interface FocusStatsRepository {

    suspend fun getStats(
        period: StatsPeriod
    ): FocusStats
}
