package com.corphish.quicktools.functions

import com.tabotabo.ui.color.Hue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ThemeFunctionsTest {

    private val themeFunctions = ThemeFunctions()

    @Test
    fun testRandomHue_UsesGivenRandom() {
        val expected = Hue.random(Random(42)).degrees
        assertEquals(expected, themeFunctions.randomHue(Random(42)), 0f)
    }

    @Test
    fun testRandomHue_InRange() {
        repeat(100) {
            val hue = themeFunctions.randomHue()
            assertTrue("Hue $hue out of range", hue >= 0f && hue < 360f)
        }
    }
}
