package com.corphish.quicktools.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class PieChartData(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat()
    if (total == 0f) return

    Box(modifier = modifier.aspectRatio(1f)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            var startAngle = -90f
            data.forEach { item ->
                val sweepAngle = (item.value / total) * 360f
                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    size = Size(size.width, size.height),
                    topLeft = Offset(0f, 0f)
                )
                startAngle += sweepAngle
            }
        }
    }
}
