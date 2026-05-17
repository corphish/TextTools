package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.data.Result
import com.corphish.quicktools.repository.SettingsRepository
import com.corphish.quicktools.usecases.ClipboardUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EvalViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: EvalViewModel
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)
    private val clipboardUseCase: ClipboardUseCase = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { settingsRepository.getEvaluateResultMode() } returns EvalViewModel.EVAL_RESULT_MODE_ASK_NEXT_TIME
        viewModel = EvalViewModel(settingsRepository, clipboardUseCase)
    }

    @Test
    fun testEvaluate_Success() = runTest {
        every { settingsRepository.getDecimalPoints() } returns 2
        
        viewModel.evaluate("2 + 2")
        
        val result = viewModel.evalResult.value
        assertTrue(result is Result.Success)
        assertEquals("4", (result as Result.Success).value.resultString)
    }

    @Test
    fun testEvaluate_SuccessWithDecimals() = runTest {
        every { settingsRepository.getDecimalPoints() } returns 2
        
        viewModel.evaluate("5 / 2")
        
        val result = viewModel.evalResult.value
        assertTrue(result is Result.Success)
        assertEquals("2.5", (result as Result.Success).value.resultString)
    }

    @Test
    fun testEvaluate_Error() = runTest {
        viewModel.evaluate("invalid expression")
        
        val result = viewModel.evalResult.value
        assertTrue(result is Result.Error)
    }

    @Test
    fun testEvaluate_CopyToClipboard() = runTest {
        every { settingsRepository.getDecimalPoints() } returns 2
        viewModel.denoteModeSelectionByUser(EvalViewModel.EVAL_RESULT_COPY_TO_CLIPBOARD)
        
        viewModel.evaluate("10 * 10")
        
        verify { clipboardUseCase.copyToClipboard("100") }
    }

    @Test
    fun testRememberChoice() {
        viewModel.denoteModeSelectionByUser(EvalViewModel.EVAL_RESULT_REPLACE)
        viewModel.denoteUserRememberChoice(true)
        
        verify { settingsRepository.setEvaluateResultMode(EvalViewModel.EVAL_RESULT_REPLACE) }
    }
}
