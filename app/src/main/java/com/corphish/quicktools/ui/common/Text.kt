package com.corphish.quicktools.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.text.EmphasisTextScope
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.body
import com.tabotabo.ui.typography.label

/** Line height relative to font size for text sized by [determineFontSizeForWordCount]. */
private const val TEXT_LINE_HEIGHT_FACTOR = 1.4f

/**
 * Body text style whose size shrinks as the word count of [text] grows, so short text is easy
 * to read and long text still fits.
 *
 * @param text Text the style is for.
 */
@Composable
fun wordCountTextStyle(text: String): TextStyle {
    val fontSize = determineFontSizeForWordCount(text)
    val body = TabotaboTheme.typography.body
    return remember(body, fontSize) {
        body.copy(fontSize = fontSize, lineHeight = fontSize * TEXT_LINE_HEIGHT_FACTOR)
    }
}

/** Word count above which [determineFontSizeForWordCount] uses its smallest size. */
private const val LARGEST_WORD_COUNT_TIER = 10

/**
 * Picks a font size for [text] that shrinks as the word count grows. Runs on every edit, so it
 * counts words in place and stops once the count passes the last tier, instead of splitting
 * the whole text.
 *
 * @param text Text to measure.
 * @return 24sp for up to 3 words, 20sp up to 6, 16sp up to 10 and 14sp beyond.
 */
fun determineFontSizeForWordCount(
    text: String
): TextUnit {
    val wordCount = countWords(text, limit = LARGEST_WORD_COUNT_TIER + 1)

    return when {
        wordCount <= 3 -> 24.sp
        wordCount <= 6 -> 20.sp
        wordCount <= LARGEST_WORD_COUNT_TIER -> 16.sp
        else -> 14.sp
    }
}

/**
 * Counts whitespace-separated words in [text], stopping early at [limit].
 *
 * @param text Text to count words in.
 * @param limit Count at which to stop scanning.
 * @return Number of words, at most [limit].
 */
internal fun countWords(text: String, limit: Int = Int.MAX_VALUE): Int {
    var count = 0
    var inWord = false
    for (char in text) {
        if (char.isWhitespace()) {
            inWord = false
        } else if (!inWord) {
            inWord = true
            if (++count >= limit) return count
        }
    }
    return count
}

/**
 * Splits [text] into everything before its last word and the last word itself, so the last
 * word can be emphasised. Text without whitespace (single words, or scripts written without
 * spaces) is returned whole as the last word.
 *
 * @param text Text to split.
 * @return Pair of (leading text, last word); the leading text is empty when there is one word.
 */
fun splitForEmphasis(text: String): Pair<String, String> {
    val trimmed = text.trim()
    val index = trimmed.indexOfLast { it.isWhitespace() }
    return if (index < 0) "" to trimmed else trimmed.substring(0, index).trimEnd() to trimmed.substring(index + 1)
}

/**
 * Appends [text] to an emphasis line with its last word focused, e.g. "Find and **replace**".
 *
 * @param text Text to append; see [splitForEmphasis] for how it is split.
 */
fun EmphasisTextScope.FocusedLastWord(text: String) {
    val (leading, last) = splitForEmphasis(text)
    if (leading.isNotEmpty()) UnfocusedText(leading)
    FocusedText(last)
}

/**
 * Material3 text field without its own container or indicator, placed inside an outlined card.
 *
 * @param value Current text.
 * @param onValueChange Called when the user edits the text.
 * @param modifier Modifier for the card.
 * @param textFieldModifier Modifier for the text field itself (e.g. a test tag).
 * @param label Optional floating label.
 * @param placeholder Optional placeholder shown while empty.
 * @param singleLine Whether the field is limited to one line.
 * @param highlighted Whether the card glows.
 * @param suffix Optional trailing content inside the field.
 * @param visualTransformation Transformation applied to the shown text.
 * @param keyboardOptions Keyboard configuration.
 * @param textStyle Style of the entered text.
 */
@Composable
fun CardTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    singleLine: Boolean = false,
    highlighted: Boolean = false,
    suffix: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    val transparent = Color.Transparent

    GlowCard(modifier = modifier, highlighted = highlighted, contentPadding = PaddingValues()) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = textFieldModifier.fillMaxWidth(),
            label = label?.let { { Text(it) } },
            placeholder = placeholder?.let { { Text(it) } },
            singleLine = singleLine,
            suffix = suffix,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            textStyle = textStyle,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = transparent,
                unfocusedContainerColor = transparent,
                disabledContainerColor = transparent,
                errorContainerColor = transparent,
                focusedIndicatorColor = transparent,
                unfocusedIndicatorColor = transparent,
                disabledIndicatorColor = transparent,
            ),
        )
    }
}

/**
 * Small primary-colored label introducing a section of a screen.
 *
 * @param text Label text.
 * @param modifier Modifier for the label.
 */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = TabotaboTheme.typography.label,
        color = TabotaboTheme.colors.primary,
        modifier = modifier.padding(top = TabotaboTheme.dimensions.spacing.s)
    )
}
