package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.functions.TextFunctions
import com.corphish.quicktools.repository.TextReplacementRepositoryImpl
import com.corphish.quicktools.usecases.ClipboardUseCase
import com.corphish.quicktools.usecases.FindOccurrencesUseCase
import com.corphish.quicktools.usecases.ReplaceTextUseCase
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
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
    
    // Real implementations for testing logic
    private val textFunctions = TextFunctions()
    private val repository = TextReplacementRepositoryImpl()
    private val findOccurrencesUseCase = FindOccurrencesUseCase(textFunctions)
    private val replaceTextUseCase = ReplaceTextUseCase(repository)

    @Before
    fun setUp() {
        viewModel = TextReplacementViewModel(
            repository,
            findOccurrencesUseCase,
            replaceTextUseCase,
            clipboardUseCase
        )
    }

    @Test
    fun testInitializationAndFind() = runTest {
        backgroundScope.launch { viewModel.mainText.collect() }
        backgroundScope.launch { viewModel.counterTotal.collect() }
        backgroundScope.launch { viewModel.findRanges.collect() }

        viewModel.initializeWith("hello world hello")
        viewModel.setFindText("hello")
        runCurrent()
        
        assertEquals(2, viewModel.counterTotal.value)
        assertEquals(0, viewModel.counterIndex.value)
        assertEquals(2, viewModel.findRanges.value.size)
    }

    @Test
    fun testReplaceFirst() = runTest {
        backgroundScope.launch { viewModel.mainText.collect() }
        backgroundScope.launch { viewModel.undoState.collect() }
        backgroundScope.launch { viewModel.findRanges.collect() }

        viewModel.initializeWith("hello world hello")
        viewModel.setFindText("hello")
        viewModel.setReplaceText("hi")
        runCurrent()
        
        viewModel.replaceFirst()
        runCurrent()
        
        assertEquals("hi world hello", viewModel.mainText.value)
        assertTrue(viewModel.undoState.value)
    }

    @Test
    fun testReplaceAll() = runTest {
        backgroundScope.launch { viewModel.mainText.collect() }
        backgroundScope.launch { viewModel.findRanges.collect() }

        viewModel.initializeWith("hello world hello")
        viewModel.setFindText("hello")
        viewModel.setReplaceText("hi")
        runCurrent()
        
        viewModel.replaceAll()
        runCurrent()
        
        assertEquals("hi world hi", viewModel.mainText.value)
    }

    @Test
    fun testUndoRedo() = runTest {
        backgroundScope.launch { viewModel.mainText.collect() }
        backgroundScope.launch { viewModel.undoState.collect() }
        backgroundScope.launch { viewModel.redoState.collect() }

        viewModel.initializeWith("initial")
        viewModel.updateMainText("changed")
        runCurrent()
        
        viewModel.undo()
        runCurrent()
        assertEquals("initial", viewModel.mainText.value)
        assertTrue(viewModel.redoState.value)
        
        viewModel.redo()
        runCurrent()
        assertEquals("changed", viewModel.mainText.value)
        assertFalse(viewModel.redoState.value)
    }

    @Test
    fun testReset() = runTest {
        backgroundScope.launch { viewModel.mainText.collect() }
        backgroundScope.launch { viewModel.findText.collect() }
        backgroundScope.launch { viewModel.counterTotal.collect() }

        viewModel.initializeWith("initial")
        viewModel.updateMainText("changed")
        viewModel.setFindText("h")
        runCurrent()
        
        viewModel.reset()
        runCurrent()
        
        assertEquals("initial", viewModel.mainText.value)
        assertEquals("", viewModel.findText.value)
        assertEquals(0, viewModel.counterTotal.value)
    }

    @Test
    fun testCounterNavigation() = runTest {
        backgroundScope.launch { viewModel.counterTotal.collect() }
        backgroundScope.launch { viewModel.counterIndex.collect() }

        viewModel.initializeWith("a a a")
        viewModel.setFindText("a")
        runCurrent()
        
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
