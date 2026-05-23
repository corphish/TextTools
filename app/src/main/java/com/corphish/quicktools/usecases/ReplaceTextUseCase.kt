package com.corphish.quicktools.usecases

import androidx.compose.ui.text.TextRange
import com.corphish.quicktools.repository.TextReplacementRepository
import javax.inject.Inject

/**
 * Use case to replace text.
 */
class ReplaceTextUseCase @Inject constructor(
    private val textReplacementRepository: TextReplacementRepository
) {
    fun replaceOne(range: TextRange, newText: String) {
        textReplacementRepository.replaceOne(range, newText)
    }

    fun replaceAll(oldText: String, newText: String, ignoreCase: Boolean) {
        textReplacementRepository.replaceAll(oldText, newText, ignoreCase)
    }
}
