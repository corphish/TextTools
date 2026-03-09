package com.corphish.quicktools.ui.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.corphish.quicktools.R
import com.corphish.quicktools.ui.theme.TypographyV2

@Composable
fun InputAndPreviewTextField(
    modifier: Modifier = Modifier,
    inputText: String,
    previewText: String,
    onInputTextChanged: (String) -> Unit = {},
) {
    val fontSize = determineFontSizeForWordCount(inputText)

    Column(
        modifier = modifier
            .animateContentSize()
            .fillMaxSize()
            .padding(all = 16.dp)
    ) {
        Text(
            stringResource(R.string.input),
            style = TypographyV2.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))

        Box(modifier = modifier) {
            if (inputText.isEmpty()) {
                Text(
                    stringResource(R.string.enter_text_here),
                    style = LocalTextStyle.current.copy(fontSize = fontSize),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }

            BasicTextField(
                value = inputText,
                onValueChange = onInputTextChanged,
                textStyle = LocalTextStyle.current.copy(fontSize = fontSize, color = MaterialTheme.colorScheme.onSurface),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(
            thickness = 4.dp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .width(32.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            stringResource(R.string.preview),
            style = TypographyV2.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))

        if (previewText.isEmpty()) {
            Text(
                stringResource(R.string.preview_text_placeholder),
                style = LocalTextStyle.current.copy(fontSize = fontSize),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .fillMaxWidth()
            )
        } else {
            Text(
                previewText,
                style = LocalTextStyle.current.copy(fontSize = fontSize),
                modifier = Modifier
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

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