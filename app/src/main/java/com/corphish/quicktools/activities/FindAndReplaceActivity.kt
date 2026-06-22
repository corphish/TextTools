package com.corphish.quicktools.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.window.core.layout.WindowSizeClass
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.ui.common.CustomTopAppBar
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2
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
                                onNavigationClick = { finish() },
                            )
                        }
                    ) {
                        FindAndReplace(
                            defaultPadding = it,
                            viewModel = viewModel,
                            onComplete = { finalText ->
                                val resultIntent = Intent(this, TextActionActivity::class.java)
                                resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, finalText)
                                resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, readonly)
                                resultIntent.putExtra(Constants.INTENT_FORCE_COPY, forceCopy)
                                startActivity(resultIntent)

                                finish()
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

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FindAndReplace(
    defaultPadding: PaddingValues,
    viewModel: TextReplacementViewModel,
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

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isWideScreen = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(defaultPadding)) {
        if (isWideScreen) {
            Row(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main text area
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.input),
                        style = TypographyV2.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Surface(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 2.dp
                    ) {
                        TextField(
                            value = mainTextState,
                            onValueChange = { viewModel.updateMainText(it) },
                            modifier = Modifier.fillMaxSize().testTag("main_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                            visualTransformation = visualTransformation
                        )
                    }
                }

                // Controls sidebar
                Column(
                    modifier = Modifier.width(320.dp).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Find Section
                    Column {
                        Text(
                            text = stringResource(id = R.string.find),
                            style = TypographyV2.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 2.dp
                            ) {
                                TextField(
                                    value = findText,
                                    onValueChange = { viewModel.setFindText(it) },
                                    modifier = Modifier.fillMaxWidth().testTag("find_input"),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                    ),
                                    suffix = {
                                        if (findText.isNotEmpty()) {
                                            Text(text = "(${minOf(counterTotal, counterIndex + 1)}/$counterTotal)")
                                        }
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            FindButtons(viewModel, findText)
                        }
                    }

                    // Replace Section
                    Column {
                        Text(
                            text = stringResource(id = R.string.replace),
                            style = TypographyV2.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surface,
                                tonalElevation = 2.dp
                            ) {
                                TextField(
                                    value = replaceText,
                                    onValueChange = { viewModel.setReplaceText(it) },
                                    modifier = Modifier.fillMaxWidth().testTag("replace_input"),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        disabledContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            ReplaceButtons(viewModel, replaceText, counterTotal)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Bottom actions
                    ActionButtons(
                        viewModel = viewModel,
                        ignoreCase = ignoreCase,
                        undoState = undoState,
                        redoState = redoState,
                        mainTextState = mainTextState,
                        onComplete = onComplete
                    )
                }
            }
        } else {
            // Standard vertical layout using ConstraintLayout (modified for card style)
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (
                    mainTextField,
                    findTextField,
                    replaceTextField,
                    findButtons,
                    replaceButtons,
                    floatingToolbar,
                    mainLabel,
                    findLabel,
                    replaceLabel
                ) = createRefs()

                Text(
                    text = stringResource(id = R.string.input),
                    style = TypographyV2.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.constrainAs(mainLabel) {
                        top.linkTo(parent.top, margin = 16.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                    }
                )

                Surface(
                    modifier = Modifier.constrainAs(mainTextField) {
                        start.linkTo(parent.start, margin = 16.dp)
                        bottom.linkTo(findLabel.top, margin = 16.dp)
                        end.linkTo(parent.end, margin = 16.dp)
                        top.linkTo(mainLabel.bottom, margin = 8.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }.testTag("main_input"),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    TextField(
                        value = mainTextState,
                        onValueChange = { viewModel.updateMainText(it) },
                        modifier = Modifier.fillMaxSize(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        visualTransformation = visualTransformation
                    )
                }

                Text(
                    text = stringResource(id = R.string.find),
                    style = TypographyV2.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.constrainAs(findLabel) {
                        bottom.linkTo(findTextField.top, margin = 8.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                    }
                )

                Surface(
                    modifier = Modifier.constrainAs(findTextField) {
                        start.linkTo(parent.start, margin = 16.dp)
                        bottom.linkTo(replaceLabel.top, margin = 16.dp)
                        end.linkTo(findButtons.start, margin = 8.dp)
                        width = Dimension.fillToConstraints
                    }.testTag("find_input"),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    TextField(
                        value = findText,
                        onValueChange = { viewModel.setFindText(it) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        suffix = {
                            if (findText.isNotEmpty()) {
                                Text(text = "(${minOf(counterTotal, counterIndex + 1)}/$counterTotal)")
                            }
                        }
                    )
                }

                Box(modifier = Modifier.constrainAs(findButtons) {
                    end.linkTo(parent.end, margin = 16.dp)
                    top.linkTo(findTextField.top)
                    bottom.linkTo(findTextField.bottom)
                }) {
                    FindButtons(viewModel, findText)
                }

                Text(
                    text = stringResource(id = R.string.replace),
                    style = TypographyV2.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.constrainAs(replaceLabel) {
                        bottom.linkTo(replaceTextField.top, margin = 8.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                    }
                )

                Surface(
                    modifier = Modifier.constrainAs(replaceTextField) {
                        start.linkTo(parent.start, margin = 16.dp)
                        bottom.linkTo(floatingToolbar.top, margin = 24.dp)
                        end.linkTo(replaceButtons.start, margin = 8.dp)
                        width = Dimension.fillToConstraints
                    }.testTag("replace_input"),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    TextField(
                        value = replaceText,
                        onValueChange = { viewModel.setReplaceText(it) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        )
                    )
                }

                Box(modifier = Modifier.constrainAs(replaceButtons) {
                    end.linkTo(parent.end, margin = 16.dp)
                    top.linkTo(replaceTextField.top)
                    bottom.linkTo(replaceTextField.bottom)
                }) {
                    ReplaceButtons(viewModel, replaceText, counterTotal)
                }

                Row(
                    modifier = Modifier.constrainAs(floatingToolbar) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom, margin = 16.dp)
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ActionButtons(
                        viewModel = viewModel,
                        ignoreCase = ignoreCase,
                        undoState = undoState,
                        redoState = redoState,
                        mainTextState = mainTextState,
                        onComplete = onComplete
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FindButtons(viewModel: TextReplacementViewModel, findText: String, modifier: Modifier = Modifier) {
    SplitButtonLayout(
        modifier = modifier,
        leadingButton = {
            SplitButtonDefaults.LeadingButton(
                onClick = { viewModel.decrementCounter() },
                enabled = findText.isNotEmpty(),
                modifier = Modifier.testTag("find_prev_button")
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = null
                )
            }
        },
        trailingButton = {
            SplitButtonDefaults.TrailingButton(
                onClick = { viewModel.incrementCounter() },
                enabled = findText.isNotEmpty(),
                modifier = Modifier.testTag("find_next_button")
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ReplaceButtons(viewModel: TextReplacementViewModel, replaceText: String, counterTotal: Int, modifier: Modifier = Modifier) {
    SplitButtonLayout(
        modifier = modifier,
        leadingButton = {
            SplitButtonDefaults.LeadingButton(
                onClick = { viewModel.replaceFirst() },
                enabled = (replaceText.isNotEmpty() && counterTotal > 0),
                modifier = Modifier.testTag("replace_button")
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_done),
                    contentDescription = null,
                )
            }
        },
        trailingButton = {
            SplitButtonDefaults.TrailingButton(
                onClick = { viewModel.replaceAll() },
                enabled = (replaceText.isNotEmpty() && counterTotal > 0),
                modifier = Modifier.testTag("replace_all_button")
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_done_all),
                    contentDescription = null
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActionButtons(
    viewModel: TextReplacementViewModel,
    ignoreCase: Boolean,
    undoState: Boolean,
    redoState: Boolean,
    mainTextState: String,
    onComplete: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.weight(1f))

        HorizontalFloatingToolbar(
            expanded = true,
        ) {
            FilledIconButton(
                onClick = {
                    viewModel.setIgnoreCase(!ignoreCase)
                },
                colors = if (ignoreCase) IconButtonDefaults.outlinedIconButtonColors() else IconButtonDefaults.filledIconButtonColors(),
                modifier = Modifier.testTag("ignore_case_button")
            ) {
                Icon(painterResource(R.drawable.ic_title), contentDescription = null)
            }
            IconButton(
                onClick = { viewModel.undo() },
                enabled = undoState,
                modifier = Modifier.testTag("undo_button")
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_undo),
                    contentDescription = null
                )
            }
            IconButton(
                onClick = { viewModel.redo() },
                enabled = redoState,
                modifier = Modifier.testTag("redo_button")
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_redo),
                    contentDescription = null
                )
            }
            IconButton(
                onClick = { viewModel.reset() },
                modifier = Modifier.testTag("reset_button")
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_reset),
                    contentDescription = null
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        FloatingActionButton(
            onClick = { onComplete(mainTextState) },
            modifier = Modifier.testTag("complete_button")
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.weight(1f))
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