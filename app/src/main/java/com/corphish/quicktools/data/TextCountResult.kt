package com.corphish.quicktools.data

/**
 * Data class representing the result of text counting.
 */
data class TextCountResult(
    val characterCount: Int = 0,
    val letterCount: Int = 0,
    val digitCount: Int = 0,
    val wordCount: Int = 0,
    val spaceCount: Int = 0,
    val symbolCount: Int = 0,
    val wordFrequency: List<Pair<String, Int>> = emptyList()
)
