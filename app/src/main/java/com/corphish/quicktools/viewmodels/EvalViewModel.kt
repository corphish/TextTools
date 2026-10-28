package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import com.corphish.quicktools.data.Result
import com.corphish.quicktools.repository.SettingsRepository
import com.corphish.quicktools.usecases.EvalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Drives the evaluate flow.
 *
 * Flow:
 * 1. The selected expression is evaluated. Invalid expressions end in [EvalUiState.Error].
 * 2. If the result can be applied and the user has remembered Replace/Append, the output is
 *    produced right away (fast path).
 * 3. Otherwise the result is handed to Text Actions ([EvalUiState.ShowActions]), where copy is
 *    always available and apply only when the source is editable.
 * 4. When the user taps Apply there, the Replace/Append choice is asked ([EvalUiState.AskApplyMode]).
 *
 * State lives here so the flow survives configuration changes of the host activity.
 */
@HiltViewModel
class EvalViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val evalUseCase: EvalUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<EvalUiState>(EvalUiState.Idle)

    /** Current step of the evaluate flow. */
    val uiState: StateFlow<EvalUiState> = _uiState.asStateFlow()

    // Original expression, needed to build the appended output
    private var _expression = ""

    /**
     * Starts the flow for [expression]. Ignored if the flow has already started, so it is safe
     * to call again after the activity is recreated.
     *
     * @param expression Selected text to evaluate.
     * @param canApply Whether the result can be written back to the source (editable and not
     *                 forced to copy).
     */
    fun start(expression: String, canApply: Boolean) {
        if (_uiState.value != EvalUiState.Idle) return
        _expression = expression

        val result = evalUseCase.execute(expression, settingsRepository.getDecimalPoints())
        if (result !is Result.Success) {
            _uiState.value = EvalUiState.Error
            return
        }

        val rememberedMode = settingsRepository.getEvaluateResultMode()
        _uiState.value = if (canApply && rememberedMode.isApplyMode()) {
            EvalUiState.Finished(evalUseCase.buildOutput(expression, result.value, rememberedMode))
        } else {
            EvalUiState.ShowActions(result.value)
        }
    }

    /** Marks Text Actions as launched, so it is not launched again after recreation. */
    fun onActionsLaunched() {
        val state = _uiState.value
        if (state is EvalUiState.ShowActions) {
            _uiState.value = EvalUiState.AwaitingActions(state.result)
        }
    }

    /**
     * Handles Text Actions closing.
     *
     * @param appliedText Text returned by Apply, or null if the user copied, performed another
     *                    action, or went back.
     */
    fun onActionsClosed(appliedText: String?) {
        _uiState.value = if (appliedText != null) {
            EvalUiState.AskApplyMode(appliedText)
        } else {
            EvalUiState.Finished(null)
        }
    }

    /**
     * Handles the Replace/Append choice made after Apply.
     *
     * @param mode [EVAL_RESULT_REPLACE] or [EVAL_RESULT_APPEND].
     * @param remember Whether to save [mode] so later evaluations apply instantly.
     */
    fun onApplyModeSelected(mode: Int, remember: Boolean) {
        val state = _uiState.value as? EvalUiState.AskApplyMode ?: return
        if (remember) {
            settingsRepository.setEvaluateResultMode(mode)
        }
        _uiState.value = EvalUiState.Finished(evalUseCase.buildOutput(_expression, state.result, mode))
    }

    /** Handles the Replace/Append dialog being dismissed without a choice. */
    fun onApplyModeDismissed() {
        if (_uiState.value is EvalUiState.AskApplyMode) {
            _uiState.value = EvalUiState.Finished(null)
        }
    }

    private fun Int.isApplyMode() = this == EVAL_RESULT_REPLACE || this == EVAL_RESULT_APPEND

    companion object {
        // Eval result mode choices will be shown to user next time
        const val EVAL_RESULT_MODE_ASK_NEXT_TIME = 0

        // Eval result will be replaced by the selected text
        const val EVAL_RESULT_REPLACE = 1

        // Eval result will be appended after the selected text with = sign
        const val EVAL_RESULT_APPEND = 2
    }
}

/**
 * Steps of the evaluate flow.
 */
sealed interface EvalUiState {
    /** Flow not started yet. */
    data object Idle : EvalUiState

    /** Expression could not be evaluated. */
    data object Error : EvalUiState

    /** Text Actions should be launched with [result]. */
    data class ShowActions(val result: String) : EvalUiState

    /** Text Actions is open with [result]. */
    data class AwaitingActions(val result: String) : EvalUiState

    /** User tapped Apply on [result]; ask how it should be applied. */
    data class AskApplyMode(val result: String) : EvalUiState

    /** Flow is over. [output] is written back to the source if not null. */
    data class Finished(val output: String?) : EvalUiState
}
