package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextCountViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TextCountViewModel

    @Before
    fun setUp() {
        viewModel = TextCountViewModel()
    }

    @Test
    fun testTextProcessing() = runTest {
        val text = "Hello world 123!"
        viewModel.setTextAndProcess(text)
        
        assertEquals(16, viewModel.characterCount.value)
        assertEquals(10, viewModel.letterCount.value) // Hello world
        assertEquals(3, viewModel.digitCount.value)   // 123
        assertEquals(2, viewModel.spaceCount.value)   // 2 spaces
        assertEquals(1, viewModel.symbolCount.value)  // !
        assertEquals(3, viewModel.wordCount.value)    // Hello, world, 123!
    }

    @Test
    fun testWordFrequency() = runTest {
        val text = "apple banana apple orange banana apple"
        viewModel.setTextAndProcess(text)
        
        val freq = viewModel.wordFrequency.value
        assertEquals(3, freq.size)
        assertEquals("apple" to 3, freq[0])
        assertEquals("banana" to 2, freq[1])
        assertEquals("orange" to 1, freq[2])
    }

    @Test
    fun testEmptyText() = runTest {
        viewModel.setTextAndProcess("")
        assertEquals(0, viewModel.characterCount.value)
        assertEquals(0, viewModel.wordCount.value)
        assertTrue(viewModel.wordFrequency.value.isEmpty())
    }
}
