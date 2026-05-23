package com.corphish.quicktools.viewmodels

import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.repository.TextReplacementRepository
import com.corphish.quicktools.usecases.ClipboardUseCase
import com.corphish.quicktools.usecases.FindOccurrencesUseCase
import com.corphish.quicktools.usecases.ReplaceTextUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TextReplacementViewModel @Inject constructor(
    private val textReplacementRepository: TextReplacementRepository,
    private val findOccurrencesUseCase: FindOccurrencesUseCase,
    private val replaceTextUseCase: ReplaceTextUseCase,
    clipboardUseCase: ClipboardUseCase
) : ClipboardCopyViewModel(clipboardUseCase) {

    // Main text state from repository
    val mainText = textReplacementRepository.state.map { it.currentText }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        textReplacementRepository.state.value.currentText
    )

    // Undo/Redo states
    val undoState = textReplacementRepository.state.map { it.canUndo }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false
    )

    val redoState = textReplacementRepository.state.map { it.canRedo }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        false
    )

    // Find and replace text states
    private val _findText = MutableStateFlow("")
    private val _replaceText = MutableStateFlow("")
    private val _ignoreCase = MutableStateFlow(false)
    val findText = _findText.asStateFlow()
    val replaceText = _replaceText.asStateFlow()
    val ignoreCase = _ignoreCase.asStateFlow()

    // Counter states
    private val _counterIndex = MutableStateFlow(0)
    val counterIndex = _counterIndex.asStateFlow()

    // Find range state and counter total derived from find results
    val findRanges = combine(
        textReplacementRepository.state,
        _findText,
        _ignoreCase
    ) { state, findText, ignoreCase ->
        if (findText.isNotEmpty()) {
            findOccurrencesUseCase.execute(state.currentText, findText, ignoreCase)
        } else {
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val counterTotal = findRanges.map { it.size }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        0
    )

    fun initializeWith(originalString: String) {
        textReplacementRepository.init(originalString)
    }

    fun undo() {
        textReplacementRepository.undo()
        resetCounter()
    }

    fun redo() {
        textReplacementRepository.redo()
        resetCounter()
    }

    fun reset() {
        textReplacementRepository.reset()
        _findText.value = ""
        _replaceText.value = ""
        resetCounter()
    }

    fun replaceFirst() {
        val ranges = findRanges.value
        val index = _counterIndex.value
        if (ranges.isNotEmpty() && index < ranges.size) {
            replaceTextUseCase.replaceOne(ranges[index], _replaceText.value)
            resetCounter()
        }
    }

    fun replaceAll() {
        replaceTextUseCase.replaceAll(_findText.value, _replaceText.value, _ignoreCase.value)
        resetCounter()
    }

    fun setFindText(text: String) {
        _findText.value = text
        resetCounter()
    }

    fun setReplaceText(text: String) {
        _replaceText.value = text
    }

    fun setIgnoreCase(state: Boolean) {
        _ignoreCase.value = state
        resetCounter()
    }

    fun updateMainText(text: String) {
        textReplacementRepository.updateText(text)
        resetCounter()
    }

    private fun resetCounter() {
        _counterIndex.value = 0
    }

    fun decrementCounter() {
        val total = counterTotal.value
        if (total > 0) {
            _counterIndex.value = (_counterIndex.value - 1 + total) % total
        }
    }

    fun incrementCounter() {
        val total = counterTotal.value
        if (total > 0) {
            _counterIndex.value = (_counterIndex.value + 1) % total
        }
    }
}
