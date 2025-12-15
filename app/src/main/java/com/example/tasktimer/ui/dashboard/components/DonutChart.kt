package com.example.tasktimer.ui.dashboard.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun DonutChart(
    data: Map<String, Int>,
    colors: List<Color>,
    size: Dp = 160.dp
) {
    val total = data.values.sum()

    // 🔲 Container com padding e tamanho máximo
    Box(
        modifier = Modifier
            .padding(16.dp)        // espaço externo
            .size(size)            // tamanho máximo
            .background(Color(0xFF121212), CircleShape), // opcional: fundo para destacar
        contentAlignment = Alignment.Center
    ) {
        if (total == 0) {
            Text("No Data", color = Color.Gray)
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidthPx = this.size.minDimension * 0.10f
                var startAngle = -90f

                data.entries.forEachIndexed { index, entry ->
                    val sweepAngle = (entry.value.toFloat() / total) * 360f

                    drawArc(
                        color = colors[index % colors.size],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(
                            width = strokeWidthPx,
                            cap = StrokeCap.Round
                        )
                    )

                    startAngle += sweepAngle
                }
            }
        }

        // Centro
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = total.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color =  Color.White
            )
            Text(
                text = "min",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun FocusDonutWithLegend(
    data: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFF8A6CFF),
        Color(0xFFFF6C6C),
        Color(0xFF6CFF8A),
        Color(0xFF6CCFFF),
        Color(0xFFFFB86C),
        Color(0xFFFF6CFF),
        Color(0xFFCCCCCC)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        // 🔵 Donut centralizado
        DonutChart(
            data = data,
            colors = colors,
            size = 200.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 🏷️ Legendas em pares
        LegendGrid(
            data = data,
            colors = colors
        )
    }
}
@Composable
fun LegendGrid(
    data: Map<String, Int>,
    colors: List<Color>
) {
    val entries = data.entries.toList()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Agrupa de 2 em 2
        entries.chunked(2).forEach { pair ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                pair.forEachIndexed { index, entry ->
                    LegendItem(
                        color = colors[index % colors.size],
                        label = entry.key,
                        value = entry.value
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String,
    value: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$label → $value min",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}