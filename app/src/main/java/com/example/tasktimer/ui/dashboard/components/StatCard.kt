package com.example.tasktimer.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasktimer.model.FocusStats




@Composable
fun StatsGrid(stats: List<FocusStats>) {

    if (stats.isEmpty()) {
        Text(
            text = "Nenhum dado disponível",
            color = Color.Gray
        )
        return
    }

    val data = stats.first()

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Tarefas de Hoje",
                value = data.todayTasks.toString(),
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Foco Hoje (h)",
                value = formatMinutesToHours(data.todayFocusMinutes),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total de Tarefas",
                value = data.totalTasks.toString(),
                modifier = Modifier.weight(1f)
            )

            StatCard(
                title = "Duração Total de Foco",
                value = formatMinutesToHours(data.totalFocusMinutes),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun formatMinutesToHours(totalMinutes: Int): String {
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return String.format("%02d:%02d", hours, minutes)
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1C1E)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = Color.Gray,
                fontSize = 12.sp
            )

            Text(
                text = value,
                color = Color(0xFF8A6CFF),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
