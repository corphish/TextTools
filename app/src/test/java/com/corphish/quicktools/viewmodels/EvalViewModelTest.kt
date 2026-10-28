package com.corphish.quicktools.viewmodels

import com.corphish.quicktools.MainDispatcherRule
import com.corphish.quicktools.data.Result
import com.corphish.quicktools.repository.EvalRepository
import com.corphish.quicktools.repository.SettingsRepository
import com.corphish.quicktools.usecases.EvalUseCase
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_APPEND
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_MODE_ASK_NEXT_TIME
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_REPLACE
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class EvalViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: EvalViewModel
    private val settingsRepository: SettingsRepository = mockk(relaxed = true)
    private val evalRepository: EvalRepository = mockk()

    @Before
    fun setUp() {
        every { settingsRepository.getDecimalPoints() } returns 2
        every { settingsRepository.getEvaluateResultMode() } returns EVAL_RESULT_MODE_ASK_NEXT_TIME
        every { evalRepository.evaluate("2 + 2", 2) } returns Result.Success("4")
        every { evalRepository.evaluate("invalid", 2) } returns Result.Error
        viewModel = EvalViewModel(settingsRepository, EvalUseCase(evalRepository))
    }

    @Test
    fun testInitialState_Idle() {
        assertEquals(EvalUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun testStart_InvalidExpression_Error() {
        viewModel.start("invalid", canApply = true)

        assertEquals(EvalUiState.Error, viewModel.uiState.value)
    }

    @Test
    fun testStart_AskMode_ShowsActions() {
        viewModel.start("2 + 2", canApply = true)

        assertEquals(EvalUiState.ShowActions("4"), viewModel.uiState.value)
    }

    @Test
    fun testStart_RememberedReplaceOnEditable_AppliesInstantly() {
        every { settingsRepository.getEvaluateResultMode() } returns EVAL_RESULT_REPLACE

        viewModel.start("2 + 2", canApply = true)

        assertEquals(EvalUiState.Finished("4"), viewModel.uiState.value)
    }

    @Test
    fun testStart_RememberedAppendOnEditable_AppliesInstantly() {
        every { settingsRepository.getEvaluateResultMode() } returns EVAL_RESULT_APPEND

        viewModel.start("2 + 2", canApply = true)

        assertEquals(EvalUiState.Finished("2 + 2 = 4"), viewModel.uiState.value)
    }

    @Test
    fun testStart_RememberedModeOnReadOnly_ShowsActions() {
        every { settingsRepository.getEvaluateResultMode() } returns EVAL_RESULT_REPLACE

        viewModel.start("2 + 2", canApply = false)

        assertEquals(EvalUiState.ShowActions("4"), viewModel.uiState.value)
    }

    @Test
    fun testStart_CalledAgain_Ignored() {
        viewModel.start("2 + 2", canApply = true)
        viewModel.onActionsLaunched()

        // Simulates activity recreation
        viewModel.start("2 + 2", canApply = true)

        assertEquals(EvalUiState.AwaitingActions("4"), viewModel.uiState.value)
        verify(exactly = 1) { evalRepository.evaluate("2 + 2", 2) }
    }

    @Test
    fun testOnActionsClosed_WithoutApply_FinishesWithoutOutput() {
        viewModel.start("2 + 2", canApply = false)
        viewModel.onActionsLaunched()

        viewModel.onActionsClosed(null)

        assertEquals(EvalUiState.Finished(null), viewModel.uiState.value)
    }

    @Test
    fun testOnActionsClosed_WithApply_AsksApplyMode() {
        viewModel.start("2 + 2", canApply = true)
        viewModel.onActionsLaunched()

        viewModel.onActionsClosed("4")

        assertEquals(EvalUiState.AskApplyMode("4"), viewModel.uiState.value)
    }

    @Test
    fun testOnApplyModeSelected_Append_WithoutRemember() {
        viewModel.start("2 + 2", canApply = true)
        viewModel.onActionsClosed("4")

        viewModel.onApplyModeSelected(EVAL_RESULT_APPEND, remember = false)

        assertEquals(EvalUiState.Finished("2 + 2 = 4"), viewModel.uiState.value)
        verify(exactly = 0) { settingsRepository.setEvaluateResultMode(any()) }
    }

    @Test
    fun testOnApplyModeSelected_Replace_WithRemember() {
        viewModel.start("2 + 2", canApply = true)
        viewModel.onActionsClosed("4")

        viewModel.onApplyModeSelected(EVAL_RESULT_REPLACE, remember = true)

        assertEquals(EvalUiState.Finished("4"), viewModel.uiState.value)
        verify { settingsRepository.setEvaluateResultMode(EVAL_RESULT_REPLACE) }
    }

    @Test
    fun testOnApplyModeSelected_NotAsking_Ignored() {
        viewModel.start("2 + 2", canApply = true)

        viewModel.onApplyModeSelected(EVAL_RESULT_REPLACE, remember = true)

        assertEquals(EvalUiState.ShowActions("4"), viewModel.uiState.value)
        verify(exactly = 0) { settingsRepository.setEvaluateResultMode(any()) }
    }

    @Test
    fun testOnApplyModeDismissed_FinishesWithoutOutput() {
        viewModel.start("2 + 2", canApply = true)
        viewModel.onActionsClosed("4")

        viewModel.onApplyModeDismissed()

        assertEquals(EvalUiState.Finished(null), viewModel.uiState.value)
    }

    @Test
    fun testOnApplyModeDismissed_AfterSelection_KeepsOutput() {
        viewModel.start("2 + 2", canApply = true)
        viewModel.onActionsClosed("4")
        viewModel.onApplyModeSelected(EVAL_RESULT_REPLACE, remember = false)

        viewModel.onApplyModeDismissed()

        assertEquals(EvalUiState.Finished("4"), viewModel.uiState.value)
    }
}
