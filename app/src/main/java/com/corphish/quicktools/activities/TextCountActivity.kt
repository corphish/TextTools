package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.corphish.quicktools.R
import com.corphish.quicktools.ui.common.CustomTopAppBar
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2
import com.corphish.quicktools.viewmodels.TextCountViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextCountActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {

            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
            setContent {
                QuickToolsTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            CustomTopAppBar(
                                id = R.string.text_count,
                                onNavigationClick = { finish() }
                            )
                        }
                    ) { innerPadding ->
                        TextCount(
                            text = text,
                            innerPadding = innerPadding
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TextCount(
    text: String,
    innerPadding: PaddingValues = PaddingValues(),
    viewModel: TextCountViewModel = hiltViewModel()
) {
    val inputText by viewModel.text.collectAsState()
    val countResult by viewModel.countResult.collectAsState()

    val modifier = Modifier

    LaunchedEffect(true) {
        viewModel.setTextAndProcess(text)
    }

    ConstraintLayout(modifier = modifier.fillMaxSize()) {
        val (
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

        OutlinedTextField(
            value = inputText,
            onValueChange = {
                viewModel.setTextAndProcess(it)
            },
            modifier = Modifier.constrainAs(textInputRef) {
                top.linkTo(parent.top, margin = innerPadding.calculateTopPadding().plus(16.dp))
                bottom.linkTo(statsLabelRef.top, margin = 8.dp)
                start.linkTo(parent.start, margin = innerPadding.calculateStartPadding(LayoutDirection.Ltr).plus(16.dp))
                end.linkTo(parent.end, margin = innerPadding.calculateEndPadding(LayoutDirection.Ltr).plus(16.dp))
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
                    start.linkTo(parent.start, margin = innerPadding.calculateStartPadding(LayoutDirection.Ltr).plus(16.dp))
                    bottom.linkTo(charCountRef.top, margin = 8.dp)
                },
            fontFamily = BrandFontFamily,
            color = MaterialTheme.colorScheme.primary,
        )

        // Character count and word count
        Card(
            modifier = modifier.constrainAs(charCountRef) {
                bottom.linkTo(letterCountRef.top, margin = 8.dp)
                start.linkTo(parent.start, margin = innerPadding.calculateStartPadding(LayoutDirection.Ltr).plus(16.dp))
                end.linkTo(wordCountRef.start, margin = 4.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Column(modifier = modifier.padding(all = 8.dp)) {
                Text(
                    text = countResult.characterCount.toString(),
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
                end.linkTo(parent.end, margin = innerPadding.calculateEndPadding(LayoutDirection.Ltr).plus(16.dp))
                width = Dimension.fillToConstraints
            }
        ) {
            Column(modifier = modifier.padding(all = 8.dp)) {
                Text(
                    text = countResult.wordCount.toString(),
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
                start.linkTo(parent.start, margin = innerPadding.calculateStartPadding(LayoutDirection.Ltr).plus(16.dp))
                end.linkTo(digitCountRef.start, margin = 4.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Column(modifier = modifier.padding(all = 8.dp)) {
                Text(
                    text = countResult.letterCount.toString(),
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
                end.linkTo(parent.end, margin = innerPadding.calculateEndPadding(LayoutDirection.Ltr).plus(16.dp))
                width = Dimension.fillToConstraints
            }
        ) {
            Column(modifier = modifier.padding(all = 8.dp)) {
                Text(
                    text = countResult.digitCount.toString(),
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
                start.linkTo(parent.start, margin = innerPadding.calculateStartPadding(LayoutDirection.Ltr).plus(16.dp))
                end.linkTo(symbolCountRef.start, margin = 4.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Column(modifier = modifier.padding(all = 8.dp)) {
                Text(
                    text = countResult.spaceCount.toString(),
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
                end.linkTo(parent.end, margin = innerPadding.calculateEndPadding(LayoutDirection.Ltr).plus(16.dp))
                width = Dimension.fillToConstraints
            }
        ) {
            Column(modifier = modifier.padding(all = 8.dp)) {
                Text(
                    text = countResult.symbolCount.toString(),
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
                    start.linkTo(parent.start, margin = innerPadding.calculateStartPadding(LayoutDirection.Ltr).plus(16.dp))
                    end.linkTo(parent.end, margin = innerPadding.calculateEndPadding(LayoutDirection.Ltr).plus(16.dp))
                    bottom.linkTo(parent.bottom, margin = innerPadding.calculateBottomPadding().plus(16.dp))
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
                items(countResult.wordFrequency) {
                    Card {
                        Column(
                            modifier = modifier
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .fillMaxWidth()
                        ) {
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
        TextCount("Hello Android")
    }
}

