package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingToolbarColors
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.ui.common.CircularButtonWithText
import com.corphish.quicktools.ui.common.CustomTopAppBar
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.utils.ClipboardHelper
import com.corphish.quicktools.viewmodels.TextReplacementViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindAndReplaceActivity : ComponentActivity() {
    private val viewModel by viewModels<TextReplacementViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                QuickToolsTheme {
                    // A surface container using the 'background' color from the theme
                    Scaffold(
                        topBar = {
                            CustomTopAppBar(
                                id = R.string.title_activity_find_and_replace,
                                onNavigationClick = { finish() })
                        }
                    ) {
                        FindAndReplace(
                            defaultPadding = it,
                            viewModel = viewModel,
                            forceCopy = forceCopy,
                            onComplete = { finalText ->
                                if (forceCopy) {
                                    ClipboardHelper.copyToClipboard(this, finalText)
                                } else {
                                    val resultIntent = Intent()
                                    resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, finalText)
                                    setResult(RESULT_OK, resultIntent)
                                    finish()
                                }
                            }
                        )
                    }
                }
            }
        } else {
            finish()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FindAndReplace(
    defaultPadding: PaddingValues,
    viewModel: TextReplacementViewModel,
    forceCopy: Boolean,
    onComplete: (String) -> Unit,
) {
    val highlightColor = MaterialTheme.colorScheme.secondary
    val highlightColorText = MaterialTheme.colorScheme.onSecondary
    val selectedColor = MaterialTheme.colorScheme.primary
    val selectedColorText = MaterialTheme.colorScheme.onPrimary

    val mainTextState by viewModel.mainText.collectAsState()
    val findText by viewModel.findText.collectAsState()
    val replaceText by viewModel.replaceText.collectAsState()
    val counterIndex by viewModel.counterIndex.collectAsState()
    val counterTotal by viewModel.counterTotal.collectAsState()
    val findRanges by viewModel.findRanges.collectAsState()
    val ignoreCase by viewModel.ignoreCase.collectAsState()
    val undoState by viewModel.undoState.collectAsState()
    val redoState by viewModel.redoState.collectAsState()

    val visualTransformation = remember(findRanges, counterIndex) {
        HighlightVisualTransformation(
            findRanges,
            counterIndex,
            selectedColor,
            selectedColorText,
            highlightColor,
            highlightColorText
        )
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (
            mainTextField,
            findTextField,
            replaceTextField,
            findButtons,
            replaceButtons,
            floatingToolbar,
        ) = createRefs()

        Row(
            modifier = Modifier.constrainAs(floatingToolbar) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 16.dp)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalFloatingToolbar(
                expanded = true,
                //colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
            ) {
                // Not the best approach I would say, to show different buttons
                FilledIconButton(
                    onClick = {
                        viewModel.setIgnoreCase(!ignoreCase)
                    },
                    colors = if (ignoreCase) IconButtonDefaults.outlinedIconButtonColors() else IconButtonDefaults.filledIconButtonColors()
                ) {
                    Icon(Icons.Filled.Title, contentDescription = null)
                }
                // Action buttons in the bottom row
                // Undo
                IconButton(
                    onClick = { viewModel.undo() },
                    enabled = undoState,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_undo),
                        contentDescription = null
                    )
                }

                // Redo
                IconButton(
                    onClick = { viewModel.redo() },
                    enabled = redoState,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_redo),
                        contentDescription = null
                    )
                }

                // Reset
                IconButton(
                    onClick = { viewModel.reset() },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_reset),
                        contentDescription = null
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Save or copy
            FloatingActionButton(
                onClick = { onComplete(mainTextState) },
            ) {
                Icon(
                    painter = painterResource(id = if (forceCopy) R.drawable.ic_copy else R.drawable.ic_save),
                    contentDescription = null
                )
            }
        }

        SplitButtonLayout(
            // Replace current
            leadingButton = {
                SplitButtonDefaults.LeadingButton(
                    onClick = { viewModel.replaceFirst() },
                    enabled = replaceText.isNotEmpty() && counterTotal > 0,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_done),
                        contentDescription = null,
                    )
                }
            },

            // Replace all
            trailingButton = {
                SplitButtonDefaults.TrailingButton(
                    onClick = { viewModel.replaceAll() },
                    enabled = replaceText.isNotEmpty() && counterTotal > 0,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_done_all),
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.constrainAs(replaceButtons) {
                start.linkTo(replaceTextField.end, margin = 8.dp)
                top.linkTo(replaceTextField.top)
                bottom.linkTo(replaceTextField.bottom)
                end.linkTo(
                    parent.end, margin = defaultPadding
                        .calculateEndPadding(LayoutDirection.Ltr)
                        .plus(16.dp)
                )
            }
        )

        SplitButtonLayout(
            leadingButton = {
                SplitButtonDefaults.LeadingButton(
                    onClick = { viewModel.decrementCounter() },
                    enabled = findText.isNotEmpty(),
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_previous),
                        contentDescription = null
                    )
                }
            },
            trailingButton = {
                SplitButtonDefaults.TrailingButton(
                    onClick = { viewModel.incrementCounter() },
                    enabled = findText.isNotEmpty(),
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_next),
                        contentDescription = null
                    )
                }
            },
            modifier = Modifier.constrainAs(findButtons) {
                start.linkTo(findTextField.end, margin = 8.dp)
                top.linkTo(findTextField.top)
                bottom.linkTo(findTextField.bottom)
                end.linkTo(
                    parent.end, margin = defaultPadding
                        .calculateEndPadding(LayoutDirection.Ltr)
                        .plus(16.dp)
                )
            }
        )

        OutlinedTextField(
            value = replaceText,
            onValueChange = { viewModel.setReplaceText(it) },
            modifier = Modifier
                .constrainAs(replaceTextField) {
                    start.linkTo(
                        parent.start,
                        margin = defaultPadding.calculateStartPadding(LayoutDirection.Ltr)
                            .plus(16.dp)
                    )
                    bottom.linkTo(floatingToolbar.top, margin = 16.dp)
                    end.linkTo(replaceButtons.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                },
            label = {
                Text(
                    text = stringResource(
                        id = R.string.replace
                    )
                )
            })

        OutlinedTextField(
            value = findText,
            onValueChange = {
                viewModel.setFindText(it)
            },
            suffix = {
                if (findText.isNotEmpty()) {
                    Text(text = "(${minOf(counterTotal, counterIndex + 1)}/$counterTotal)")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(findTextField) {
                    start.linkTo(
                        parent.start,
                        margin = defaultPadding
                            .calculateStartPadding(LayoutDirection.Ltr)
                            .plus(16.dp)
                    )
                    bottom.linkTo(replaceTextField.top, margin = 4.dp)
                    end.linkTo(findButtons.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                },
            label = {
                Text(
                    text = stringResource(
                        id = R.string.find
                    )
                )
            })

        OutlinedTextField(
            value = mainTextState,
            onValueChange = { viewModel.updateMainText(it) },
            modifier = Modifier.constrainAs(mainTextField) {
                start.linkTo(
                    parent.start,
                    margin = defaultPadding.calculateStartPadding(LayoutDirection.Ltr).plus(16.dp)
                )
                bottom.linkTo(findTextField.top, margin = 8.dp)
                end.linkTo(
                    parent.end,
                    margin = defaultPadding.calculateEndPadding(LayoutDirection.Ltr).plus(16.dp)
                )
                top.linkTo(parent.top, margin = defaultPadding.calculateTopPadding().plus(16.dp))
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            },
            visualTransformation = visualTransformation
        )
    }
}

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