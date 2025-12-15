package com.example.tasktimer.ui.dashboard.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktimer.ui.dashboard.StatsPeriod

@Composable
fun DetailsSection(
    selectedPeriod: StatsPeriod,
    onPeriodChange: (StatsPeriod) -> Unit
) {


    PeriodTabs(
        selected = selectedPeriod,
        onSelect = onPeriodChange
    )

    FocusDonutWithLegend(
        data = mapOf(
            "Trabalho" to 120,
            "Desenvolvimento" to 90,
            "Estudos" to 45
        )
    )



}
