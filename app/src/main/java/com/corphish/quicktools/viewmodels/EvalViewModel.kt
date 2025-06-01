package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.data.Result
import com.corphish.quicktools.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.floor

@HiltViewModel
class EvalViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {
    private val _evalMode = MutableStateFlow(settingsRepository.getEvaluateResultMode())
    val evalMode: StateFlow<Int> = _evalMode

    // To be populated when user finally selects a mode, if at all they select
    private var _userSelectedMode = _evalMode.value

    fun denoteModeSelectionByUser(selectedMode: Int) {
        this._userSelectedMode = selectedMode
    }

    fun denoteUserRememberChoice(choice: Boolean) {
        if (choice) {
            settingsRepository.setEvaluateResultMode(_userSelectedMode)
        }
    }

    suspend fun shouldForceCopy(choice: Boolean) {
        if (choice) {
            _evalMode.emit(EVAL_RESULT_COPY_TO_CLIPBOARD)
        }
    }

    private val _evalResult = MutableStateFlow<Result<EvaluateResult>>(Result.Initial)
    val evalResult: StateFlow<Result<EvaluateResult>> = _evalResult

    fun evaluate(text: String) {
        viewModelScope.launch {
            val decimalPoints = settingsRepository.getDecimalPoints()
            try {
                val expression = ExpressionBuilder(text).build()
                val result = expression.evaluate()

                _evalResult.value = Result.Success(
                    EvaluateResult(
                        resultString = if (ceil(result) == floor(result)) {
                            result.toInt().toString()
                        } else {
                            val decimalFormat = DecimalFormat("0.${"#".repeat(decimalPoints)}")
                            decimalFormat.format(result)
                        },
                        finalMode = _userSelectedMode
                    )
                )
            } catch (e: Exception) {
                _evalResult.value = Result.Error
            }
        }
    }

    companion object {
        // Eval result mode choices will be shown to user next time
        const val EVAL_RESULT_MODE_ASK_NEXT_TIME = 0

        // Eval result will be replaced by the selected text
        const val EVAL_RESULT_REPLACE = 1

        // Eval result will be appended after the selected text with = sign
        const val EVAL_RESULT_APPEND = 2

        // Eval result will be copied to the clipboard
        const val EVAL_RESULT_COPY_TO_CLIPBOARD = 3
    }
}

data class EvaluateResult(
    val resultString: String,
    val finalMode: Int,
)