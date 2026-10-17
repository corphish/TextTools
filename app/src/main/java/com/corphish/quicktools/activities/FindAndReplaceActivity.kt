package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.ui.common.AppScreenHeader
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.common.CardTextField
import com.corphish.quicktools.ui.theme.AppTheme
import com.corphish.quicktools.viewmodels.TextReplacementViewModel
import com.corphish.quicktools.viewmodels.ThemeViewModel
import com.tabotabo.ui.button.ActionStack
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.surface.SelectablePill
import com.tabotabo.ui.theme.TabotaboTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindAndReplaceActivity : ComponentActivity() {
    private val viewModel by viewModels<TextReplacementViewModel>()

    // Same instance as the theme's; used to pass this screen's hue to the screens it opens
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
            val forceCopy = intent.getBooleanExtra(Constants.INTENT_FORCE_COPY, false)

            if (readonly) {
                // We are only interested in editable text
                Toast.makeText(this, R.string.editable_error, Toast.LENGTH_LONG).show()
                finish()
            }

            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
            viewModel.initializeWith(text)

            setContent {
                AppTheme {
                    FindAndReplace(
                        viewModel = viewModel,
                        onBack = { finish() },
                        onComplete = { finalText ->
                            val resultIntent = Intent(this, TextActionActivity::class.java)
                            resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, finalText)
                            resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, readonly)
                            resultIntent.putExtra(Constants.INTENT_FORCE_COPY, forceCopy)
                            resultIntent.putExtra(Constants.INTENT_HUE, themeViewModel.hue.value)
                            startActivity(resultIntent)

                            finish()
                        }
                    )
                }
            }
        } else {
            finish()
        }
    }
}

/**
 * Find and replace screen: the text being edited (with matches highlighted) as content; find,
 * replace, editing tools and the Next button as actions.
 *
 * @param viewModel Holds the text, the search state and the edit history.
 * @param onBack Called when the user leaves the screen.
 * @param onComplete Called with the final text to continue to the text actions.
 */
@Composable
fun FindAndReplace(
    viewModel: TextReplacementViewModel,
    onBack: () -> Unit,
    onComplete: (String) -> Unit,
) {
    val colors = TabotaboTheme.colors
    val selectedColor = colors.primary
    val selectedColorText = colors.onPrimary
    val highlightColor = colors.primary.copy(alpha = MATCH_HIGHLIGHT_ALPHA)
    val highlightColorText = colors.accent

    val mainTextState by viewModel.mainText.collectAsState()
    val findText by viewModel.findText.collectAsState()
    val replaceText by viewModel.replaceText.collectAsState()
    val counterIndex by viewModel.counterIndex.collectAsState()
    val counterTotal by viewModel.counterTotal.collectAsState()
    val findRanges by viewModel.findRanges.collectAsState()
    val ignoreCase by viewModel.ignoreCase.collectAsState()
    val undoState by viewModel.undoState.collectAsState()
    val redoState by viewModel.redoState.collectAsState()

    val visualTransformation = remember(findRanges, counterIndex, colors) {
        HighlightVisualTransformation(
            findRanges,
            counterIndex,
            selectedColor,
            selectedColorText,
            highlightColor,
            highlightColorText
        )
    }

    ScreenScaffold(
        header = { AppScreenHeader(id = R.string.title_activity_find_and_replace, onBack = onBack) },
        actions = {
            ActionStack {
                Column(verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CardTextField(
                            value = findText,
                            onValueChange = { viewModel.setFindText(it) },
                            modifier = Modifier.weight(1f),
                            textFieldModifier = Modifier.testTag("find_input"),
                            label = stringResource(id = R.string.find),
                            singleLine = true,
                            highlighted = counterTotal > 0,
                            suffix = {
                                if (findText.isNotEmpty()) {
                                    Text(text = "(${minOf(counterTotal, counterIndex + 1)}/$counterTotal)")
                                }
                            }
                        )
                        FindButtons(viewModel, findText)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CardTextField(
                            value = replaceText,
                            onValueChange = { viewModel.setReplaceText(it) },
                            modifier = Modifier.weight(1f),
                            textFieldModifier = Modifier.testTag("replace_input"),
                            label = stringResource(id = R.string.replace),
                            singleLine = true,
                        )
                        ReplaceButtons(viewModel, replaceText, counterTotal)
                    }

                    ActionButtons(
                        viewModel = viewModel,
                        ignoreCase = ignoreCase,
                        undoState = undoState,
                        redoState = redoState,
                    )
                }

                PrimaryButton(
                    text = stringResource(R.string.next),
                    onClick = { onComplete(mainTextState) },
                    modifier = Modifier.testTag("complete_button"),
                    trailingIcon = { ButtonIcon(R.drawable.ic_arrow_right) },
                )
            }
        }
    ) {
        CardTextField(
            value = mainTextState,
            onValueChange = { viewModel.updateMainText(it) },
            modifier = Modifier.fillMaxSize(),
            textFieldModifier = Modifier
                .fillMaxSize()
                .testTag("main_input"),
            label = stringResource(id = R.string.input),
            visualTransformation = visualTransformation
        )
    }
}

