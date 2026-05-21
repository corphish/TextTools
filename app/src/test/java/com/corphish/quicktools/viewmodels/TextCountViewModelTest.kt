package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.data.TextCountResult
import com.corphish.quicktools.usecases.TextCountUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextCountViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val textCountUseCase: TextCountUseCase = mockk()
    private lateinit var viewModel: TextCountViewModel

    @Before
    fun setUp() {
        viewModel = TextCountViewModel(textCountUseCase)
    }

    @Test
    fun testTextProcessing() = runTest {
        val text = "Hello world 123!"
        val expectedResult = TextCountResult(
            characterCount = 16,
            letterCount = 10,
            digitCount = 3,
            spaceCount = 2,
            symbolCount = 1,
            wordCount = 3
        )
        every { textCountUseCase.execute(text) } returns expectedResult
        
        viewModel.setTextAndProcess(text)
        
        assertEquals(expectedResult, viewModel.countResult.value)
    }

    @Test
    fun testWordFrequency() = runTest {
        val text = "apple banana apple orange banana apple"
        val expectedResult = TextCountResult(
            wordFrequency = listOf("apple" to 3, "banana" to 2, "orange" to 1)
        )
        every { textCountUseCase.execute(text) } returns expectedResult
        
        viewModel.setTextAndProcess(text)
        
        val freq = viewModel.countResult.value.wordFrequency
        assertEquals(3, freq.size)
        assertEquals("apple" to 3, freq[0])
        assertEquals("banana" to 2, freq[1])
        assertEquals("orange" to 1, freq[2])
    }
}
