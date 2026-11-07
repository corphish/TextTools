package com.corphish.quicktools.ui.common.walkthrough

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Places the walkthrough card next to the [spotlight]: below it when the card fits there,
 * else above it, else at the edge of [area] with more room. The card's left edge lines up
 * with the spotlight's, and the card always stays inside [area].
 *
 * @param spotlight Highlighted bounds, or null when the target is not on screen; the card then
 * sits at the bottom of [area].
 * @param cardSize Measured size of the card.
 * @param area Region the card must stay in (the screen minus insets and margins).
 * @param gap Space between the spotlight and the card.
 * @return Top-left position of the card.
 */
internal fun walkthroughCardPosition(spotlight: Rect?, cardSize: IntSize, area: IntRect, gap: Int): IntOffset {
    val maxX = max(area.left, area.right - cardSize.width)
    val maxY = max(area.top, area.bottom - cardSize.height)
    if (spotlight == null) return IntOffset(area.left, maxY)

    val below = spotlight.bottom.roundToInt() + gap
    val above = spotlight.top.roundToInt() - gap - cardSize.height
    val spaceBelow = area.bottom - below
    val spaceAbove = spotlight.top.roundToInt() - gap - area.top
    val y = when {
        spaceBelow >= cardSize.height -> below
        spaceAbove >= cardSize.height -> above
        spaceBelow >= spaceAbove -> maxY
        else -> area.top
    }
    return IntOffset(
        x = spotlight.left.roundToInt().coerceIn(area.left, maxX),
        y = y.coerceIn(area.top, maxY),
    )
}
