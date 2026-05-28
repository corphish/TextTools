package com.corphish.quicktools.functions

import android.icu.text.Transliterator
import androidx.compose.ui.text.TextRange
import com.corphish.quicktools.data.TextCountResult
import java.text.Normalizer
import java.util.Locale
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
            wordFrequency = wordFrequency,
            longestRepeatedSubstring = findLongestRepeatedSubstring(text),
            runLengthEncoding = calculateRLE(text),
            longestPalindrome = findLongestPalindrome(text),
            emails = extractEmails(text),
            phoneNumbers = extractPhoneNumbers(text),
            urls = extractURLs(text),
            ipv4Addresses = extractIPv4(text),
            ipv6Addresses = extractIPv6(text),
            dates = extractDates(text),
            times = extractTimes(text),
            currencies = extractCurrencies(text),
            binaryTexts = extractBinary(text),
            hexTexts = extractHex(text),
            jsonTexts = extractJSON(text),
            longestIncreasingSubsequence = findLIS(text)
        )
    }

    private fun findLongestRepeatedSubstring(text: String): String {
        val n = text.length
        var longest = ""
        for (i in 0 until n) {
            for (j in i + 1 until n) {
                var k = 0
                while (i + k < n && j + k < n && text[i + k] == text[j + k]) {
                    k++
                }
                if (k > longest.length) {
                    longest = text.substring(i, i + k)
                }
            }
        }
        return longest
    }

    private fun calculateRLE(text: String): String {
        if (text.isEmpty()) return ""
        val sb = StringBuilder()
        var i = 0
        while (i < text.length) {
            var count = 1
            while (i + 1 < text.length && text[i] == text[i + 1]) {
                i++
                count++
            }
            sb.append(text[i]).append(count)
            i++
        }
        return sb.toString()
    }

    private fun findLongestPalindrome(text: String): String {
        if (text.isEmpty()) return ""
        var start = 0
        var end = 0
        for (i in text.indices) {
            val len1 = expandAroundCenter(text, i, i)
            val len2 = expandAroundCenter(text, i, i + 1)
            val len = maxOf(len1, len2)
            if (len > end - start) {
                start = i - (len - 1) / 2
                end = i + len / 2
            }
        }
        return text.substring(start, end + 1)
    }

    private fun expandAroundCenter(s: String, left: Int, right: Int): Int {
        var l = left
        var r = right
        while (l >= 0 && r < s.length && s[l] == s[r]) {
            l--
            r++
        }
        return r - l - 1
    }

    private fun extractEmails(text: String): List<String> {
        val emailRegex = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".toRegex()
        return emailRegex.findAll(text).map { it.value }.toList()
    }

    private fun extractPhoneNumbers(text: String): List<String> {
        val phoneRegex = "(\\+?\\d{1,3}[- ]?)?(\\d{10}|\\d{3}[- ]\\d{3}[- ]\\d{4}|\\d{3}[- ]\\d{4})".toRegex()
        return phoneRegex.findAll(text).map { it.value }.toList()
    }

    private fun extractURLs(text: String): List<String> {
        val urlRegex = "https?://[\\w\\d:#@%/\\$()~_?+\\-=\\\\.&]*".toRegex()
        return urlRegex.findAll(text).map { it.value }.toList()
    }

    private fun extractIPv4(text: String): List<String> {
        val ipv4Regex = "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b".toRegex()
        return ipv4Regex.findAll(text).map { it.value }.toList()
    }

    private fun extractIPv6(text: String): List<String> {
        val ipv6Regex = "\\b(?:[A-F0-9]{1,4}:){7}[A-F0-9]{1,4}\\b".toRegex(RegexOption.IGNORE_CASE)
        return ipv6Regex.findAll(text).map { it.value }.toList()
    }

    private fun extractDates(text: String): List<String> {
        val dateRegex = "\\b\\d{1,4}[-/.]\\d{1,2}[-/.]\\d{1,4}\\b".toRegex()
        return dateRegex.findAll(text).map { it.value }.toList()
    }

    private fun extractTimes(text: String): List<String> {
        val timeRegex = "\\b\\d{1,2}:\\d{2}(?::\\d{2})?\\b".toRegex()
        return timeRegex.findAll(text).map { it.value }.toList()
    }

    private fun extractCurrencies(text: String): List<String> {
        val currencyRegex = "[\$€£¥₹]\\s?\\d+(?:[.,]\\d{2})?".toRegex()
        return currencyRegex.findAll(text).map { it.value }.toList()
    }

    private fun extractBinary(text: String): List<String> {
        val binaryRegex = "\\b[01]{8,}\\b".toRegex()
        return binaryRegex.findAll(text).map { it.value }.toList()
    }

    private fun extractHex(text: String): List<String> {
        val hexRegex = "\\b(?:0x)?[A-F0-9]{2,}\\b".toRegex(RegexOption.IGNORE_CASE)
        return hexRegex.findAll(text).map { it.value }.filter { it.length % 2 == 0 || it.startsWith("0x") }.toList()
    }

    private fun extractJSON(text: String): List<String> {
        val jsonList = mutableListOf<String>()
        var i = 0
        while (i < text.length) {
            if (text[i] == '{' || text[i] == '[') {
                val start = i
                val openChar = text[i]
                val closeChar = if (openChar == '{') '}' else ']'
                var balance = 1
                i++
                while (i < text.length && balance > 0) {
                    if (text[i] == openChar) balance++
                    else if (text[i] == closeChar) balance--
                    i++
                }
                if (balance == 0) {
                    val potentialJson = text.substring(start, i)
                    if (openChar == '[' || potentialJson.contains(":")) {
                        jsonList.add(potentialJson)
                    }
                }
            } else {
                i++
            }
        }
        return jsonList
    }

    private fun findLIS(text: String): String {
        if (text.isEmpty()) return ""
        val n = text.length
        val parent = IntArray(n) { -1 }
        val indexInTails = IntArray(n)
        var size = 0

        for (i in 0 until n) {
            var lo = 0
            var hi = size
            while (lo < hi) {
                val mid = (lo + hi) / 2
                if (text[indexInTails[mid]] < text[i]) {
                    lo = mid + 1
                } else {
                    hi = mid
                }
            }

            if (lo > 0) {
                parent[i] = indexInTails[lo - 1]
            }
            indexInTails[lo] = i
            if (lo == size) {
                size++
            }
        }

        val res = StringBuilder()
        var curr = indexInTails[size - 1]
        while (curr != -1) {
            res.append(text[curr])
            curr = parent[curr]
        }
        return res.reverse().toString()
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

    /**
     * Changes the case of the given text.
     */
    fun changeCase(text: String, toCase: Int): String {
        return when (toCase) {
            0 -> text.uppercase(Locale.getDefault())
            1 -> text.lowercase(Locale.getDefault())
            2 -> titleCase(text, true)
            3 -> titleCase(text, false)
            4 -> randomCase(text)
            else -> text
        }
    }

    private fun randomCase(str: String): String {
        val array = str.toCharArray()
        for (i in array.indices) {
            array[i] =
                if ((Math.random() * 2).toInt() % 2 == 0) Character.toUpperCase(array[i]) else Character.toLowerCase(
                    array[i]
                )
        }

        return String(array)
    }

    private fun titleCase(str: String, firstWordOnly: Boolean): String {
        return str.lines().joinToString("\n") { line ->
            val parts = line.split(" ")
            val builder = StringBuilder()
            var firstWordConverted = false

            for (word in parts) {
                var w = word
                if (w.isEmpty()) continue
                if (!firstWordOnly || !firstWordConverted) {
                    val array = w.toCharArray()
                    array[0] = Character.toUpperCase(array[0])
                    w = String(array)
                    firstWordConverted = true
                }

                builder.append(w).append(" ")
            }

            builder.toString().trim()
        }
    }

    /**
     * Performs custom wrap on str with the given wrap text.
     * str will be prepended with the first 2 characters of wrap and appended with the
     * last 2 letters of wrap if the wrap string length is even.
     * If the wrap string length is odd, the string will be wrapped with wrap text
     * on both sides.
     */
    fun customWrap(str: String, wrap: String): String {
        if (wrap.length % 2 == 1) {
            return "${wrap}${str}${wrap}"
        }

        val first = wrap.substring(0, wrap.length / 2)
        val second = wrap.substring(wrap.length / 2)

        return "${first}${str}${second}"
    }

    /**
     * Wraps the text with pre-defined options.
     */
    fun presetWrap(text: String, option: Int): String {
        return when (option) {
            0 -> "'$text'"
            1 -> "\"$text\""
            2 -> "($text)"
            3 -> "{$text}"
            4 -> "[$text]"
            else -> text
        }
    }

    /**
     * Sorts the lines of the given text.
     */
    fun sortLines(text: String) =
        text.split("\n").sorted().joinToString("\n")

    /**
     * Repeats the text for the given number of times.
     */
    fun repeatText(text: String, repeatCount: Int) =
        text.repeat(repeatCount)

    /**
     * Removes the given text from the given string.
     * 3 modes are supported:
     * 0 -> Remove the first occurrence.
     * 1 -> Remove the last occurrence.
     * 2 -> Remove all occurrences.
     */
    fun removeText(text: String, remove: String, mode: Int) =
        when (mode) {
            0 -> text.replaceFirst(remove, "")
            1 -> {
                val reversed = reverseText(text)
                val replaced = reversed.replaceFirst(remove, "")
                reverseText(replaced)
            }

            else -> text.replace(remove, "")
        }

    /**
     * Reverses the given text.
     */
    fun reverseText(text: String) =
        text.reversed()

    /**
     * Adds number to lines in the given text.
     */
    fun numberLines(text: String): String {
        val lines = text.split("\n")
        var number = 1
        return lines.joinToString("\n") {
            "${number++}. $it"
        }
    }

    /**
     * Adds prefix to a given string.
     */
    fun addPrefix(text: String, prefix: String) = "$prefix$text"

    /**
     * Adds suffix to a given string.
     */
    fun addSuffix(text: String, suffix: String) = "$text$suffix"

    /**
     * Reverses each word of the given string.
     */
    fun reverseWords(text: String): String {
        val words = text.split(" ")
        return words.joinToString(" ") { reverseText(it) }.trim()
    }

    /**
     * Reverses each word of the given string.
     */
    fun reverseLines(text: String): String {
        val words = text.split("\n")
        return words.joinToString("\n") { reverseText(it) }.trim()
    }

    /**
     * Removes white spaces from the string.
     */
    fun removeWhiteSpaces(text: String) =
        text.replace(" ", "")

    /**
     * Replaces line breaks from the string.
     */
    fun removeLineBreaks(text: String) =
        text.replace("\n", "")

    /**
     * Removes empty lines from the string.
     */
    fun removeEmptyLines(text: String) =
        text.split("\n").filter { it.isNotEmpty() }.joinToString("\n")

    /**
     * Removes duplicate words from the string.
     */
    fun removeDuplicateWords(text: String, ignoreCase: Boolean = true): String {
        val words = text.split(" ")
        val sb = StringBuilder()
        val set = HashSet<String>()

        for (word in words) {
            if (ignoreCase) {
                if (set.add(word.lowercase())) {
                    sb.append(word).append(" ")
                }
            } else {
                if (set.add(word)) {
                    sb.append(word).append(" ")
                }
            }
        }

        return sb.toString().trim()
    }

    /**
     * Removes duplicate words from the string.
     */
    fun removeDuplicateLines(text: String, ignoreCase: Boolean = true): String {
        val words = text.split("\n")
        val sb = StringBuilder()
        val set = HashSet<String>()

        for (word in words) {
            if (ignoreCase) {
                if (set.add(word.lowercase())) {
                    sb.append(word).append("\n")
                }
            } else {
                if (set.add(word)) {
                    sb.append(word).append("\n")
                }
            }
        }

        return sb.toString().trim()
    }

    /**
     * Converts the given string to bold (serif).
     * Only alphanumeric characters will be converted.
     */
    fun boldSerif(s: String): String {
        val alphanumericCount = countAlphanumericCharacters(s)
        val otherCharCount = s.length - alphanumericCount
        val result = ByteArray(4 * alphanumericCount + otherCharCount)
        var index = 0

        s.codePoints().forEach {
            if (Character.isDigit(it)) {
                result[index] = -16
                result[index + 1] = -99
                result[index + 2] = -97
                result[index + 3] = (it - 162).toByte()
                index += 4
            } else if (Character.isUpperCase(it)) {
                result[index] = -16
                result[index + 1] = -99
                result[index + 2] = -112
                result[index + 3] = (it - 193).toByte()
                index += 4
            } else if (Character.isLowerCase(it)) {
                result[index] = -16
                result[index + 1] = -99
                result[index + 2] = -112
                result[index + 3] = (it - 199).toByte()
                index += 4
            } else {
                result[index] = it.toByte()
                index += 1
            }
        }

        return String(result)
    }

    /**
     * Converts the given string to italic (serif).
     * Only alphabetic characters will be converted.
     */
    fun italicSerif(s: String): String {
        val alphabeticCount = countAlphabeticCharacters(s)
        val otherCharCount = s.length - alphabeticCount
        val smallHCount = s.count { it == 'h' }
        val result = ByteArray(4 * alphabeticCount + otherCharCount - smallHCount)
        var index = 0

        s.codePoints().forEach {
            if (Character.isUpperCase(it)) {
                if (it < 77) {
                    // A-L
                    result[index] = -16
                    result[index + 1] = -99
                    result[index + 2] = -112
                    result[index + 3] = (it - 141).toByte()
                } else {
                    // M-Z
                    result[index] = -16
                    result[index + 1] = -99
                    result[index + 2] = -111
                    result[index + 3] = (it - 205).toByte()
                }
                index += 4
            } else if (Character.isLowerCase(it)) {
                if (it == 104) {
                    // Exception for h
                    result[index] = -30
                    result[index + 1] = -124
                    result[index + 2] = -114
                    index += 3
                } else {
                    result[index] = -16
                    result[index + 1] = -99
                    result[index + 2] = -111
                    result[index + 3] = (it - 211).toByte()
                    index += 4
                }
            } else {
                result[index] = it.toByte()
                index += 1
            }
        }

        return String(result)
    }

    /**
     * Converts the given string to bold/italic (serif).
     * Only alphabetic characters will be converted.
     */
    fun boldItalicSerif(s: String): String {
        val alphabeticCount = countAlphabeticCharacters(s)
        val otherCharCount = s.length - alphabeticCount
        val result = ByteArray(4 * alphabeticCount + otherCharCount)
        var index = 0

        s.codePoints().forEach {
            if (it == 89) {
                // Exception for Y
                result[index] = -16
                result[index + 1] = -99
                result[index + 2] = -110
                result[index + 3] = -128
                index += 4
            } else if (it == 90) {
                // Exception for Z
                result[index] = -16
                result[index + 1] = -99
                result[index + 2] = -110
                result[index + 3] = -127
                index += 4
            } else if (Character.isUpperCase(it)) {
                result[index] = -16
                result[index + 1] = -99
                result[index + 2] = -111
                result[index + 3] = (it - 153).toByte()
                index += 4
            } else if (Character.isLowerCase(it)) {
                result[index] = -16
                result[index + 1] = -99
                result[index + 2] = -110
                result[index + 3] = (it - 223).toByte()
                index += 4
            } else {
                result[index] = it.toByte()
                index += 1
            }
        }

        return String(result)
    }

    /**
     * Converts the given string to bold (sans).
     * Only alphanumeric characters will be converted.
     */
    fun boldSans(s: String): String {
        val alphanumericCount = countAlphanumericCharacters(s)
        val otherCharCount = s.length - alphanumericCount
        val result = ByteArray(4 * alphanumericCount + otherCharCount)
        var index = 0

        s.codePoints().forEach {
            if (Character.isDigit(it)) {
                result[index] = -16
                result[index + 1] = -99
                result[index + 2] = -97
                result[index + 3] = (it - 132).toByte()
                index += 4
            } else if (Character.isUpperCase(it)) {
                result[index] = -16
                result[index + 1] = -99
                result[index + 2] = -105
                result[index + 3] = (it - 173).toByte()
                index += 4
            } else if (Character.isLowerCase(it)) {
                if (it < 115) {
                    // a-r
                    result[index] = -16
                    result[index + 1] = -99
                    result[index + 2] = -105
                    result[index + 3] = (it - 179).toByte()
                } else {
                    // s-z
                    result[index] = -16
                    result[index + 1] = -99
                    result[index + 2] = -104
                    result[index + 3] = (it - 243).toByte()
                }
                index += 4
            } else {
                result[index] = it.toByte()
                index += 1
            }
        }

        return String(result)
    }

    /**
     * Converts the given string to italic (sans).
     * Only alphabetic characters will be converted.
     */
    fun italicSans(s: String): String {
        val alphabeticCount = countAlphabeticCharacters(s)
        val otherCharCount = s.length - alphabeticCount
        val result = ByteArray(4 * alphabeticCount + otherCharCount)
        var index = 0

        s.codePoints().forEach {
            if (Character.isUpperCase(it)) {
                result[index] = -16
                result[index + 1] = -99
                result[index + 2] = -104
                result[index + 3] = (it - 185).toByte()
                index += 4
            } else if (Character.isLowerCase(it)) {
                result[index] = -16
                result[index + 1] = -99
                result[index + 2] = -104
                result[index + 3] = (it - 191).toByte()
                index += 4
            } else {
                result[index] = it.toByte()
                index += 1
            }
        }

        return String(result)
    }

    /**
     * Converts the given string to bold/italic (sans).
     * Only alphabetic characters will be converted.
     */
    fun boldItalicSans(s: String): String {
        val alphabeticCount = countAlphabeticCharacters(s)
        val otherCharCount = s.length - alphabeticCount
        val result = ByteArray(4 * alphabeticCount + otherCharCount)
        var index = 0

        s.codePoints().forEach {
            if (Character.isUpperCase(it)) {
                if (it < 69) {
                    // A-D
                    result[index] = -16
                    result[index + 1] = -99
                    result[index + 2] = -104
                    result[index + 3] = (it - 133).toByte()
                } else {
                    // E-Z
                    result[index] = -16
                    result[index + 1] = -99
                    result[index + 2] = -103
                    result[index + 3] = (it - 197).toByte()
                }
                index += 4
            } else if (Character.isLowerCase(it)) {
                result[index] = -16
                result[index + 1] = -99
                result[index + 2] = -103
                result[index + 3] = (it - 203).toByte()
                index += 4
            } else {
                result[index] = it.toByte()
                index += 1
            }
        }

        return String(result)
    }

    /**
     * Decorates the text with short strikethrough (unicode text).
     */
    fun shortStrikethrough(s: String): String {
        val result = ByteArray(3 * s.length)
        var index = 0
        s.codePoints().forEach {
            result[index] = it.toByte()
            result[index + 1] = -52
            result[index + 2] = -75
            index += 3
        }

        return String(result)
    }

    /**
     * Decorates the text with long strikethrough (unicode text).
     */
    fun longStrikethrough(s: String): String {
        val result = ByteArray(3 * s.length)
        var index = 0
        s.codePoints().forEach {
            result[index] = it.toByte()
            result[index + 1] = -52
            result[index + 2] = -74
            index += 3
        }

        return String(result)
    }

    /**
     * Decorates text as cursive (unicode text).
     */
    fun cursive(s: String): String {
        val exceptionChars = "egoBEFHILMR"
        var exceptionCount = 0

        s.forEach {
            if (exceptionChars.contains("$it")) {
                exceptionCount += 1
            }
        }

        val alphabeticCount = countAlphabeticCharacters(s)
        val otherCharCount = s.length - alphabeticCount
        val result = ByteArray(4 * alphabeticCount + otherCharCount - exceptionCount)
        var index = 0

        s.codePoints().forEach {
            if (Character.isUpperCase(it)) {
                // A-D
                when (it) {
                    66 -> {
                        result[index] = -30
                        result[index + 1] = -124
                        result[index + 2] = -84
                        index += 3
                    }
                    69 -> {
                        result[index] = -30
                        result[index + 1] = -124
                        result[index + 2] = -80
                        index += 3
                    }
                    70 -> {
                        result[index] = -30
                        result[index + 1] = -124
                        result[index + 2] = -79
                        index += 3
                    }
                    72 -> {
                        result[index] = -30
                        result[index + 1] = -124
                        result[index + 2] = -117
                        index += 3
                    }
                    73 -> {
                        result[index] = -30
                        result[index + 1] = -124
                        result[index + 2] = -112
                        index += 3
                    }
                    76 -> {
                        result[index] = -30
                        result[index + 1] = -124
                        result[index + 2] = -110
                        index += 3
                    }
                    77 -> {
                        result[index] = -30
                        result[index + 1] = -124
                        result[index + 2] = -77
                        index += 3
                    }
                    82 -> {
                        result[index] = -30
                        result[index + 1] = -124
                        result[index + 2] = -101
                        index += 3
                    }
                    else -> {
                        result[index] = -16
                        result[index + 1] = -99
                        result[index + 2] = -110
                        result[index + 3] = (it - 165).toByte()
                        index += 4
                    }
                }
            } else if (Character.isLowerCase(it)) {
                if (it == 101) {
                    result[index] = -30
                    result[index + 1] = -124
                    result[index + 2] = -81
                    index += 3
                } else if (it == 103) {
                    result[index] = -30
                    result[index + 1] = -124
                    result[index + 2] = -118
                    index += 3
                } else if (it == 111) {
                    result[index] = -30
                    result[index + 1] = -124
                    result[index + 2] = -76
                    index += 3
                } else if (it < 107) {
                    result[index] = -16
                    result[index + 1] = -99
                    result[index + 2] = -110
                    result[index + 3] = (it - 171).toByte()
                    index += 4
                } else {
                    result[index] = -16
                    result[index + 1] = -99
                    result[index + 2] = -109
                    result[index + 3] = (it - 235).toByte()
                    index += 4
                }
            } else {
                result[index] = it.toByte()
                index += 1
            }
        }

        return String(result)
    }

    /**
     * Counts the number of alphanumeric characters.
     */
    private fun countAlphanumericCharacters(s: String) =
        s.codePoints().filter { codePoint ->
            (codePoint in 65..90) || (codePoint in 97..122) || (codePoint in 48..57)
        }.count().toInt()

    /**
     * Counts the number of alphabetic characters.
     */
    private fun countAlphabeticCharacters(s: String) =
        s.codePoints().filter { codePoint ->
            (codePoint in 65..90) || (codePoint in 97..122)
        }.count().toInt()

    /**
     * Line break by characters
     */
    fun lineBreakByCharacter(s: String, count: Int): String {
        if (count == 0) {
            return s
        }

        val sb = StringBuilder()
        var x = 0

        for (i in s.indices) {
            if (x == count) {
                sb.append("\n")
                x = 0
            }

            sb.append(s[i])
            x += 1
        }

        return sb.toString()
    }

    /**
     * Line break by characters
     */
    fun lineBreakByWords(s: String, count: Int): String {
        if (count == 0) {
            return s
        }

        val sb = StringBuilder()
        var x = 0

        for (str in s.split(" ")) {
            if (x == count) {
                sb.append("\n")
                x = 0
            }

            sb.append(str).append(" ")
            x += 1
        }

        return sb.toString().trim()
    }

    /**
     * Squeezes multi-line string into lesser lines such that each line
     * has at most maxCharsPerLine characters.
     * If an input line consists of chars more than it behaves as line break.
     */
    fun squeeze(s: String, maxCharsPerLine: Int) =
        lineBreakByCharacter(s.split("\n").joinToString(""), maxCharsPerLine)

    fun prependLines(s: String, prependText: String): String {
        val lines = s.split("\n")
        return lines.joinToString("\n") {
            "$prependText$it"
        }
    }

    fun appendLines(s: String, appendText: String): String {
        val lines = s.split("\n")
        return lines.joinToString("\n") {
            "$it$appendText"
        }
    }

    fun monospaceFont(s: String): String {
        val sb = StringBuilder()

        for (i in 0..<s.length) {
            val codePoint = s.codePointAt(i)

            if (codePoint >= 'A'.code && codePoint <= 'Z'.code) {
                // Math Monospace Capital A is U+1D670
                sb.appendCodePoint(codePoint - 'A'.code + 0x1D670)
            } else if (codePoint >= 'a'.code && codePoint <= 'z'.code) {
                // Math Monospace Small a is U+1D68A
                sb.appendCodePoint(codePoint - 'a'.code + 0x1D68A)
            } else if (codePoint >= '0'.code && codePoint <= '9'.code) {
                // Math Monospace Digit 0 is U+1D7F6
                sb.appendCodePoint(codePoint - '0'.code + 0x1D7F6)
            } else {
                // Keep spaces, punctuation, and other symbols as they are
                sb.appendCodePoint(codePoint)
            }
        }

        return sb.toString()
    }

    fun clearUnicodeFormatting(input: String): String {
        return try {
            val transliterator = Transliterator.getInstance("Any-Publishing; Any-Name; Name-Any")
            val cleaned = transliterator.transliterate(input)
            Normalizer.normalize(cleaned, Normalizer.Form.NFKD)
        } catch (e: Exception) {
            Normalizer.normalize(input, Normalizer.Form.NFKD)
        }
    }
}
