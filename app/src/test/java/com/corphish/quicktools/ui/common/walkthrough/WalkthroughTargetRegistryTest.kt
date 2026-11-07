package com.corphish.quicktools.ui.common.walkthrough

import androidx.compose.ui.geometry.Rect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WalkthroughTargetRegistryTest {

    private val registry = WalkthroughTargetRegistry()

    @Test
    fun testUpdate_StoresBounds() {
        registry.update("a", Rect(0f, 0f, 10f, 10f))
        assertEquals(mapOf("a" to Rect(0f, 0f, 10f, 10f)), registry.bounds.toMap())
    }

    @Test
    fun testUpdate_ReplacesBounds() {
        registry.update("a", Rect(0f, 0f, 10f, 10f))
        registry.update("a", Rect(5f, 5f, 20f, 20f))
        assertEquals(Rect(5f, 5f, 20f, 20f), registry.bounds["a"])
    }

    @Test
    fun testUpdate_EmptyBoundsRemoveTarget() {
        registry.update("a", Rect(0f, 0f, 10f, 10f))
        registry.update("a", Rect(0f, 10f, 10f, 10f))
        assertTrue(registry.bounds.isEmpty())
    }

    @Test
    fun testRemove() {
        registry.update("a", Rect(0f, 0f, 10f, 10f))
        registry.update("b", Rect(0f, 0f, 10f, 10f))
        registry.remove("a")
        assertEquals(setOf("b"), registry.bounds.keys)
    }
}
