package com.corphish.quicktools.ui.common.walkthrough

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import org.junit.Assert.assertEquals
import org.junit.Test

class WalkthroughLayoutTest {

    private val area = IntRect(left = 16, top = 40, right = 1064, bottom = 2000)
    private val card = IntSize(width = 800, height = 400)
    private val gap = 12

    @Test
    fun testPlacesBelowTargetWhenItFits() {
        val position = walkthroughCardPosition(Rect(100f, 200f, 300f, 300f), card, area, gap)
        assertEquals(IntOffset(100, 312), position)
    }

    @Test
    fun testPlacesAboveTargetNearTheBottom() {
        val position = walkthroughCardPosition(Rect(100f, 1800f, 300f, 1900f), card, area, gap)
        assertEquals(IntOffset(100, 1800 - gap - 400), position)
    }

    @Test
    fun testFallsBackToEdgeWithMoreRoom() {
        val tallCard = IntSize(800, 1200)
        // 700px above, 1088px below: neither fits, below has more room
        val position = walkthroughCardPosition(Rect(100f, 752f, 300f, 900f), tallCard, area, gap)
        assertEquals(IntOffset(100, 2000 - 1200), position)
    }

    @Test
    fun testFallsBackToTopWhenAboveHasMoreRoom() {
        val tallCard = IntSize(800, 1200)
        val position = walkthroughCardPosition(Rect(100f, 1100f, 300f, 1300f), tallCard, area, gap)
        assertEquals(IntOffset(100, 40), position)
    }

    @Test
    fun testKeepsCardInsideHorizontally() {
        val position = walkthroughCardPosition(Rect(900f, 200f, 1000f, 300f), card, area, gap)
        assertEquals(1064 - 800, position.x)
    }

    @Test
    fun testKeepsCardInsideWhenTargetStartsLeftOfArea() {
        val position = walkthroughCardPosition(Rect(-50f, 200f, 100f, 300f), card, area, gap)
        assertEquals(16, position.x)
    }

    @Test
    fun testNoTargetSitsAtBottom() {
        assertEquals(IntOffset(16, 2000 - 400), walkthroughCardPosition(null, card, area, gap))
    }

    @Test
    fun testCardLargerThanAreaStaysAtTopLeft() {
        val huge = IntSize(2000, 3000)
        assertEquals(IntOffset(16, 40), walkthroughCardPosition(Rect(100f, 200f, 300f, 300f), huge, area, gap))
    }
}
