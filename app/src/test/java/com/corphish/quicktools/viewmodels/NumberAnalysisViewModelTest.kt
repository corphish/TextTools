package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.data.NumberAnalysisResult
import com.corphish.quicktools.repository.SettingsRepository
import com.corphish.quicktools.usecases.NumberAnalysisUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
class NumberAnalysisViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(testDispatcher)

    private val numberAnalysisUseCase: NumberAnalysisUseCase = mockk()
    private val settingsRepository: SettingsRepository = mockk()
    private lateinit var viewModel: NumberAnalysisViewModel

    @Before
    fun setUp() {
        mockkStatic(Dispatchers::class)
        every { Dispatchers.Default } returns testDispatcher
        every { settingsRepository.getDecimalPoints() } returns 2
        viewModel = NumberAnalysisViewModel(numberAnalysisUseCase, settingsRepository)
    }

    @After
    fun tearDown() {
        unmockkStatic(Dispatchers::class)
    }

    @Test
    fun testSetInputAndProcess() = runTest(testDispatcher) {
        val input = "10"
        val determination = mockk<NumberAnalysisResult>(relaxed = true)
        every { determination.base } returns 10
        
        // Mock the determination call
        every { numberAnalysisUseCase.execute(input, base = null, precision = 2) } returns determination
        
        // Mock the final analysis call
        val expectedResult = mockk<NumberAnalysisResult>(relaxed = true)
        every { expectedResult.base } returns 10
        every { numberAnalysisUseCase.execute(input, base = 10, precision = 2) } returns expectedResult

        viewModel.setInput(input)
        
        assertEquals(input, viewModel.inputText.value)
        assertEquals(expectedResult, viewModel.analysisResult.value)
    }

    @Test
    fun testSetBase() = runTest(testDispatcher) {
        val input = "10"
        val determination = mockk<NumberAnalysisResult>(relaxed = true)
        every { determination.base } returns 10
        
        // Mocks for initial setInput
        every { numberAnalysisUseCase.execute(input, base = null, precision = 2) } returns determination
        every { numberAnalysisUseCase.execute(input, base = 10, precision = 2) } returns determination
        
        viewModel.setInput(input)

        // Switch base to 2
        val base2Result = mockk<NumberAnalysisResult>(relaxed = true)
        every { base2Result.base } returns 2
        every { numberAnalysisUseCase.execute(input, base = 2, precision = 2) } returns base2Result
        
        viewModel.setBase(2)
        
        assertEquals(base2Result, viewModel.analysisResult.value)
        assertEquals(2, viewModel.selectedBase.value)
    }
}
