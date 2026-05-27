package com.corphish.quicktools.usecases

import com.corphish.quicktools.repository.TextTransformRepository
import javax.inject.Inject

/**
 * Use case for text transformation.
 */
class TextTransformUseCase @Inject constructor(
    private val textTransformRepository: TextTransformRepository
) {
    /**
     * Executes the text transformation.
     */
    fun execute(
        text: String,
        primaryIndex: Int,
        secondaryIndex: Int,
        secondaryText: String
    ): String {
        return textTransformRepository.transform(text, primaryIndex, secondaryIndex, secondaryText)
    }
}
