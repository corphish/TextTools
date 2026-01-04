package com.corphish.quicktools.viewmodels

import android.content.res.Resources
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.R
import com.corphish.quicktools.text.TextTransformer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TextTransformViewModel : ViewModel() {
    private val textTransformer = TextTransformer()

    private val _mainText = MutableStateFlow("")
    val mainText = _mainText.asStateFlow()

    private val _previewText = MutableStateFlow("")
    val previewText = _previewText.asStateFlow()

    private val _selectedPrimaryIndex = MutableStateFlow(0)
    val selectedPrimaryIndex = _selectedPrimaryIndex.asStateFlow()

    private val _selectedSecondaryIndex = MutableStateFlow(0)
    val selectedSecondaryIndex = _selectedSecondaryIndex.asStateFlow()

    private val _secondaryFunctionText = MutableStateFlow("")
    val secondaryFunctionText = _secondaryFunctionText.asStateFlow()

    private val _secondaryOptionList = MutableStateFlow(listOf<Int>())
    val secondaryOptionList = _secondaryOptionList.asStateFlow()

    private val _secondaryFunctionTextLabel = MutableStateFlow(Resources.ID_NULL)
    val secondaryFunctionTextLabel = _secondaryFunctionTextLabel.asStateFlow()

    private val _secondaryFunctionTextInputType = MutableStateFlow(KeyboardType.Text)
    val secondaryFunctionTextInputType = _secondaryFunctionTextInputType.asStateFlow()

    private val _secondaryFunctionTextEnabled = MutableStateFlow(true)
    val secondaryFunctionTextEnabled = _secondaryFunctionTextEnabled.asStateFlow()

    private val _secondaryFunctionTextVisible = MutableStateFlow(false)
    val secondaryFunctionTextVisible = _secondaryFunctionTextVisible.asStateFlow()

    private val _decorateTextErrorFlow = MutableSharedFlow<Boolean>()
    val decorateTextErrorFlow: SharedFlow<Boolean> = _decorateTextErrorFlow

    private fun determineSecondaryOptions() {
        viewModelScope.launch {
            _secondaryOptionList.value = when (_selectedPrimaryIndex.value) {
                INDEX_WRAP_TEXT -> wrapOptions
                INDEX_CHANGE_CASE -> caseOptions
                INDEX_REMOVE_TEXT -> removeOptions
                INDEX_ADD_PREFIX_SUFFIX -> prefixSuffixOptions
                INDEX_DECORATE_TEXT -> decorateOptions
                INDEX_LINE_BREAK -> lineBreakOptions
                else -> listOf()
            }
        }
    }

    fun initializeText(text: String) {
        viewModelScope.launch {
            _mainText.value = text
            _previewText.value = text
            transform()
        }
    }

    fun selectPrimaryIndex(index: Int) {
        viewModelScope.launch {
            _selectedPrimaryIndex.value = index

            // Select the appropriate choice for those supporting secondary functions
            when (index) {
                INDEX_WRAP_TEXT, INDEX_CHANGE_CASE, INDEX_REMOVE_TEXT, INDEX_ADD_PREFIX_SUFFIX, INDEX_DECORATE_TEXT, INDEX_REPLACE_WHITESPACE, INDEX_PREPEND_LINES, INDEX_APPEND_LINES -> {
                    _selectedSecondaryIndex.value = 0
                    _secondaryFunctionText.value = ""
                }

                INDEX_REPEAT_TEXT -> {
                    // Default for repeat
                    _secondaryFunctionText.value = "1"
                }

                INDEX_LINE_BREAK, INDEX_SQUEEZE -> {
                    _selectedSecondaryIndex.value = 0
                    _secondaryFunctionText.value = _mainText.value.length.toString()
                }
            }

            determineSecondaryOptions()
            determineSecondaryFunctionTextProperties()
            transform()
        }
    }

    fun selectSecondaryIndex(index: Int) {
        viewModelScope.launch {
            _selectedSecondaryIndex.value = index
            determineSecondaryFunctionTextProperties()
            transform()
        }
    }

    fun setSecondaryText(text: String) {
        viewModelScope.launch {
            _secondaryFunctionText.value = text
            transform()
        }
    }

    private fun determineSecondaryFunctionTextProperties() {
        viewModelScope.launch {
            _secondaryFunctionTextVisible.value =
                optionsWithSecondaryFunctionText.contains(_selectedPrimaryIndex.value)

            _secondaryFunctionTextLabel.value = when (_selectedPrimaryIndex.value) {
                INDEX_WRAP_TEXT -> R.string.wrap_text
                INDEX_REPEAT_TEXT -> R.string.repeat_text
                INDEX_REMOVE_TEXT -> R.string.remove_text
                INDEX_ADD_PREFIX_SUFFIX -> R.string.add_prefix_suffix
                INDEX_LINE_BREAK -> listOf(
                    R.string.after_certain_characters,
                    R.string.after_certain_words
                )[_selectedSecondaryIndex.value.coerceIn(0..1)]

                INDEX_SQUEEZE -> R.string.max_char_per_line
                INDEX_REPLACE_WHITESPACE -> R.string.replace_whitespace
                INDEX_PREPEND_LINES -> R.string.prepend_lines
                INDEX_APPEND_LINES -> R.string.append_lines
                else -> R.string.transform
            }

            _secondaryFunctionTextInputType.value =
                if (_selectedPrimaryIndex.value in listOf(
                        INDEX_REPEAT_TEXT,
                        INDEX_LINE_BREAK,
                        INDEX_SQUEEZE
                    )
                ) {
                    KeyboardType.Number
                } else {
                    KeyboardType.Text
                }

            // RIP indent
            _secondaryFunctionTextEnabled.value =
                    // Disable when remove option is selected for preset characters
                !((_selectedPrimaryIndex.value == INDEX_REMOVE_TEXT && _selectedSecondaryIndex.value > 2) ||

                        // Disable for custom wrap
                        _selectedPrimaryIndex.value == INDEX_WRAP_TEXT && _selectedSecondaryIndex.value != 5)
        }
    }

    private fun transform() {
        viewModelScope.launch {
            _previewText.value = when (_selectedPrimaryIndex.value) {
                INDEX_NONE -> {
                    // None
                    _mainText.value
                }

                INDEX_WRAP_TEXT -> {
                    // Wrap
                    if (_selectedSecondaryIndex.value == 5) {
                        textTransformer.customWrap(_mainText.value, _secondaryFunctionText.value)
                    } else {
                        textTransformer.presetWrap(_mainText.value, _selectedSecondaryIndex.value)
                    }
                }

                INDEX_CHANGE_CASE -> {
                    // Change case
                    textTransformer.changeCase(_mainText.value, _selectedSecondaryIndex.value)
                }

                INDEX_SORT_LINES -> {
                    // Sort lines
                    textTransformer.sortLines(_mainText.value)
                }

                INDEX_REPEAT_TEXT -> {
                    // Repeat text
                    textTransformer.repeatText(
                        _mainText.value,
                        _secondaryFunctionText.value.toIntOrNull() ?: 1
                    )
                }

                INDEX_REMOVE_TEXT -> {
                    // Remove text
                    when (_selectedSecondaryIndex.value) {
                        // Remove first/last/all
                        0, 1, 2 -> textTransformer.removeText(
                            _mainText.value,
                            _secondaryFunctionText.value,
                            _selectedSecondaryIndex.value
                        )

                        3 -> textTransformer.removeWhiteSpaces(_mainText.value)
                        4 -> textTransformer.removeLineBreaks(_mainText.value)
                        5 -> textTransformer.removeEmptyLines(_mainText.value)
                        6 -> textTransformer.removeDuplicateWords(
                            _mainText.value,
                            ignoreCase = true
                        )

                        7 -> textTransformer.removeDuplicateWords(
                            _mainText.value,
                            ignoreCase = false
                        )

                        8 -> textTransformer.removeDuplicateLines(
                            _mainText.value,
                            ignoreCase = true
                        )

                        9 -> textTransformer.removeDuplicateLines(
                            _mainText.value,
                            ignoreCase = false
                        )

                        else -> _mainText.value
                    }
                }

                INDEX_ADD_PREFIX_SUFFIX -> {
                    // Add prefix or suffix
                    when (_selectedSecondaryIndex.value) {
                        0 -> {
                            textTransformer.addPrefix(_mainText.value, _secondaryFunctionText.value)
                        }

                        1 -> {
                            textTransformer.addSuffix(_mainText.value, _secondaryFunctionText.value)
                        }

                        else -> {
                            _mainText.value
                        }
                    }
                }

                INDEX_NUMBER_LINES -> {
                    // Number lines
                    textTransformer.numberLines(_mainText.value)
                }

                INDEX_REVERSE_TEXT -> {
                    // Reverse text
                    textTransformer.reverseText(_mainText.value)
                }

                INDEX_REVERSE_WORDS -> {
                    // Reverse words
                    textTransformer.reverseWords(_mainText.value)
                }

                INDEX_PREPEND_LINES -> {
                    // Prepend text
                    textTransformer.prependLines(_mainText.value, _secondaryFunctionText.value)
                }

                INDEX_APPEND_LINES -> {
                    // Prepend text
                    textTransformer.appendLines(_mainText.value, _secondaryFunctionText.value)
                }

                INDEX_REVERSE_LINES -> {
                    // Reverse lines
                    textTransformer.reverseLines(_mainText.value)
                }

                INDEX_DECORATE_TEXT -> {
                    try {
                        when (_selectedSecondaryIndex.value) {
                            // TODO: We need to identify the exact formatting, strip and reformat
                            0 -> textTransformer.boldSerif(_mainText.value)
                            1 -> textTransformer.italicSerif(_mainText.value)
                            2 -> textTransformer.boldItalicSerif(_mainText.value)
                            3 -> textTransformer.boldSans(_mainText.value)
                            4 -> textTransformer.italicSans(_mainText.value)
                            5 -> textTransformer.boldItalicSans(_mainText.value)
                            6 -> textTransformer.shortStrikethrough(_mainText.value)
                            7 -> textTransformer.longStrikethrough(_mainText.value)
                            8 -> textTransformer.cursive(_mainText.value)
                            else -> _mainText.value
                        }
                    } catch (e: ArrayIndexOutOfBoundsException) {
                        _decorateTextErrorFlow.emit(true)
                        _mainText.value
                    }
                }

                INDEX_LINE_BREAK -> {
                    when (_selectedSecondaryIndex.value) {
                        0 -> textTransformer.lineBreakByCharacter(
                            _mainText.value,
                            _secondaryFunctionText.value.toIntOrNull() ?: 0
                        )

                        1 -> textTransformer.lineBreakByWords(
                            _mainText.value,
                            _secondaryFunctionText.value.toIntOrNull() ?: 0
                        )

                        else -> _mainText.value
                    }
                }

                INDEX_SQUEEZE -> {
                    textTransformer.squeeze(_mainText.value, _secondaryFunctionText.value.toIntOrNull() ?: 0)
                }

                INDEX_REPLACE_WHITESPACE -> {
                    _mainText.value.replace(" ", _secondaryFunctionText.value)
                }

                else -> {
                    _mainText.value
                }
            }
        }
    }

    companion object {
        // Index mapping
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

        private val optionsWithSecondaryFunctionText = listOf(
            INDEX_WRAP_TEXT,
            INDEX_REPEAT_TEXT,
            INDEX_REMOVE_TEXT,
            INDEX_ADD_PREFIX_SUFFIX,
            INDEX_LINE_BREAK,
            INDEX_SQUEEZE,
            INDEX_REPLACE_WHITESPACE,
            INDEX_PREPEND_LINES,
            INDEX_APPEND_LINES,
        )

        val transformOptions = listOf(
            R.string.none,
            R.string.wrap_text,
            R.string.change_case,
            R.string.sort_lines,
            R.string.repeat_text,
            R.string.remove_text,
            R.string.add_prefix_suffix,
            R.string.number_lines,
            R.string.prepend_lines,
            R.string.append_lines,
            R.string.reverse_text,
            R.string.reverse_words,
            R.string.reverse_lines,
            R.string.text_decorate,
            R.string.line_break,
            R.string.squeeze,
            R.string.replace_whitespace
        )

        private val wrapOptions = listOf(
            R.string.wrap_single_inverted,
            R.string.wrap_double_inverted,
            R.string.wrap_first_bracket,
            R.string.wrap_curly_braces,
            R.string.wrap_square_bracket,
            R.string.custom_wrapper_text
        )

        private val caseOptions = listOf(
            R.string.upper_case,
            R.string.lower_case,
            R.string.title_case_first_word_only,
            R.string.title_case_all_words,
            R.string.random_case
        )

        private val removeOptions = listOf(
            R.string.remove_first,
            R.string.remove_last,
            R.string.remove_all,
            R.string.remove_whitespaces,
            R.string.remove_line_breaks,
            R.string.remove_empty_lines,
            R.string.remove_duplicate_words,
            R.string.remove_duplicate_words_case_sensitive,
            R.string.remove_duplicate_lines,
            R.string.remove_duplicate_lines_case_sensitive
        )

        private val prefixSuffixOptions = listOf(
            R.string.prefix,
            R.string.suffix
        )

        private val decorateOptions = listOf(
            R.string.bold_serif,
            R.string.italic_serif,
            R.string.bold_italic_serif,
            R.string.bold_sans,
            R.string.italic_sans,
            R.string.bold_italic_sans,
            R.string.strikethrough_short,
            R.string.strikethrough_long,
            R.string.cursive,
        )

        private val lineBreakOptions = listOf(
            R.string.after_certain_characters,
            R.string.after_certain_words
        )
    }
}