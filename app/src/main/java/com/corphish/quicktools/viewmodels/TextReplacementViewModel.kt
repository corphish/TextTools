package com.corphish.quicktools.viewmodels

import androidx.compose.ui.text.TextRange
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.text.TextReplacementManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TextReplacementViewModel : ViewModel() {
    private lateinit var _textReplacementManager: TextReplacementManager

    // Selected text can be edited further by the user
    private val _mainText = MutableStateFlow("")
    val mainText = _mainText.asStateFlow()

    // Find and replace text states
    private val _findText = MutableStateFlow("")
    private val _replaceText = MutableStateFlow("")
    private val _ignoreCase = MutableStateFlow(false)
    val findText = _findText.asStateFlow()
    val replaceText = _replaceText.asStateFlow()
    val ignoreCase = _ignoreCase.asStateFlow()

    // Counter states
    private val _counterIndex = MutableStateFlow(0)
    private val _counterTotal = MutableStateFlow(0)
    val counterIndex = _counterIndex.asStateFlow()
    val counterTotal = _counterTotal.asStateFlow()

    // Find range state
    private val _findRanges = MutableStateFlow(listOf<TextRange>())
    val findRanges = _findRanges.asStateFlow()

    // Undo and redo states
    private val _undoState = MutableStateFlow(false)
    private val _redoState = MutableStateFlow(false)
    val undoState = _undoState.asStateFlow()
    val redoState = _redoState.asStateFlow()

    fun initializeWith(originalString: String) {
        _textReplacementManager = TextReplacementManager(originalString)
        _mainText.value = originalString
    }

    private fun invokeFind() {
        if (_findText.value.isNotEmpty()) {
            _findRanges.value = findText(_mainText.value, _findText.value, _ignoreCase.value)
            _counterIndex.value = 0
            _counterTotal.value = _findRanges.value.size
        } else {
            _findRanges.value = listOf()
            _counterIndex.value = 0
            _counterTotal.value = 0
        }

        _undoState.value = _textReplacementManager.canUndo()
        _redoState.value = _textReplacementManager.canRedo()
    }

    fun undo() {
        viewModelScope.launch {
            _mainText.value = _textReplacementManager.undo()
            invokeFind()
        }
    }

    fun redo() {
        viewModelScope.launch {
            _mainText.value = _textReplacementManager.redo()
            invokeFind()
        }
    }

    fun reset() {
        viewModelScope.launch {
            _mainText.value = _textReplacementManager.reset()
            _findRanges.value = listOf()
            _counterIndex.value = 0
            _counterTotal.value = 0
            _undoState.value = _textReplacementManager.canUndo()
            _redoState.value = _textReplacementManager.canRedo()
            _findText.value = ""
            _replaceText.value = ""
        }
    }

    fun replaceFirst() {
        viewModelScope.launch {
            _mainText.value = _textReplacementManager.replaceOne(
                _findRanges.value[_counterIndex.value],
                _replaceText.value
            )
            invokeFind()
        }
    }

    fun replaceAll() {
        viewModelScope.launch {
            _mainText.value = _textReplacementManager.replaceAll(
                _findText.value,
                _replaceText.value,
                _ignoreCase.value
            )
            invokeFind()
        }
    }

    fun setFindText(text: String) {
        viewModelScope.launch {
            _findText.value = text
            invokeFind()
        }
    }

    fun setReplaceText(text: String) {
        viewModelScope.launch {
            _replaceText.value = text
        }
    }

    fun setIgnoreCase(state: Boolean) {
        viewModelScope.launch {
            _ignoreCase.value = state
            invokeFind()
        }
    }

    fun updateMainText(text: String) {
        viewModelScope.launch {
            _mainText.value = text
            _textReplacementManager.updateText(text)
            _undoState.value = _textReplacementManager.canUndo()
            _redoState.value = _textReplacementManager.canRedo()
        }
    }

    fun decrementCounter() {
        viewModelScope.launch {
            _counterIndex.value = (_counterIndex.value - 1) % _counterTotal.value
            if (_counterIndex.value < 0) {
                _counterIndex.value = _counterTotal.value - 1
            }
        }
    }

    fun incrementCounter() {
        viewModelScope.launch {
            _counterIndex.value = (_counterIndex.value + 1) % _counterTotal.value
        }
    }

    private fun findText(
        mainInput: String,
        findText: String,
        ignoreCase: Boolean
    ): List<TextRange> {
        val result = mutableListOf<TextRange>()
        var index = -findText.length
        do {
            index = mainInput.indexOf(
                findText,
                startIndex = index + findText.length,
                ignoreCase = ignoreCase
            )
            if (index != -1) {
                result += TextRange(start = index, end = index + findText.length)
            }
        } while (index >= 0)

        return result
    }
}