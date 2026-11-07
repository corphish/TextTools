package com.corphish.quicktools.viewmodels

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Result
import com.corphish.quicktools.data.TextInputType
import com.corphish.quicktools.data.TextTransformIndex.INDEX_ADD_PREFIX_SUFFIX
import com.corphish.quicktools.data.TextTransformIndex.INDEX_APPEND_LINES
import com.corphish.quicktools.data.TextTransformIndex.INDEX_CHANGE_CASE
import com.corphish.quicktools.data.TextTransformIndex.INDEX_DECORATE_TEXT
import com.corphish.quicktools.data.TextTransformIndex.INDEX_LINE_BREAK
import com.corphish.quicktools.data.TextTransformIndex.INDEX_PREPEND_LINES
import com.corphish.quicktools.data.TextTransformIndex.INDEX_REMOVE_TEXT
import com.corphish.quicktools.data.TextTransformIndex.INDEX_REPEAT_TEXT
import com.corphish.quicktools.data.TextTransformIndex.INDEX_REPLACE_WHITESPACE
import com.corphish.quicktools.data.TextTransformIndex.INDEX_SQUEEZE
import com.corphish.quicktools.data.TextTransformIndex.INDEX_WRAP_TEXT
import com.corphish.quicktools.usecases.TextTransformUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextTransformViewModel @Inject constructor (
    private val textTransformUseCase: TextTransformUseCase,
) : ViewModel() {

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

    private val _secondaryFunctionTextInputType = MutableStateFlow(TextInputType.TEXT)
    val secondaryFunctionTextInputType = _secondaryFunctionTextInputType.asStateFlow()

    private val _secondaryFunctionTextEnabled = MutableStateFlow(true)
    val secondaryFunctionTextEnabled = _secondaryFunctionTextEnabled.asStateFlow()

    private val _secondaryFunctionTextVisible = MutableStateFlow(false)
    val secondaryFunctionTextVisible = _secondaryFunctionTextVisible.asStateFlow()

    private val _decorateTextErrorFlow = MutableSharedFlow<Boolean>()
    val decorateTextErrorFlow: SharedFlow<Boolean> = _decorateTextErrorFlow

    private fun determineSecondaryOptions() {
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

    fun initializeText(text: String) {
        _mainText.value = text
        _previewText.value = text
        transform()
    }

    fun selectPrimaryIndex(index: Int) {
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

    fun selectSecondaryIndex(index: Int) {
        _selectedSecondaryIndex.value = index
        determineSecondaryFunctionTextProperties()
        transform()
    }

    fun setSecondaryText(text: String) {
        _secondaryFunctionText.value = text
        transform()
    }

    private fun determineSecondaryFunctionTextProperties() {
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
                TextInputType.NUMBER
            } else {
                TextInputType.TEXT
            }

        // Disable when remove option is selected for preset characters, or for custom wrap
        _secondaryFunctionTextEnabled.value =
            !((_selectedPrimaryIndex.value == INDEX_REMOVE_TEXT && _selectedSecondaryIndex.value > 2) ||
                _selectedPrimaryIndex.value == INDEX_WRAP_TEXT && _selectedSecondaryIndex.value != 5)
    }

    private fun transform() {
        viewModelScope.launch {
            when (val result = textTransformUseCase.execute(
                _mainText.value,
                _selectedPrimaryIndex.value,
                _selectedSecondaryIndex.value,
                _secondaryFunctionText.value
            )) {
                is Result.Success -> _previewText.value = result.value
                is Result.Error -> {
                    _decorateTextErrorFlow.emit(true)
                    _previewText.value = _mainText.value
                }
                is Result.Initial -> Unit
            }
        }
    }

    companion object {
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
            R.string.monospace,
            R.string.none,
        )

        private val lineBreakOptions = listOf(
            R.string.after_certain_characters,
            R.string.after_certain_words
        )
    }
}
