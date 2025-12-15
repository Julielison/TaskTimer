package com.example.tasktimer.ui.dashboard.components

import androidx.compose.runtime.Composable
import com.example.tasktimer.model.FocusStats
import com.example.tasktimer.ui.dashboard.StatsPeriod

@Composable
fun DetailsSection(
    selectedPeriod: StatsPeriod,
    stats: FocusStats?,
    onPeriodChange: (StatsPeriod) -> Unit
) {
    PeriodTabs(
        selected = selectedPeriod,
        onSelect = onPeriodChange
    )

    // Usar dados reais das categorias do Firebase
    val focusData = stats?.focusByCategory ?: emptyMap()
    
    if (focusData.isNotEmpty()) {
        FocusDonutWithLegend(data = focusData)
    } else {
        // Exibir mensagem quando não há dados
        FocusDonutWithLegend(
            data = mapOf("Nenhum dado" to 0)
        )
    }
}
