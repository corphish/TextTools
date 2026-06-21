package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.NumberAnalysisResult
import com.corphish.quicktools.repository.NumberAnalysisRepository
import javax.inject.Inject

class NumberAnalysisUseCase @Inject constructor(
    private val numberAnalysisRepository: NumberAnalysisRepository
) {
    fun execute(text: String, base: Int? = null, precision: Int = 10): NumberAnalysisResult? {
        return numberAnalysisRepository.analyze(text, base, precision)
    }
}
