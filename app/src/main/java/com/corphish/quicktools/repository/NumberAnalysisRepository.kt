package com.corphish.quicktools.repository

import com.corphish.quicktools.data.NumberAnalysisResult

interface NumberAnalysisRepository {
    fun analyze(text: String, base: Int? = null, precision: Int = 10): NumberAnalysisResult?
}
