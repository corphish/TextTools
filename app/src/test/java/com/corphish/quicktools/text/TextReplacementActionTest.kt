package com.corphish.quicktools.text

import org.junit.Assert.assertEquals
import org.junit.Test

class TextReplacementActionTest {

    @Test
    fun testProperties() {
        val oldText = "hello"
        val newText = "world"
        val action = TextReplacementAction(oldText, newText)
        
        assertEquals(oldText, action.oldText)
        assertEquals(newText, action.newText)
    }

    @Test
    fun testEqualsAndHashCode() {
        val action1 = TextReplacementAction("a", "b")
        val action2 = TextReplacementAction("a", "b")
        val action3 = TextReplacementAction("c", "d")
        
        assertEquals(action1, action2)
        assertEquals(action1.hashCode(), action2.hashCode())
        assert(action1 != action3)
    }
}
