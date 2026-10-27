package com.corphish.quicktools.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.corphish.quicktools.R
import com.corphish.quicktools.data.TextCountResult
import com.corphish.quicktools.ui.common.AppScreenHeader
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.common.CardTextField
import com.corphish.quicktools.ui.common.PieChart
import com.corphish.quicktools.ui.common.PieChartData
import com.corphish.quicktools.ui.common.SectionLabel
import com.corphish.quicktools.ui.common.pieChartColor
import com.corphish.quicktools.ui.theme.AppTheme
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.viewmodels.TextCountViewModel
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.surface.Tag
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.body
import com.tabotabo.ui.typography.caption
import com.tabotabo.ui.typography.factTitle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextCountActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialText = if (savedInstanceState == null && intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
        } else {
            ""
        }
        setContent {
            AppTheme {
                TextCount(
                    text = initialText,
                    onBack = { finish() }
                )
            }
        }
    }
}

/** Height of the input card at the top of the list. */
private val InputHeight = 200.dp

/** Size of the pie charts. */
private val ChartSize = 150.dp

/** Lazy item content type shared by section labels, so their compositions can be reused. */
internal const val CONTENT_TYPE_SECTION_LABEL = "section_label"

/**
 * Text count screen: the input followed by statistics, charts, word frequency and advanced
 * findings, all in one scrolling list.
 *
 * @param text Initial text to analyse.
 * @param onBack Called when the user leaves the screen.
 * @param viewModel Holds the input and its analysis.
 */
@Composable
fun TextCount(
    text: String,
    onBack: () -> Unit = {},
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

    ScreenScaffold(
        header = { AppScreenHeader(id = R.string.title_activity_text_count, onBack = onBack) }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("stats_list"),
            verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m)
        ) {
            item(contentType = "input") {
                InputSection(inputText) { viewModel.setTextAndProcess(it) }
            }
            statsContent(countResult, context)
        }
    }
}

/**
 * Card holding the text to analyse.
 *
 * @param text Current text.
 * @param onValueChange Called when the user edits the text.
 */
@Composable
fun InputSection(text: String, onValueChange: (String) -> Unit) {
    CardTextField(
        value = text,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(InputHeight),
        textFieldModifier = Modifier.fillMaxSize(),
        placeholder = stringResource(R.string.input),
    )
}

/**
 * Adds the analysis sections for [result] to a lazy list.
 *
 * @param result Analysis to show.
 * @param context Used to copy values to the clipboard.
 */
fun LazyListScope.statsContent(result: TextCountResult, context: Context) {
    item(contentType = CONTENT_TYPE_SECTION_LABEL) { SectionLabel(stringResource(id = R.string.statistics), modifier = Modifier.padding(top = TabotaboTheme.dimensions.spacing.l)) }

    item(contentType = "statistics") {
        Column(modifier = Modifier.fillMaxWidth()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.xl),
                verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.l)
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

    item(contentType = CONTENT_TYPE_SECTION_LABEL) { SectionLabel(stringResource(R.string.visualizations), modifier = Modifier.padding(top = TabotaboTheme.dimensions.spacing.l)) }

    item(contentType = "visualizations") {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PieChart(
                    data = listOf(
                        PieChartData(stringResource(R.string.letters), result.letterCount.toFloat()),
                        PieChartData(stringResource(R.string.digits), result.digitCount.toFloat()),
                        PieChartData(stringResource(R.string.spaces), result.spaceCount.toFloat()),
                        PieChartData(stringResource(R.string.symbols), result.symbolCount.toFloat())
                    ),
                    modifier = Modifier.size(ChartSize)
                )
                Column(modifier = Modifier.padding(start = TabotaboTheme.dimensions.spacing.l)) {
                    LegendItem(stringResource(R.string.letters), pieChartColor(0))
                    LegendItem(stringResource(R.string.digits), pieChartColor(1))
                    LegendItem(stringResource(R.string.spaces), pieChartColor(2))
                    LegendItem(stringResource(R.string.symbols), pieChartColor(3))
                }
            }
        }
    }

    if (result.wordFrequency.isNotEmpty()) {
        item(contentType = CONTENT_TYPE_SECTION_LABEL) { SectionLabel(stringResource(R.string.word_frequency), modifier = Modifier.padding(top = TabotaboTheme.dimensions.spacing.l)) }

        item(contentType = "word_frequency") {
            val toastText = stringResource(R.string.copied_to_clipboard)
            val copyLabel = stringResource(R.string.copy)
            val spacing = TabotaboTheme.dimensions.spacing

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val topWords = result.wordFrequency.take(8)
                    PieChart(
                        data = topWords.map { pair ->
                            PieChartData(
                                pair.first,
                                pair.second.toFloat()
                            )
                        },
                        modifier = Modifier.size(ChartSize)
                    )
                    Column(modifier = Modifier.padding(start = spacing.l)) {
                        topWords.forEachIndexed { index, pair ->
                            LegendItem("${pair.first} (${pair.second})", pieChartColor(index))
                        }
                    }
                }

                SectionLabel(
                    stringResource(R.string.all_words),
                    modifier = Modifier.padding(top = spacing.m, bottom = spacing.s)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.s),
                    verticalArrangement = Arrangement.spacedBy(spacing.s)
                ) {
                    result.wordFrequency.forEach { (word, count) ->
                        Tag(
                            text = "$word  $count",
                            color = TabotaboTheme.colors.primary,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable(onClickLabel = copyLabel) {
                                    copyToClipboard(context, word, toastText)
                                }
                        )
                    }
                }
            }
        }
    }

    item(contentType = CONTENT_TYPE_SECTION_LABEL) { SectionLabel(stringResource(R.string.advanced_features), modifier = Modifier.padding(top = TabotaboTheme.dimensions.spacing.l)) }

    item(contentType = "advanced_features") {
        Column(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.l)) {
                FeatureItem(stringResource(R.string.longest_repeated_substring), result.longestRepeatedSubstring, context)
                FeatureItem(stringResource(R.string.run_length_encoding), result.runLengthEncoding, context)
                FeatureItem(stringResource(R.string.longest_palindrome), result.longestPalindrome, context)
                FeatureItem(stringResource(R.string.longest_increasing_subsequence), result.longestIncreasingSubsequence, context)
                FeatureListItems(stringResource(R.string.emails), result.emails, context)
                FeatureListItems(stringResource(R.string.phone_numbers), result.phoneNumbers, context)
                FeatureListItems(stringResource(R.string.urls), result.urls, context)
                FeatureListItems(stringResource(R.string.ipv4_addresses), result.ipv4Addresses, context)
                FeatureListItems(stringResource(R.string.ipv6_addresses), result.ipv6Addresses, context)
                FeatureListItems(stringResource(R.string.dates), result.dates, context)
                FeatureListItems(stringResource(R.string.times), result.times, context)
                FeatureListItems(stringResource(R.string.currencies), result.currencies, context)
                FeatureListItems(stringResource(R.string.binary_texts), result.binaryTexts, context)
                FeatureListItems(stringResource(R.string.hex_texts), result.hexTexts, context)
                FeatureListItems(stringResource(R.string.json_blocks), result.jsonTexts, context)
            }
        }
    }
}

