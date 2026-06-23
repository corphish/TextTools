package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.TextCountResult
import com.corphish.quicktools.repository.TextAnalysisRepository
import javax.inject.Inject

/**
 * Use case for text counting.
 */
class TextCountUseCase @Inject constructor(
    private val textAnalysisRepository: TextAnalysisRepository
) {
    /**
     * Counts the various properties of the given text.
     * @param text String to count.
     * @return TextCountResult containing the counts.
     */
    suspend fun execute(text: String): TextCountResult {
        return textAnalysisRepository.analyzeText(text)
    }
}
