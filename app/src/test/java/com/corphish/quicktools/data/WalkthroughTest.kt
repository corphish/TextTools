package com.corphish.quicktools.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WalkthroughTest {

    private fun step(target: String) = WalkthroughStep(target, 1, 2)

    @Test
    fun testTargetIds_AreDistinct() {
        val walkthrough = Walkthrough("main", listOf(step("a"), step("b"), step("a")))
        assertEquals(setOf("a", "b"), walkthrough.targetIds)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRequiresSteps() {
        Walkthrough("main", emptyList())
    }

    @Test(expected = IllegalArgumentException::class)
    fun testRequiresPositiveVersion() {
        Walkthrough("main", listOf(step("a")), version = 0)
    }

    @Test
    fun testAppWalkthroughs_HaveUniqueKeys() {
        val keys = Walkthroughs.ALL.map { it.key }
        assertEquals(keys.size, keys.toSet().size)
        assertTrue(Walkthroughs.ALL.contains(Walkthroughs.MAIN))
        assertTrue(Walkthroughs.ALL.contains(Walkthroughs.SIMULATION))
    }
}
