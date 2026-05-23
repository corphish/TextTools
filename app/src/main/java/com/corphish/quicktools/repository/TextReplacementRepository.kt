package com.corphish.quicktools.repository

import androidx.compose.ui.text.TextRange
import kotlinx.coroutines.flow.StateFlow

data class TextReplacementState(
    val currentText: String = "",
    val canUndo: Boolean = false,
    val canRedo: Boolean = false
)

interface TextReplacementRepository {
    val state: StateFlow<TextReplacementState>
    fun init(text: String)
    fun updateText(text: String)
    fun replaceOne(range: TextRange, newText: String)
    fun replaceAll(oldText: String, newText: String, ignoreCase: Boolean)
    fun undo()
    fun redo()
    fun reset()
}
