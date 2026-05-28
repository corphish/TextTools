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
    val wordFrequency: List<Pair<String, Int>> = emptyList(),
    val longestRepeatedSubstring: String = "",
    val runLengthEncoding: String = "",
    val longestPalindrome: String = "",
    val emails: List<String> = emptyList(),
    val phoneNumbers: List<String> = emptyList(),
    val urls: List<String> = emptyList(),
    val ipv4Addresses: List<String> = emptyList(),
    val ipv6Addresses: List<String> = emptyList(),
    val dates: List<String> = emptyList(),
    val times: List<String> = emptyList(),
    val currencies: List<String> = emptyList(),
    val binaryTexts: List<String> = emptyList(),
    val hexTexts: List<String> = emptyList(),
    val jsonTexts: List<String> = emptyList(),
    val longestIncreasingSubsequence: String = ""
)
