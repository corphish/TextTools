package com.corphish.quicktools.functions

import androidx.compose.ui.text.TextRange
import com.corphish.quicktools.data.TextCountResult
import javax.inject.Inject

/**
 * Class containing text related functions.
 */
class TextFunctions @Inject constructor() {
    /**
     * Counts the various properties of the given text.
     * @param text String to count.
     * @return TextCountResult containing the counts.
     */
    fun countText(text: String): TextCountResult {
        if (text.isEmpty()) return TextCountResult()

        val characterCount = text.length
        var letterCount = 0
        var digitCount = 0
        var spaceCount = 0
        var symbolCount = 0

        for (c in text.toCharArray()) {
            if (c == ' ') {
                spaceCount += 1
            } else if (Character.isLetter(c)) {
                letterCount += 1
            } else if (Character.isDigit(c)) {
                digitCount += 1
            } else {
                symbolCount += 1
            }
        }

        val freq = mutableMapOf<String, Int>()
        var wordCount = 0

        for (w in text.split(" ")) {
            if (w.isEmpty()) continue
            wordCount += 1
            freq[w] = (freq[w] ?: 0) + 1
        }

        val wordFrequency = freq.entries.map { it.key to it.value }.sortedByDescending { it.second }

        return TextCountResult(
            characterCount = characterCount,
            letterCount = letterCount,
            digitCount = digitCount,
            wordCount = wordCount,
            spaceCount = spaceCount,
            symbolCount = symbolCount,
            wordFrequency = wordFrequency
        )
    }

    /**
     * Finds occurrences of a string in another string.
     * @param mainInput The string to search in.
     * @param findText The string to search for.
     * @param ignoreCase Whether to ignore case.
     * @return List of TextRange of occurrences.
     */
    fun findOccurrences(
        mainInput: String,
        findText: String,
        ignoreCase: Boolean
    ): List<TextRange> {
        if (findText.isEmpty()) return emptyList()

        val result = mutableListOf<TextRange>()
        var index = -findText.length
        do {
            index = mainInput.indexOf(
                findText,
                startIndex = index + findText.length,
                ignoreCase = ignoreCase
            )
            if (index != -1) {
                result += TextRange(start = index, end = index + findText.length)
            }
        } while (index >= 0)

        return result
    }
}
