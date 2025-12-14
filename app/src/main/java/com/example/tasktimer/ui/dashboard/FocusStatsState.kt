package com.example.tasktimer.ui.dashboard

import com.example.tasktimer.model.FocusStats

data class FocusStatsState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val stats: List<FocusStats> = emptyList(), // CORREÇÃO: Inicialize com lista vazia
    val selectedPeriod: StatsPeriod = StatsPeriod.WEEK
)

enum class StatsPeriod(val label: String) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month")
}
