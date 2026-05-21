package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.TextCountResult
import com.corphish.quicktools.functions.TextFunctions
import javax.inject.Inject

/**
 * Use case for text counting.
 */
class TextCountUseCase @Inject constructor(
    private val textFunctions: TextFunctions
) {
    /**
     * Counts the various properties of the given text.
     * @param text String to count.
     * @return TextCountResult containing the counts.
     */
    fun execute(text: String): TextCountResult {
        return textFunctions.countText(text)
    }
}
