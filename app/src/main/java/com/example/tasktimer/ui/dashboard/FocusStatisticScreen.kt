package com.example.tasktimer.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasktimer.ui.dashboard.components.DetailsSection
import com.example.tasktimer.ui.dashboard.components.StatsGrid

@Composable
fun FocusStatisticsScreen(
    viewModel: FocusStatsViewModel = viewModel()
) {
    val state by viewModel.state

    when {
        state.isLoading -> {
            Text(
                "Carregando...",
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            )
        }

        state.errorMessage != null -> {
            Text(
                text = state.errorMessage!!,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            )
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()              // ocupa toda a tela
                    .padding(16.dp),            // padding externo
                verticalArrangement = Arrangement.Top, // conteúdo começa do topo
                horizontalAlignment = Alignment.Start  // alinhado à esquerda
            ) {
                Text(
                    text = "Resumo de Foco",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                StatsGrid(state.stats)

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "Distribuição de Foco",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                DetailsSection(
                    selectedPeriod = state.selectedPeriod,
                    onPeriodChange = viewModel::onPeriodChange
                )
            }
        }
    }
}
