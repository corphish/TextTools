package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.functions.TextFunctions
import com.corphish.quicktools.repository.TextTransformRepositoryImpl
import com.corphish.quicktools.usecases.ClipboardUseCase
import com.corphish.quicktools.usecases.TextTransformUseCase
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextTransformViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TextTransformViewModel
    private val clipboardUseCase: ClipboardUseCase = mockk(relaxed = true)
    
    // Real implementations for testing logic
    private val textFunctions = TextFunctions()
    private val repository = TextTransformRepositoryImpl(textFunctions)
    private val textTransformUseCase = TextTransformUseCase(repository)

    @Before
    fun setUp() {
        viewModel = TextTransformViewModel(
            textTransformUseCase,
            clipboardUseCase
        )
    }

    @Test
    fun testInitialization() = runTest {
        backgroundScope.launch { viewModel.mainText.collect() }
        backgroundScope.launch { viewModel.previewText.collect() }

        viewModel.initializeText("hello")
        runCurrent()
        
        assertEquals("hello", viewModel.mainText.value)
        assertEquals("hello", viewModel.previewText.value)
    }

    @Test
    fun testTransformation_ChangeCase() = runTest {
        backgroundScope.launch { viewModel.mainText.collect() }
        backgroundScope.launch { viewModel.previewText.collect() }
        backgroundScope.launch { viewModel.selectedPrimaryIndex.collect() }
        backgroundScope.launch { viewModel.selectedSecondaryIndex.collect() }

        viewModel.initializeText("hello")
        
        // INDEX_CHANGE_CASE = 2
        // UPPER_CASE = 0
        viewModel.selectPrimaryIndex(2)
        viewModel.selectSecondaryIndex(0)
        runCurrent()
        
        assertEquals("HELLO", viewModel.previewText.value)
    }

    @Test
    fun testTransformation_WrapText() = runTest {
        backgroundScope.launch { viewModel.mainText.collect() }
        backgroundScope.launch { viewModel.previewText.collect() }

        viewModel.initializeText("hello")
        
        // INDEX_WRAP_TEXT = 1
        // Parentheses = 2
        viewModel.selectPrimaryIndex(1)
        viewModel.selectSecondaryIndex(2)
        runCurrent()
        
        assertEquals("(hello)", viewModel.previewText.value)
    }

    @Test
    fun testTransformation_RepeatText() = runTest {
        backgroundScope.launch { viewModel.mainText.collect() }
        backgroundScope.launch { viewModel.previewText.collect() }
        backgroundScope.launch { viewModel.secondaryFunctionText.collect() }

        viewModel.initializeText("abc")
        
        // INDEX_REPEAT_TEXT = 4
        viewModel.selectPrimaryIndex(4)
        viewModel.setSecondaryText("3")
        runCurrent()
        
        assertEquals("abcabcabc", viewModel.previewText.value)
    }
}
