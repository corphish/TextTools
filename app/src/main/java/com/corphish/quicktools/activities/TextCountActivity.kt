package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.corphish.quicktools.R
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2
import kotlinx.coroutines.launch

class TextCountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {

            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
            setContent {
                QuickToolsTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        TextCount(
                            text = text,
                            onCancel = { finish() },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TextCount(text: String, onCancel: () -> Unit, modifier: Modifier = Modifier) {
    val viewModel = viewModel { TextCountViewModel() }
    var inputText by remember { mutableStateOf(text) }

    LaunchedEffect(true) {
        viewModel.process(text)
    }

    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (
            headerRef,
            textInputRef,
            statsLabelRef,
            charCountRef,
            wordCountRef,
            letterCountRef,
            digitCountRef,
            spaceCountRef,
            symbolCountRef,
            frequencyRef
        ) = createRefs()

        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(headerRef) {
                start.linkTo(parent.start, margin = 16.dp)
                top.linkTo(parent.top, margin = 16.dp)
            }
        ) {
            IconButton(
                onClick = { onCancel() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = "",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Text(
                text = stringResource(id = R.string.text_count),
                style = TypographyV2.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp),
                maxLines = 1
            )
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = {
                inputText = it
                viewModel.process(it)
            },
            modifier = Modifier.constrainAs(textInputRef) {
                top.linkTo(headerRef.bottom, margin = 16.dp)
                bottom.linkTo(statsLabelRef.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            },
            label = { Text(text = stringResource(id = R.string.input)) },
        )

        Text(
            text = stringResource(id = R.string.statistics),
            style = TypographyV2.labelSmall,
            modifier = Modifier
                .padding(top = 16.dp)
                .constrainAs(statsLabelRef) {
                    start.linkTo(parent.start, margin = 16.dp)
                    bottom.linkTo(charCountRef.top, margin = 8.dp)
                },
            fontFamily = BrandFontFamily,
            color = MaterialTheme.colorScheme.primary,
        )

        // Character count and word count
        Card(
            modifier = modifier.constrainAs(charCountRef) {
                bottom.linkTo(letterCountRef.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(wordCountRef.start, margin = 4.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Column(modifier = modifier.padding(all = 8.dp)) {
                Text(
                    text = viewModel.characterCount.toString(),
                    style = TypographyV2.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.characters),
                    style = TypographyV2.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )
            }
        }

        Card(
            modifier = modifier.constrainAs(wordCountRef) {
                bottom.linkTo(letterCountRef.top, margin = 8.dp)
                start.linkTo(charCountRef.end, margin = 4.dp)
                end.linkTo(parent.end, margin = 16.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Column(modifier = modifier.padding(all = 8.dp)) {
                Text(
                    text = viewModel.wordCount.toString(),
                    style = TypographyV2.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.words),
                    style = TypographyV2.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )
            }
        }

        // Letter count and digit count
        Card(
            modifier = modifier.constrainAs(letterCountRef) {
                bottom.linkTo(spaceCountRef.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(digitCountRef.start, margin = 4.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Column(modifier = modifier.padding(all = 8.dp)) {
                Text(
                    text = viewModel.letterCount.toString(),
                    style = TypographyV2.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.letters),
                    style = TypographyV2.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )
            }
        }

        Card(
            modifier = modifier.constrainAs(digitCountRef) {
                bottom.linkTo(spaceCountRef.top, margin = 8.dp)
                start.linkTo(letterCountRef.end, margin = 4.dp)
                end.linkTo(parent.end, margin = 16.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Column(modifier = modifier.padding(all = 8.dp)) {
                Text(
                    text = viewModel.digitCount.toString(),
                    style = TypographyV2.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.digits),
                    style = TypographyV2.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )
            }
        }

        // Spaces count and symbol count
        Card(
            modifier = modifier.constrainAs(spaceCountRef) {
                bottom.linkTo(frequencyRef.top, margin = 16.dp)
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(symbolCountRef.start, margin = 4.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Column(modifier = modifier.padding(all = 8.dp)) {
                Text(
                    text = viewModel.spaceCount.toString(),
                    style = TypographyV2.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.spaces),
                    style = TypographyV2.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )
            }
        }

        Card(
            modifier = modifier.constrainAs(symbolCountRef) {
                bottom.linkTo(frequencyRef.top, margin = 16.dp)
                start.linkTo(spaceCountRef.end, margin = 4.dp)
                end.linkTo(parent.end, margin = 16.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Column(modifier = modifier.padding(all = 8.dp)) {
                Text(
                    text = viewModel.symbolCount.toString(),
                    style = TypographyV2.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )

                Text(
                    text = stringResource(R.string.symbols),
                    style = TypographyV2.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = modifier.fillMaxWidth()
                )
            }
        }

        Column(
            modifier = Modifier
                .constrainAs(frequencyRef) {
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.word_frequency),
                style = TypographyV2.labelSmall,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                fontFamily = BrandFontFamily,
                color = MaterialTheme.colorScheme.primary
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.wordFrequency) {
                    Card {
                        Column(modifier = modifier
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                            .fillMaxWidth()) {
                            Text(
                                text = it.second.toString(),
                                style = TypographyV2.headlineSmall,
                                textAlign = TextAlign.Center,
                                modifier = modifier.fillMaxWidth()
                            )

                            Text(
                                text = it.first,
                                style = TypographyV2.labelSmall,
                                textAlign = TextAlign.Center,
                                modifier = modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextCountPreview() {
    QuickToolsTheme {
        TextCount("Hello Android", onCancel = {})
    }
}

class TextCountViewModel : ViewModel() {
    // Text count features
    var characterCount by mutableIntStateOf(0)
        private set
    var letterCount by mutableIntStateOf(0)
        private set
    var digitCount by mutableIntStateOf(0)
        private set
    var wordCount by mutableIntStateOf(0)
        private set
    var spaceCount by mutableIntStateOf(0)
        private set
    var symbolCount by mutableIntStateOf(0)
        private set
    var wordFrequency by mutableStateOf(listOf<Pair<String, Int>>())
        private set

    fun process(text: String) {
        viewModelScope.launch {
            val freq = mutableMapOf<String, Int>()
            val list = mutableListOf<Pair<String, Int>>()

            characterCount = 0
            letterCount = 0
            digitCount = 0
            wordCount = 0
            spaceCount = 0
            symbolCount = 0
            wordFrequency = mutableListOf()

            characterCount = text.length
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

            for (w in text.split(" ")) {
                if (w.isEmpty()) {
                    continue
                }

                wordCount += 1
                freq[w] = (freq[w] ?: 0) + 1
            }

            for (e in freq.entries) {
                list += e.key to e.value
            }

            list.sortBy { -it.second }
            wordFrequency = list.toList()
        }
    }
}