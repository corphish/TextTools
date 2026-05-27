package com.corphish.quicktools.repository

import com.corphish.quicktools.functions.TextFunctions
import javax.inject.Inject

class TextTransformRepositoryImpl @Inject constructor(
    private val textFunctions: TextFunctions
) : TextTransformRepository {
    override fun transform(
        text: String,
        primaryIndex: Int,
        secondaryIndex: Int,
        secondaryText: String
    ): String {
        return when (primaryIndex) {
            INDEX_NONE -> text

            INDEX_WRAP_TEXT -> {
                if (secondaryIndex == 5) {
                    textFunctions.customWrap(text, secondaryText)
                } else {
                    textFunctions.presetWrap(text, secondaryIndex)
                }
            }

            INDEX_CHANGE_CASE -> {
                textFunctions.changeCase(text, secondaryIndex)
            }

            INDEX_SORT_LINES -> {
                textFunctions.sortLines(text)
            }

            INDEX_REPEAT_TEXT -> {
                textFunctions.repeatText(
                    text,
                    secondaryText.toIntOrNull() ?: 1
                )
            }

            INDEX_REMOVE_TEXT -> {
                when (secondaryIndex) {
                    0, 1, 2 -> textFunctions.removeText(
                        text,
                        secondaryText,
                        secondaryIndex
                    )

                    3 -> textFunctions.removeWhiteSpaces(text)
                    4 -> textFunctions.removeLineBreaks(text)
                    5 -> textFunctions.removeEmptyLines(text)
                    6 -> textFunctions.removeDuplicateWords(
                        text,
                        ignoreCase = true
                    )

                    7 -> textFunctions.removeDuplicateWords(
                        text,
                        ignoreCase = false
                    )

                    8 -> textFunctions.removeDuplicateLines(
                        text,
                        ignoreCase = true
                    )

                    9 -> textFunctions.removeDuplicateLines(
                        text,
                        ignoreCase = false
                    )

                    else -> text
                }
            }

            INDEX_ADD_PREFIX_SUFFIX -> {
                when (secondaryIndex) {
                    0 -> textFunctions.addPrefix(text, secondaryText)
                    1 -> textFunctions.addSuffix(text, secondaryText)
                    else -> text
                }
            }

            INDEX_NUMBER_LINES -> {
                textFunctions.numberLines(text)
            }

            INDEX_REVERSE_TEXT -> {
                textFunctions.reverseText(text)
            }

            INDEX_REVERSE_WORDS -> {
                textFunctions.reverseWords(text)
            }

            INDEX_PREPEND_LINES -> {
                textFunctions.prependLines(text, secondaryText)
            }

            INDEX_APPEND_LINES -> {
                textFunctions.appendLines(text, secondaryText)
            }

            INDEX_REVERSE_LINES -> {
                textFunctions.reverseLines(text)
            }

            INDEX_DECORATE_TEXT -> {
                val cleaned = textFunctions.clearUnicodeFormatting(text)
                when (secondaryIndex) {
                    0 -> textFunctions.boldSerif(cleaned)
                    1 -> textFunctions.italicSerif(cleaned)
                    2 -> textFunctions.boldItalicSerif(cleaned)
                    3 -> textFunctions.boldSans(cleaned)
                    4 -> textFunctions.italicSans(cleaned)
                    5 -> textFunctions.boldItalicSans(cleaned)
                    6 -> textFunctions.shortStrikethrough(cleaned)
                    7 -> textFunctions.longStrikethrough(cleaned)
                    8 -> textFunctions.cursive(cleaned)
                    9 -> textFunctions.monospaceFont(cleaned)
                    10 -> cleaned
                    else -> text
                }
            }

            INDEX_LINE_BREAK -> {
                when (secondaryIndex) {
                    0 -> textFunctions.lineBreakByCharacter(
                        text,
                        secondaryText.toIntOrNull() ?: 0
                    )

                    1 -> textFunctions.lineBreakByWords(
                        text,
                        secondaryText.toIntOrNull() ?: 0
                    )

                    else -> text
                }
            }

            INDEX_SQUEEZE -> {
                textFunctions.squeeze(text, secondaryText.toIntOrNull() ?: 0)
            }

            INDEX_REPLACE_WHITESPACE -> {
                text.replace(" ", secondaryText)
            }

            else -> text
        }
    }

    companion object {
        const val INDEX_NONE = 0
        const val INDEX_WRAP_TEXT = 1
        const val INDEX_CHANGE_CASE = 2
        const val INDEX_SORT_LINES = 3
        const val INDEX_REPEAT_TEXT = 4
        const val INDEX_REMOVE_TEXT = 5
        const val INDEX_ADD_PREFIX_SUFFIX = 6
        const val INDEX_NUMBER_LINES = 7
        const val INDEX_PREPEND_LINES = 8
        const val INDEX_APPEND_LINES = 9
        const val INDEX_REVERSE_TEXT = 10
        const val INDEX_REVERSE_WORDS = 11
        const val INDEX_REVERSE_LINES = 12
        const val INDEX_DECORATE_TEXT = 13
        const val INDEX_LINE_BREAK = 14
        const val INDEX_SQUEEZE = 15
        const val INDEX_REPLACE_WHITESPACE = 16
    }
}