/**
 * One statistic: a large value above its label.
 *
 * @param label What is counted.
 * @param value The count.
 */
@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(text = value, style = TabotaboTheme.typography.headlineSmall, color = TabotaboTheme.colors.primary)
        Text(text = label, style = TabotaboTheme.typography.caption, color = TabotaboTheme.colors.muted)
    }
}

/**
 * Chart legend entry: a colored dot and its label.
 *
 * @param label Label of the slice.
 * @param color Color of the slice.
 */
@Composable
fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = TabotaboTheme.dimensions.spacing.xs / 2)
    ) {
        Canvas(modifier = Modifier.size(LegendDotSize)) {
            drawCircle(color)
        }
        Text(
            text = label,
            style = TabotaboTheme.typography.caption,
            color = TabotaboTheme.colors.accent,
            modifier = Modifier.padding(start = TabotaboTheme.dimensions.spacing.s)
        )
    }
}

/** Size of the colored dot in a legend entry. */
private val LegendDotSize = 12.dp

/**
 * A single finding with a copy action when it is not empty.
 *
 * @param label Name of the finding.
 * @param value Found value; "None" is shown when empty.
 * @param context Used to copy the value.
 */
@Composable
fun FeatureItem(label: String, value: String, context: Context) {
    Column {
        FindingLabel(label)
        FindingValue(value = value.ifEmpty { stringResource(R.string.none) }, copyable = value.isNotEmpty(), context = context)
    }
}

/**
 * A list of findings, each with a copy action.
 *
 * @param label Name of the findings.
 * @param values Found values; "None" is shown when empty.
 * @param context Used to copy a value.
 */
@Composable
fun FeatureListItems(label: String, values: List<String>, context: Context) {
    Column {
        FindingLabel(label)
        if (values.isEmpty()) {
            FindingValue(value = stringResource(R.string.none), copyable = false, context = context)
        } else {
            values.forEach { value ->
                FindingValue(value = value, copyable = true, context = context)
            }
        }
    }
}

/**
 * Title of a finding.
 *
 * @param label Name of the finding.
 */
@Composable
private fun FindingLabel(label: String) {
    Text(text = label, style = TabotaboTheme.typography.factTitle, color = TabotaboTheme.colors.accent)
}

/**
 * One found value, optionally with a copy action.
 *
 * @param value Value to show.
 * @param copyable Whether to show the copy action.
 * @param context Used to copy the value.
 */
@Composable
private fun FindingValue(value: String, copyable: Boolean, context: Context) {
    val toastText = stringResource(R.string.copied_to_clipboard)

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = value,
            style = TabotaboTheme.typography.body,
            color = TabotaboTheme.colors.muted,
            modifier = Modifier.weight(1f)
        )
        if (copyable) {
            IconAction(
                onClick = { copyToClipboard(context, value, toastText) },
                contentDescription = stringResource(R.string.copy)
            ) {
                ButtonIcon(R.drawable.ic_copy)
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
