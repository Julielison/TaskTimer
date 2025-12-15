package com.example.tasktimer.ui.dashboard.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tasktimer.ui.dashboard.StatsPeriod

@Composable
fun PeriodTabs(
    selected: StatsPeriod,
    onSelect: (StatsPeriod) -> Unit
) {
    val options = StatsPeriod.values()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // padding externo
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start) // espaçamento + alinhamento à direita
    ) {
        options.forEach { period ->
            Text(
                text = period.label,
                modifier = Modifier.clickable { onSelect(period) },
                color = if (period == selected) Color(0xFF8A6CFF) else Color.Gray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}