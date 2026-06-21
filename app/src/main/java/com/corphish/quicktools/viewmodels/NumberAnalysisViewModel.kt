package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.data.NumberAnalysisResult
import com.corphish.quicktools.repository.SettingsRepository
import com.corphish.quicktools.usecases.NumberAnalysisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NumberAnalysisViewModel @Inject constructor(
    private val numberAnalysisUseCase: NumberAnalysisUseCase,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _analysisResult = MutableStateFlow<NumberAnalysisResult?>(null)
    val analysisResult: StateFlow<NumberAnalysisResult?> = _analysisResult

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText

    private val _selectedBase = MutableStateFlow<Int?>(null)
    val selectedBase: StateFlow<Int?> = _selectedBase

    private var _precision = 10

    init {
        _precision = settingsRepository.getDecimalPoints()
    }

    fun setInput(text: String) {
        _inputText.value = text
        updateAnalysis()
    }

    fun setBase(base: Int) {
        _selectedBase.value = base
        updateAnalysis()
    }

    private fun updateAnalysis() {
        val text = _inputText.value
        _analysisResult.value = null

        viewModelScope.launch(Dispatchers.Default) {
            if (text.isNotEmpty()) {
                val determination = numberAnalysisUseCase.execute(text, precision = _precision)
                val baseToUse = _selectedBase.value ?: determination?.base ?: 10

                if (_selectedBase.value == null && determination != null) {
                    _selectedBase.value = determination.base
                }

                val normalizedText = when (baseToUse) {
                    16 -> text.lowercase()
                    10 -> {
                        text.trimStart { it == '0' }.let {
                            if (it.isEmpty()) "0"
                            else if (it.startsWith(".")) "0$it"
                            else it
                        }
                    }

                    else -> text
                }

                _analysisResult.value = numberAnalysisUseCase.execute(normalizedText, baseToUse, precision = _precision)
            }
        }
    }
}
