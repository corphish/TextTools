package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.data.NumberAnalysisResult
import com.corphish.quicktools.data.WalkthroughTargets
import com.corphish.quicktools.data.Walkthroughs
import com.corphish.quicktools.ui.common.AppScreenHeader
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.common.CardTextField
import com.corphish.quicktools.ui.common.SectionLabel
import com.corphish.quicktools.ui.common.walkthrough.WalkthroughHost
import com.corphish.quicktools.ui.common.walkthrough.walkthroughTarget
import com.corphish.quicktools.ui.theme.AppTheme
import com.corphish.quicktools.viewmodels.NumberAnalysisViewModel
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.surface.SelectablePill
import com.tabotabo.ui.text.Fact
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NumberAnalysisActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialText = if (savedInstanceState == null) {
            intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""
        } else ""

        val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
        val forceCopy = intent.getBooleanExtra(Constants.INTENT_FORCE_COPY, false)
        val allowApply = if (forceCopy) false else !readonly

        setContent {
            AppTheme {
                WalkthroughHost(walkthroughs = listOf(Walkthroughs.NUMBER_ANALYSIS)) {
                    NumberAnalysisScreen(
                        initialText = initialText,
                        allowApply = allowApply,
                        onBack = { finish() },
                        onApply = { text ->
                            val resultIntent = Intent()
                            resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, text)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

/**
 * Number analysis screen: the number and its base, followed by conversions and other derived
 * values, each of which can be copied or applied. The input, the base pills and the
 * conversions are targets of [Walkthroughs.NUMBER_ANALYSIS].
 *
 * @param initialText Text to analyse initially.
 * @param allowApply Whether values may replace the selected text.
 * @param onBack Called when the user leaves the screen.
 * @param onApply Called with a value to replace the selected text with.
 * @param viewModel Holds the input and its analysis.
 */
@Composable
fun NumberAnalysisScreen(
    initialText: String,
    allowApply: Boolean,
    onBack: () -> Unit,
    onApply: (String) -> Unit,
    viewModel: NumberAnalysisViewModel = hiltViewModel()
) {
    val inputText by viewModel.inputText.collectAsState()
    val analysisResult by viewModel.analysisResult.collectAsState()
    val selectedBase by viewModel.selectedBase.collectAsState()
    val spacing = TabotaboTheme.dimensions.spacing
    val context = LocalContext.current
    val copiedText = stringResource(R.string.copied_to_clipboard)
    // Built once here and passed to the result rows inside the lazy list
    val onCopy: (String) -> Unit = remember(viewModel, context, copiedText) {
        { value ->
            viewModel.copyToClipboard(value)
            Toast.makeText(context, copiedText, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (initialText.isNotEmpty()) {
            viewModel.setInput(initialText)
        }
    }

    ScreenScaffold(
        header = { AppScreenHeader(id = R.string.title_activity_number_analysis, onBack = onBack) }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(spacing.m)
        ) {
            item(contentType = "input") {
                NumberInputSection(
                    text = inputText,
                    selectedBase = selectedBase ?: analysisResult?.base ?: 10,
                    supportedBases = analysisResult?.supportedBases ?: emptyList(),
                    onTextChange = { viewModel.setInput(it) },
                    onBaseChange = { viewModel.setBase(it) }
                )
            }

            if (analysisResult == null) {
                item(contentType = "invalid") { Subtitle(text = stringResource(R.string.invalid_number), maxLines = Int.MAX_VALUE) }
            } else {
                analysisContent(analysisResult, allowApply, onCopy, onApply)
            }
        }
    }
}

/**
 * Adds the analysis sections for [result] to a lazy list.
 *
 * @param result Analysis to show; nothing is added when null.
 * @param allowApply Whether values may replace the selected text.
 * @param onCopy Copies a value to the clipboard.
 * @param onApply Called with a value to apply.
 */
fun LazyListScope.analysisContent(
    result: NumberAnalysisResult?,
    allowApply: Boolean,
    onCopy: (String) -> Unit,
    onApply: (String) -> Unit
) {
    result?.let { res ->
        resultSection(
            title = R.string.conversions,
            entries = res.conversions.toList(),
            modifier = Modifier.walkthroughTarget(WalkthroughTargets.NUMBER_ANALYSIS_RESULTS),
        ) { (base, value) ->
            ResultItem(label = "Base $base", value = value, allowApply = allowApply, onCopy = onCopy, onApply = onApply)
        }

        resultSection(R.string.math_transformations, res.mathTransformations.toList()) { (labelRes, value) ->
            val isError = value.startsWith(STRING_REFERENCE_PREFIX)
            ResultItem(
                label = stringResource(labelRes),
                value = resolveValue(value),
                allowApply = allowApply && !isError,
                allowCopy = !isError,
                onCopy = onCopy,
                onApply = onApply
            )
        }

        resultSection(R.string.digit_derivations, res.digitDerivations.toList()) { (labelRes, value) ->
            ResultItem(label = stringResource(labelRes), value = value, allowApply = allowApply, onCopy = onCopy, onApply = onApply)
        }

        resultSection(R.string.factors, res.factors.toList()) { (labelRes, value) ->
            ResultItem(label = stringResource(labelRes), value = value, allowApply = allowApply, onCopy = onCopy, onApply = onApply)
        }

        resultSection(R.string.prime_number, res.primeInfo.toList()) { (labelRes, value) ->
            ResultItem(
                label = stringResource(labelRes),
                value = resolveValue(value),
                allowApply = allowApply && !value.startsWith(STRING_REFERENCE_PREFIX),
                allowCopy = value != RESULT_TOO_LARGE,
                onCopy = onCopy,
                onApply = onApply
            )
        }

        resultSection(R.string.language_conversion, res.languageConversions.toList()) { (labelRes, value) ->
            ResultItem(label = stringResource(labelRes), value = value, allowApply = allowApply, onCopy = onCopy, onApply = onApply)
        }
    }
}

/** Prefix of values that refer to a string resource instead of holding a result. */
private const val STRING_REFERENCE_PREFIX = "@string/"

/** Value reporting that a result was too large to compute. */
private const val RESULT_TOO_LARGE = "@string/result_too_large"

/**
 * Turns a string resource reference produced by the analysis into its text; other values are
 * returned unchanged.
 *
 * @param value Analysis value.
 */
@Composable
private fun resolveValue(value: String): String {
    if (!value.startsWith(STRING_REFERENCE_PREFIX)) return value
    return stringResource(
        when (value) {
            "@string/yes" -> R.string.yes
            "@string/no" -> R.string.no
            RESULT_TOO_LARGE -> R.string.result_too_large
            else -> R.string.generic_error
        }
    )
}

/**
 * Adds a titled section of [entries] to a lazy list; nothing is added when [entries] is empty.
 *
 * @param title String resource of the section title.
 * @param entries Entries to show.
 * @param modifier Modifier for the entries, e.g. to make them a walkthrough target.
 * @param itemContent Renders one entry.
 */
private fun <T> LazyListScope.resultSection(
    @StringRes title: Int,
    entries: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit,
) {
    if (entries.isEmpty()) return

    item(contentType = CONTENT_TYPE_SECTION_LABEL) { SectionLabel(stringResource(title), modifier = Modifier.padding(top = TabotaboTheme.dimensions.spacing.l)) }
    item(contentType = "result_section") {
        Column(modifier = modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s)) {
                entries.forEach { itemContent(it) }
            }
        }
    }
}

/**
 * The number to analyse and the base to read it in.
 *
 * @param text Current input.
 * @param selectedBase Base the input is read in.
 * @param supportedBases Bases the input is valid in.
 * @param onTextChange Called when the user edits the input.
 * @param onBaseChange Called when the user picks a base.
 * @param modifier Modifier for the section.
 */
@Composable
fun NumberInputSection(
    text: String,
    selectedBase: Int,
    supportedBases: List<Int>,
    onTextChange: (String) -> Unit,
    onBaseChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = TabotaboTheme.dimensions.spacing

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing.m)) {
        CardTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .walkthroughTarget(WalkthroughTargets.NUMBER_ANALYSIS_INPUT),
            label = stringResource(R.string.input),
            placeholder = stringResource(R.string.enter_text_here),
            singleLine = true,
            highlighted = supportedBases.isNotEmpty(),
        )

        if (supportedBases.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .selectableGroup()
                    .walkthroughTarget(WalkthroughTargets.NUMBER_ANALYSIS_BASES),
                horizontalArrangement = Arrangement.spacedBy(spacing.s),
                verticalArrangement = Arrangement.spacedBy(spacing.s),
            ) {
                supportedBases.forEach { base ->
                    SelectablePill(
                        text = "Base $base",
                        selected = base == selectedBase,
                        onClick = { onBaseChange(base) },
                    )
                }
            }
        }
    }
}

/**
 * One analysis value with copy and apply actions.
 *
 * @param label Name of the value.
 * @param value The value.
 * @param allowApply Whether to show the apply action.
 * @param allowCopy Whether to show the copy action.
 * @param onCopy Called with [value] when copied.
 * @param onApply Called with [value] when applied.
 */
@Composable
fun ResultItem(
    label: String,
    value: String,
    allowApply: Boolean,
    allowCopy: Boolean = true,
    onCopy: (String) -> Unit,
    onApply: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Fact(title = label, value = value, modifier = Modifier.weight(1f))
        if (allowCopy) {
            IconAction(
                onClick = { onCopy(value) },
                contentDescription = stringResource(R.string.copy_to_clipboard)
            ) {
                ButtonIcon(R.drawable.ic_copy)
            }
        }
        if (allowApply) {
            IconAction(onClick = { onApply(value) }, contentDescription = stringResource(R.string.apply)) {
                ButtonIcon(R.drawable.ic_done)
            }
        }
    }
}
