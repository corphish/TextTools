package com.corphish.quicktools.repository

import androidx.compose.ui.text.TextRange
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextReplacementRepositoryImplTest {

    private lateinit var repository: TextReplacementRepositoryImpl

    @Before
    fun setUp() {
        repository = TextReplacementRepositoryImpl()
    }

    @Test
    fun testInit() = runTest {
        repository.init("hello")
        assertEquals("hello", repository.state.value.currentText)
        assertFalse(repository.state.value.canUndo)
        assertFalse(repository.state.value.canRedo)
    }

    @Test
    fun testUpdateText() = runTest {
        repository.init("hello")
        repository.updateText("world")
        assertEquals("world", repository.state.value.currentText)
        assertTrue(repository.state.value.canUndo)
    }

    @Test
    fun testReplaceOne() = runTest {
        repository.init("hello world")
        repository.replaceOne(TextRange(0, 5), "hi")
        assertEquals("hi world", repository.state.value.currentText)
        assertTrue(repository.state.value.canUndo)
    }

    @Test
    fun testReplaceAll() = runTest {
        repository.init("hello hello")
        repository.replaceAll("hello", "hi", false)
        assertEquals("hi hi", repository.state.value.currentText)
        assertTrue(repository.state.value.canUndo)
    }

    @Test
    fun testUndoRedo() = runTest {
        repository.init("1")
        repository.updateText("2")
        
        repository.undo()
        assertEquals("1", repository.state.value.currentText)
        assertTrue(repository.state.value.canRedo)
        
        repository.redo()
        assertEquals("2", repository.state.value.currentText)
        assertFalse(repository.state.value.canRedo)
    }

    @Test
    fun testReset() = runTest {
        repository.init("initial")
        repository.updateText("changed")
        repository.reset()
        
        assertEquals("initial", repository.state.value.currentText)
        assertFalse(repository.state.value.canUndo)
        assertFalse(repository.state.value.canRedo)
    }
}
