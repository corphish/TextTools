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
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.theme.TabotaboTheme

/**
 * One slice of a [PieChart].
 *
 * @property label Label of the slice.
 * @property value Size of the slice relative to the others.
 * @property color Fill color; when unspecified, [pieChartColor] for the slice's index is used.
 */
data class PieChartData(
    val label: String,
    val value: Float,
    val color: Color = Color.Unspecified
)

/**
 * Theme color for the slice (or legend entry) at [index]. Neighbouring indices get clearly
 * different hues derived from the theme's primary color; index 0 is the primary color.
 *
 * @param index Zero-based index of the slice.
 */
@Composable
fun pieChartColor(index: Int): Color = TabotaboTheme.colors.distinct(index)

/**
 * Pie chart of [data]. Draws nothing when all values are zero.
 *
 * @param data Slices to draw, clockwise from the top.
 * @param modifier Modifier for the chart; it is kept square.
 */
@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier
) {
    val total = data.sumOf { it.value.toDouble() }.toFloat()
    if (total == 0f) return

    val sliceColors = data.mapIndexed { index, item ->
        if (item.color.isSpecified) item.color else pieChartColor(index)
    }

    Box(modifier = modifier.aspectRatio(1f)) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            var startAngle = -90f
            data.forEachIndexed { index, item ->
                val sweepAngle = (item.value / total) * 360f
                drawArc(
                    color = sliceColors[index],
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
