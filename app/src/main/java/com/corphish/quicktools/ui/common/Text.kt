package com.corphish.quicktools.ui.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.corphish.quicktools.R
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.text.EmphasisTextScope
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.body
import com.tabotabo.ui.typography.label

/** Line height relative to font size for the input and preview text. */
private const val TEXT_LINE_HEIGHT_FACTOR = 1.4f

/**
 * Input text card and preview text card, stacked in portrait and side by side in landscape.
 * The preview card is highlighted once there is something to preview.
 *
 * @param modifier Modifier for the container.
 * @param inputText Current input text.
 * @param previewText Text to preview; a placeholder is shown when empty.
 * @param onInputTextChanged Called when the user edits the input.
 * @param showInput Whether to show the input card.
 * @param showPreview Whether to show the preview card.
 * @param isLandscape Whether to place the cards side by side.
 */
@Composable
fun InputAndPreviewTextField(
    modifier: Modifier = Modifier,
    inputText: String,
    previewText: String,
    onInputTextChanged: (String) -> Unit = {},
    showInput: Boolean = true,
    showPreview: Boolean = true,
    isLandscape: Boolean = false,
) {
    val spacing = TabotaboTheme.dimensions.spacing
    val fontSize = determineFontSizeForWordCount(inputText)
    val containerModifier = modifier
        .animateContentSize()
        .fillMaxSize()
        .padding(all = spacing.l)

    if (isLandscape) {
        Row(modifier = containerModifier, horizontalArrangement = Arrangement.spacedBy(spacing.l)) {
            if (showInput) {
                InputCard(inputText, onInputTextChanged, fontSize, Modifier.weight(1f).fillMaxHeight())
            }
            if (showPreview) {
                PreviewCard(previewText, fontSize, Modifier.weight(1f).fillMaxHeight())
            }
        }
    } else {
        Column(modifier = containerModifier, verticalArrangement = Arrangement.spacedBy(spacing.l)) {
            if (showInput) {
                InputCard(inputText, onInputTextChanged, fontSize, Modifier.weight(1f).fillMaxWidth())
            }
            if (showPreview) {
                PreviewCard(previewText, fontSize, Modifier.weight(1f).fillMaxWidth())
            }
        }
    }
}

/**
 * Card holding an editable input text with a label and placeholder.
 *
 * @param inputText Current input text.
 * @param onInputTextChanged Called when the user edits the input.
 * @param fontSize Font size of the input text.
 * @param modifier Modifier for the card.
 */
@Composable
private fun InputCard(
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    fontSize: TextUnit,
    modifier: Modifier,
) {
    val colors = TabotaboTheme.colors
    val textStyle = contentTextStyle(fontSize)

    GlowCard(modifier = modifier) {
        CardLabel(stringResource(R.string.input))

        Box(modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
            if (inputText.isEmpty()) {
                Text(stringResource(R.string.enter_text_here), style = textStyle, color = colors.muted)
            }

            BasicTextField(
                value = inputText,
                onValueChange = onInputTextChanged,
                textStyle = textStyle.copy(color = colors.accent),
                cursorBrush = SolidColor(colors.primary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Card showing the preview text with a label, highlighted when [previewText] is not empty.
 *
 * @param previewText Text to preview; a placeholder is shown when empty.
 * @param fontSize Font size of the preview text.
 * @param modifier Modifier for the card.
 */
@Composable
private fun PreviewCard(
    previewText: String,
    fontSize: TextUnit,
    modifier: Modifier,
) {
    val colors = TabotaboTheme.colors
    val isEmpty = previewText.isEmpty()

    GlowCard(modifier = modifier, highlighted = !isEmpty) {
        CardLabel(stringResource(R.string.preview))

        Text(
            text = if (isEmpty) stringResource(R.string.preview_text_placeholder) else previewText,
            style = contentTextStyle(fontSize),
            color = if (isEmpty) colors.muted else colors.accent,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        )
    }
}

/**
 * Small primary-colored label at the top of a card.
 *
 * @param text Label text.
 */
@Composable
private fun ColumnScope.CardLabel(text: String) {
    Text(text, style = TabotaboTheme.typography.label, color = TabotaboTheme.colors.primary)
    Spacer(modifier = Modifier.height(TabotaboTheme.dimensions.spacing.xs))
}

/**
 * Body text style at [fontSize] with a matching line height.
 *
 * @param fontSize Font size to apply.
 */
@Composable
private fun contentTextStyle(fontSize: TextUnit): TextStyle =
    TabotaboTheme.typography.body.copy(fontSize = fontSize, lineHeight = fontSize * TEXT_LINE_HEIGHT_FACTOR)

/**
 * Picks a font size for [text] that shrinks as the word count grows.
 *
 * @param text Text to measure.
 * @return 24sp for up to 3 words, 20sp up to 6, 16sp up to 10 and 14sp beyond.
 */
fun determineFontSizeForWordCount(
    text: String
): TextUnit {
    // Split words (handle multiple spaces safely)
    val wordCount = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size

    // Determine font size based on number of words
    return when {
        wordCount <= 3 -> 24.sp
        wordCount <= 6 -> 20.sp
        wordCount <= 10 -> 16.sp
        else -> 14.sp
    }
}

/**
 * Single-line text that scrolls horizontally when it overflows.
 *
 * @param text Text to show.
 * @param fontFamily Font family of the text.
 */
@Composable
fun MarqueeText(text: String, fontFamily: FontFamily) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Clip,
        modifier = Modifier.basicMarquee(),
        fontFamily = fontFamily,
    )
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
