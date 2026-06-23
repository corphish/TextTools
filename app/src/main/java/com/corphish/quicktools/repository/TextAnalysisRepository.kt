package com.corphish.quicktools.repository

import com.corphish.quicktools.data.TextCountResult

interface TextAnalysisRepository {
    suspend fun analyzeText(text: String): TextCountResult
}
