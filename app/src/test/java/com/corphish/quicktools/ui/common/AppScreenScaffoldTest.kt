package com.corphish.quicktools.ui.common

import androidx.compose.ui.unit.dp
import com.tabotabo.ui.layout.SizeClass
import org.junit.Assert.assertEquals
import org.junit.Test

class AppScreenScaffoldTest {

    @Test
    fun testSizeClassFor_Phone() {
        assertEquals(SizeClass.Compact, sizeClassFor(411.dp))
    }

    @Test
    fun testSizeClassFor_JustBelowThreshold() {
        assertEquals(SizeClass.Compact, sizeClassFor(599.dp))
    }

    @Test
    fun testSizeClassFor_AtThreshold() {
        assertEquals(SizeClass.Expanded, sizeClassFor(600.dp))
    }

    @Test
    fun testSizeClassFor_Tablet() {
        assertEquals(SizeClass.Expanded, sizeClassFor(800.dp))
    }
}
