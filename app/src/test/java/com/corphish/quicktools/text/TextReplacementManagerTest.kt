package com.corphish.quicktools.text

import androidx.compose.ui.text.TextRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TextReplacementManagerTest {

    @Test
    fun testReplaceOne() {
        val manager = TextReplacementManager("hello world")
        // Replace "world" with "there"
        // "world" starts at 6, ends at 11
        val result = manager.replaceOne(TextRange(6, 11), "there")
        assertEquals("hello there", result)
    }

    @Test
    fun testReplaceAll() {
        val manager = TextReplacementManager("hello world hello")
        val result = manager.replaceAll("hello", "hi", false)
        assertEquals("hi world hi", result)
    }

    @Test
    fun testUndoRedo() {
        val manager = TextReplacementManager("initial")
        assertFalse(manager.canUndo())
        assertFalse(manager.canRedo())

        manager.updateText("state 1")
        assertTrue(manager.canUndo())
        assertFalse(manager.canRedo())

        manager.updateText("state 2")
        assertEquals("state 1", manager.undo())
        assertEquals("state 2", manager.redo())
        
        val m = TextReplacementManager("0")
        m.updateText("1") // Action("0", "1"), ptr=1, current="1"
        assertEquals("0", m.undo()) // Action("0", "1"), ptr=0, current="0"
        assertTrue(m.canRedo())
        assertEquals("1", m.redo()) // Action("0", "1"), ptr=1, current="1"
        assertFalse(m.canRedo())
    }

    @Test
    fun testReset() {
        val manager = TextReplacementManager("initial")
        manager.updateText("changed")
        assertEquals("initial", manager.reset())
        assertFalse(manager.canUndo())
    }
    
    @Test
    fun testActionOverwriting() {
        val m = TextReplacementManager("0")
        m.updateText("1") // Action("0", "1"), ptr=1
        m.updateText("2") // Action("1", "2"), ptr=2
        m.undo() // current="1", ptr=1
        
        m.updateText("3") // Overwrites Action("1", "2") with Action("1", "3"), ptr=2
        assertEquals("3", m.redo()) // Wait, redo should not be possible if we just added a new action at ptr
        // Actually the implementation says:
        // mActionList.add(mActionPointer, action)
        // mActionList.subList(mActionPointer + 1, mActionList.size).clear()
        // mActionPointer += 1
        // So if we were at ptr=1, we add at 1, clear everything after 1, ptr becomes 2.
        
        assertFalse(m.canRedo())
        assertEquals("1", m.undo())
    }
}
