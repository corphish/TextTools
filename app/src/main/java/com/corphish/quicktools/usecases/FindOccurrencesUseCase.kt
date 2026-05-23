package com.corphish.quicktools.usecases

import androidx.compose.ui.text.TextRange
import com.corphish.quicktools.functions.TextFunctions
import javax.inject.Inject

/**
 * Use case to find occurrences of a string in another string.
 */
class FindOccurrencesUseCase @Inject constructor(
    private val textFunctions: TextFunctions
) {
    fun execute(
        mainInput: String,
        findText: String,
        ignoreCase: Boolean
    ): List<TextRange> {
        return textFunctions.findOccurrences(mainInput, findText, ignoreCase)
    }
}
