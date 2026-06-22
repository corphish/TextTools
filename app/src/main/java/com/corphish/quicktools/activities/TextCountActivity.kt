package com.corphish.quicktools.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.corphish.quicktools.R
import com.corphish.quicktools.data.TextCountResult
import com.corphish.quicktools.ui.common.CustomTopAppBar
import com.corphish.quicktools.ui.common.PieChart
import com.corphish.quicktools.ui.common.PieChartData
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
        val initialText = if (savedInstanceState == null && intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
        } else {
            ""
        }
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
                        text = initialText,
                        innerPadding = innerPadding
                    )
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
    val context = LocalContext.current

    LaunchedEffect(text) {
        if (text.isNotEmpty()) {
            viewModel.setTextAndProcess(text)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                bottom = innerPadding.calculateBottomPadding()
            )
    ) {
        val isWide = maxWidth >= 800.dp

        if (isWide) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    InputSection(inputText) { viewModel.setTextAndProcess(it) }
                }
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                        .testTag("stats_list"),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    statsContent(countResult, context)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .testTag("stats_list"),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    InputSection(inputText) { viewModel.setTextAndProcess(it) }
                }
                statsContent(countResult, context)
            }
        }
    }
}

@Composable
fun InputSection(text: String, onValueChange: (String) -> Unit) {
    Column {
        Text(
            text = stringResource(id = R.string.input),
            style = TypographyV2.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            TextField(
                value = text,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxSize(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                placeholder = { Text(text = stringResource(R.string.input)) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
fun LazyListScope.statsContent(result: TextCountResult, context: Context) {
    item {
        Text(
            text = stringResource(id = R.string.statistics),
            style = TypographyV2.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = BrandFontFamily
        )
    }

    item {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            FlowRow(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatItem(label = stringResource(R.string.characters), value = result.characterCount.toString())
                StatItem(label = stringResource(R.string.words), value = result.wordCount.toString())
                StatItem(label = stringResource(R.string.letters), value = result.letterCount.toString())
                StatItem(label = stringResource(R.string.digits), value = result.digitCount.toString())
                StatItem(label = stringResource(R.string.spaces), value = result.spaceCount.toString())
                StatItem(label = stringResource(R.string.symbols), value = result.symbolCount.toString())
            }
        }
    }

    item {
        Text(
            text = stringResource(R.string.visualizations),
            style = TypographyV2.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

    item {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                PieChart(
                    data = listOf(
                        PieChartData(stringResource(R.string.letters), result.letterCount.toFloat(), Color(0xFF6200EE)),
                        PieChartData(stringResource(R.string.digits), result.digitCount.toFloat(), Color(0xFF03DAC6)),
                        PieChartData(stringResource(R.string.spaces), result.spaceCount.toFloat(), Color(0xFFBB86FC)),
                        PieChartData(stringResource(R.string.symbols), result.symbolCount.toFloat(), Color(0xFF3700B3))
                    ),
                    modifier = Modifier.size(150.dp)
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    LegendItem(stringResource(R.string.letters), Color(0xFF6200EE))
                    LegendItem(stringResource(R.string.digits), Color(0xFF03DAC6))
                    LegendItem(stringResource(R.string.spaces), Color(0xFFBB86FC))
                    LegendItem(stringResource(R.string.symbols), Color(0xFF3700B3))
                }
            }
        }
    }

    if (result.wordFrequency.isNotEmpty()) {
        item {
            Text(
                stringResource(R.string.word_frequency),
                style = TypographyV2.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        item {
            val toastText = stringResource(R.string.copied_to_clipboard)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val topWords = result.wordFrequency.take(8)
                        val colors = listOf(
                            Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0),
                            Color(0xFF673AB7), Color(0xFF3F51B5), Color(0xFF2196F3),
                            Color(0xFF03A9F4), Color(0xFF00BCD4)
                        )
                        PieChart(
                            data = topWords.mapIndexed { index, pair ->
                                PieChartData(
                                    pair.first,
                                    pair.second.toFloat(),
                                    colors[index % colors.size]
                                )
                            },
                            modifier = Modifier.size(150.dp)
                        )
                        Column(modifier = Modifier.padding(start = 16.dp)) {
                            topWords.forEachIndexed { index, pair ->
                                LegendItem("${pair.first} (${pair.second})", colors[index % colors.size])
                            }
                        }
                    }

                    Text(
                        stringResource(R.string.all_words),
                        style = TypographyV2.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        result.wordFrequency.forEach { (word, count) ->
                            SuggestionChip(
                                onClick = { copyToClipboard(context, word, toastText) },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = word)
                                        Spacer(Modifier.size(4.dp))
                                       Badge(
                                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                                            contentColor = MaterialTheme.colorScheme.primary
                                        ) {
                                            Text(
                                                text = count.toString(),
                                                style = TypographyV2.labelSmall
                                            )
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    item {
        Text(
            text = stringResource(R.string.advanced_features),
            style = TypographyV2.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = BrandFontFamily,
            modifier = Modifier.padding(top = 16.dp)
        )
    }

    item {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column {
                FeatureItem(stringResource(R.string.longest_repeated_substring), result.longestRepeatedSubstring, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureItem(stringResource(R.string.run_length_encoding), result.runLengthEncoding, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureItem(stringResource(R.string.longest_palindrome), result.longestPalindrome, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureItem(stringResource(R.string.longest_increasing_subsequence), result.longestIncreasingSubsequence, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureListItems(stringResource(R.string.emails), result.emails, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureListItems(stringResource(R.string.phone_numbers), result.phoneNumbers, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureListItems(stringResource(R.string.urls), result.urls, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureListItems(stringResource(R.string.ipv4_addresses), result.ipv4Addresses, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureListItems(stringResource(R.string.ipv6_addresses), result.ipv6Addresses, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureListItems(stringResource(R.string.dates), result.dates, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureListItems(stringResource(R.string.times), result.times, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureListItems(stringResource(R.string.currencies), result.currencies, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureListItems(stringResource(R.string.binary_texts), result.binaryTexts, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureListItems(stringResource(R.string.hex_texts), result.hexTexts, context)
                HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 2.dp)
                FeatureListItems(stringResource(R.string.json_blocks), result.jsonTexts, context)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = value, style = TypographyV2.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = label, style = TypographyV2.labelSmall)
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Canvas(modifier = Modifier.size(12.dp)) {
            drawCircle(color)
        }
        Text(text = label, style = TypographyV2.labelSmall, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun FeatureItem(label: String, value: String, context: Context) {
    val toastText = stringResource(R.string.copied_to_clipboard)
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = label, style = TypographyV2.labelSmall, color = MaterialTheme.colorScheme.secondary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value.ifEmpty { stringResource(R.string.none) },
                style = TypographyV2.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            if (value.isNotEmpty()) {
                IconButton(onClick = { copyToClipboard(context, value, toastText) }) {
                    Icon(painterResource(R.drawable.ic_copy), contentDescription = "Copy", modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun FeatureListItems(label: String, values: List<String>, context: Context) {
    val toastText = stringResource(R.string.copied_to_clipboard)
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = label, style = TypographyV2.labelSmall, color = MaterialTheme.colorScheme.secondary)
        if (values.isEmpty()) {
            Text(text = stringResource(R.string.none), style = TypographyV2.bodyMedium)
        } else {
            values.forEach { value ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = value, style = TypographyV2.bodyMedium, modifier = Modifier.weight(1f))
                    IconButton(onClick = { copyToClipboard(context, value, toastText) }) {
                        Icon(painterResource(R.drawable.ic_copy), contentDescription = "Copy", modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

fun copyToClipboard(context: Context, text: String, toastText: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true)
@Composable
fun TextCountPreview() {
    QuickToolsTheme {
        TextCount("Hello Android")
    }
}
