package com.corphish.quicktools.repository

import androidx.compose.ui.text.TextRange
import com.corphish.quicktools.text.TextReplacementManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextReplacementRepositoryImpl @Inject constructor() : TextReplacementRepository {
    private var manager: TextReplacementManager? = null
    private val _state = MutableStateFlow(TextReplacementState())
    override val state: StateFlow<TextReplacementState> = _state.asStateFlow()

    override fun init(text: String) {
        manager = TextReplacementManager(text)
        updateState(text)
    }

    override fun updateText(text: String) {
        manager?.updateText(text)
        updateState(text)
    }

    override fun replaceOne(range: TextRange, newText: String) {
        val result = manager?.replaceOne(range, newText)
        if (result != null) {
            updateState(result)
        }
    }

    override fun replaceAll(oldText: String, newText: String, ignoreCase: Boolean) {
        val result = manager?.replaceAll(oldText, newText, ignoreCase)
        if (result != null) {
            updateState(result)
        }
    }

    override fun undo() {
        val result = manager?.undo()
        if (result != null) {
            updateState(result)
        }
    }

    override fun redo() {
        val result = manager?.redo()
        if (result != null) {
            updateState(result)
        }
    }

    override fun reset() {
        val result = manager?.reset()
        if (result != null) {
            updateState(result)
        }
    }

    private fun updateState(currentText: String) {
        _state.value = TextReplacementState(
            currentText = currentText,
            canUndo = manager?.canUndo() ?: false,
            canRedo = manager?.canRedo() ?: false
        )
    }
}
