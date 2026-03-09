package com.corphish.quicktools.text

import java.util.Locale
import kotlin.text.StringBuilder

/**
 * Helper class to help with text transformation.
 */
class TextTransformer {

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
        val parts = str.split(" ")
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

        return builder.toString().trim()
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
        println(words)
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
        // Logic:
        // 1. Prepend alphanumeric codepoints with -16, -99, -105 for alphabets and -16 -99 -97 for digits
        // 2. Update the alphanumeric character based on the following rule:
        // Uppercase -> ASCII value - 193
        // Lowercase -> ASCII value - 199
        // Digit -> ASCII value - 162
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
        // Logic:
        // 1. Prepend alphanumeric codepoints with -16, -99, -112 for uppercase and -16, -99, -111 for lowercase
        // 2. Update the alphanumeric character based on the following rule:
        // Uppercase -> ASCII value - 141
        // Lowercase -> ASCII value - 211
        // Exception: h -> -30 -124 -114
        // Exceptions: from M to Z -> -16 -99 -111 (codepoint - 205)
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
        // Logic:
        // 1. Prepend alphanumeric codepoints with -16, -99, -111 for uppercase and -16, -99, -110 for lowercase
        // 2. Update the alphanumeric character based on the following rule:
        // Uppercase -> ASCII value - 153
        // Lowercase -> ASCII value - 223
        // Exceptions:
        // Y -> -16 -99 -110 -128
        // Z -> -16 -99 -110 -127
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
        // Logic:
        // 1. Prepend alphanumeric codepoints with -16, -99, -105 for alphabets and -16 -99 -97 for digits
        // 2. Update the alphanumeric character based on the following rule:
        // Uppercase -> ASCII value - 173
        // Lowercase -> ASCII value - 179
        // Digit -> ASCII value - 132
        // Exceptions: s-z -> [-16, -99, -104, codepoint - 243]
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
        // Logic:
        // 1. Prepend alphanumeric codepoints with -16, -99, -104
        // 2. Update the alphanumeric character based on the following rule:
        // Uppercase -> ASCII value - 185
        // Lowercase -> ASCII value - 191
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
        // Logic:
        // 1. Prepend alphanumeric codepoints with -16, -99, -104 for uppercase and -16, -99, -103 for lowercase
        // 2. Update the alphanumeric character based on the following rule:
        // Uppercase -> ASCII value - 133
        // Lowercase -> ASCII value - 203
        // Exceptions: E-Z -> [-16, -99, -103, codepoint - 197]
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
        // Logic: append each codeword with -52 -75
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
        // Logic: append each codeword with -52 -74
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
        // Logic:
        // 1. Prepend alphanumeric codepoints with -16, -99, -110 for uppercase and -16, -99, -110 for lowercase
        // 2. Update the alphanumeric character based on the following rule:
        // Uppercase -> ASCII value - 165
        // Lowercase -> ASCII value - 171
        // Exceptions: k-z -> [-16, -99, -109, codepoint - 235]
        // B -> [-30 -124 -84]
        // E -> [-30 -124 -80]
        // F -> [-30 -124 -79]
        // H -> [-30 -124 -117]
        // I -> [-30 -124 -112]
        // L -> [-30 -124 -110]
        // M -> [-30 -124 -77]
        // R -> [-30 -124 -101]
        // e -> [-30 -124 -81]
        // g -> [-30 -124 -118]
        // o -> [-30 -124 -76]
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
}