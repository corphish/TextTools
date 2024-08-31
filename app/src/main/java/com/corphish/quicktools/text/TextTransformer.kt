package com.corphish.quicktools.text

import java.lang.StringBuilder
import java.util.Locale

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
     * last 2 letters of wrap.
     * Which is why it is important to have the length of wrap as even.
     */
    fun customWrap(str: String, wrap: String): String {
        if (wrap.length % 2 == 1) {
            return str
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
}