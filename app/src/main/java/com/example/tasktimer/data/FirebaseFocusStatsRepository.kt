package com.example.tasktimer.data


import com.example.tasktimer.model.FocusStats
import com.example.tasktimer.ui.dashboard.StatsPeriod

class FirebaseFocusStatsRepository : FocusStatsRepository {

    override suspend fun getStats(
        period: StatsPeriod
    ): FocusStats {
        // TODO: implementar consultas Firebase
        return FocusStats()
    }
}
