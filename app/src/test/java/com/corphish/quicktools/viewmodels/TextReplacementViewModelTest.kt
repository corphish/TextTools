package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.usecases.ClipboardUseCase
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextReplacementViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TextReplacementViewModel
    private val clipboardUseCase: ClipboardUseCase = mockk(relaxed = true)

    @Before
    fun setUp() {
        viewModel = TextReplacementViewModel(clipboardUseCase)
    }

    @Test
    fun testInitializationAndFind() = runTest {
        viewModel.initializeWith("hello world hello")
        viewModel.setFindText("hello")
        
        assertEquals(2, viewModel.counterTotal.value)
        assertEquals(0, viewModel.counterIndex.value)
        assertEquals(2, viewModel.findRanges.value.size)
    }

    @Test
    fun testReplaceFirst() = runTest {
        viewModel.initializeWith("hello world hello")
        viewModel.setFindText("hello")
        viewModel.setReplaceText("hi")
        
        viewModel.replaceFirst()
        
        assertEquals("hi world hello", viewModel.mainText.value)
        assertTrue(viewModel.undoState.value)
    }

    @Test
    fun testReplaceAll() = runTest {
        viewModel.initializeWith("hello world hello")
        viewModel.setFindText("hello")
        viewModel.setReplaceText("hi")
        
        viewModel.replaceAll()
        
        assertEquals("hi world hi", viewModel.mainText.value)
    }

    @Test
    fun testUndoRedo() = runTest {
        viewModel.initializeWith("initial")
        viewModel.updateMainText("changed")
        
        viewModel.undo()
        assertEquals("initial", viewModel.mainText.value)
        assertTrue(viewModel.redoState.value)
        
        viewModel.redo()
        assertEquals("changed", viewModel.mainText.value)
        assertFalse(viewModel.redoState.value)
    }

    @Test
    fun testReset() = runTest {
        viewModel.initializeWith("initial")
        viewModel.updateMainText("changed")
        viewModel.setFindText("h")
        
        viewModel.reset()
        
        assertEquals("initial", viewModel.mainText.value)
        assertEquals("", viewModel.findText.value)
        assertEquals(0, viewModel.counterTotal.value)
    }

    @Test
    fun testCounterNavigation() = runTest {
        viewModel.initializeWith("a a a")
        viewModel.setFindText("a")
        
        assertEquals(0, viewModel.counterIndex.value)
        viewModel.incrementCounter()
        assertEquals(1, viewModel.counterIndex.value)
        viewModel.incrementCounter()
        assertEquals(2, viewModel.counterIndex.value)
        viewModel.incrementCounter()
        assertEquals(0, viewModel.counterIndex.value) // wraps around
        
        viewModel.decrementCounter()
        assertEquals(2, viewModel.counterIndex.value) // wraps around
    }
}