/**
 * Previous / next match buttons.
 *
 * @param viewModel Moves between matches.
 * @param findText Current search text; buttons are disabled while it is empty.
 * @param modifier Modifier for the row.
 */
@Composable
fun FindButtons(viewModel: TextReplacementViewModel, findText: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        IconAction(
            onClick = { viewModel.decrementCounter() },
            contentDescription = stringResource(R.string.previous),
            enabled = findText.isNotEmpty(),
            modifier = Modifier.testTag("find_prev_button")
        ) {
            ButtonIcon(R.drawable.ic_arrow_left)
        }
        IconAction(
            onClick = { viewModel.incrementCounter() },
            contentDescription = stringResource(R.string.next),
            enabled = findText.isNotEmpty(),
            modifier = Modifier.testTag("find_next_button")
        ) {
            ButtonIcon(R.drawable.ic_arrow_right)
        }
    }
}

/**
 * Replace current / replace all buttons.
 *
 * @param viewModel Performs the replacements.
 * @param replaceText Replacement text; buttons are disabled while it is empty.
 * @param counterTotal Number of matches; buttons are disabled when there are none.
 * @param modifier Modifier for the row.
 */
@Composable
fun ReplaceButtons(viewModel: TextReplacementViewModel, replaceText: String, counterTotal: Int, modifier: Modifier = Modifier) {
    val enabled = replaceText.isNotEmpty() && counterTotal > 0

    Row(modifier = modifier) {
        IconAction(
            onClick = { viewModel.replaceFirst() },
            contentDescription = stringResource(R.string.replace),
            enabled = enabled,
            modifier = Modifier.testTag("replace_button")
        ) {
            ButtonIcon(R.drawable.ic_done)
        }
        IconAction(
            onClick = { viewModel.replaceAll() },
            contentDescription = stringResource(R.string.replace_all),
            enabled = enabled,
            modifier = Modifier.testTag("replace_all_button")
        ) {
            ButtonIcon(R.drawable.ic_done_all)
        }
    }
}

/**
 * Editing tools: ignore case toggle, undo, redo and reset.
 *
 * @param viewModel Applies the actions.
 * @param ignoreCase Whether matching ignores case.
 * @param undoState Whether undo is available.
 * @param redoState Whether redo is available.
 */
@Composable
fun ActionButtons(
    viewModel: TextReplacementViewModel,
    ignoreCase: Boolean,
    undoState: Boolean,
    redoState: Boolean,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        SelectablePill(
            text = stringResource(R.string.ignore_case),
            selected = ignoreCase,
            onClick = { viewModel.setIgnoreCase(!ignoreCase) },
            modifier = Modifier
                .weight(1f)
                .testTag("ignore_case_button")
        )
        IconAction(
            onClick = { viewModel.undo() },
            contentDescription = stringResource(R.string.undo),
            enabled = undoState,
            modifier = Modifier.testTag("undo_button")
        ) {
            ButtonIcon(R.drawable.ic_undo)
        }
        IconAction(
            onClick = { viewModel.redo() },
            contentDescription = stringResource(R.string.redo),
            enabled = redoState,
            modifier = Modifier.testTag("redo_button")
        ) {
            ButtonIcon(R.drawable.ic_redo)
        }
        IconAction(
            onClick = { viewModel.reset() },
            contentDescription = stringResource(R.string.reset),
            modifier = Modifier.testTag("reset_button")
        ) {
            ButtonIcon(R.drawable.ic_reset)
        }
    }
}

/** Background alpha of matches other than the selected one. */
private const val MATCH_HIGHLIGHT_ALPHA = 0.25f

class HighlightVisualTransformation(
    private val findRanges: List<TextRange>,
    private val selectedIndex: Int,
    private val selectedColor: Color,
    private val selectedColorText: Color,
    private val highlightColor: Color,
    private val highlightColorText: Color,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (text.spanStyles.isNotEmpty()) return TransformedText(text, OffsetMapping.Identity)
        val builder = buildAnnotatedString {
            append(text)
            findRanges.forEachIndexed { index, range ->
                if (index == selectedIndex) {
                    addStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            background = selectedColor,
                            color = selectedColorText
                        ),
                        range.min,
                        range.max
                    )
                } else {
                    addStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            background = highlightColor,
                            color = highlightColorText
                        ), range.min, range.max
                    )
                }
            }
        }
        return TransformedText(builder, OffsetMapping.Identity)
    }
}