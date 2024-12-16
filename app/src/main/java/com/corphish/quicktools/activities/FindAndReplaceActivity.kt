package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.corphish.quicktools.ui.common.CircularButtonWithText
import com.corphish.quicktools.ui.common.CustomTopAppBar
import com.corphish.quicktools.ui.theme.QuickToolsTheme
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
                            onComplete = { finalText ->
                                val resultIntent = Intent()
                                resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, finalText)
                                setResult(RESULT_OK, resultIntent)
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

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (
            mainTextField,
            findTextField,
            replaceTextField,
            findNextButton,
            findPreviousButton,
            replaceThisButton,
            replaceAllButton,
            undoButton,
            redoButton,
            resetButton,
            saveButton,
            caseCheckBox
        ) =
            createRefs()

        // Action buttons in the bottom row
        // Undo
        CircularButtonWithText(
            onClick = { viewModel.undo() },
            text = stringResource(id = R.string.undo),
            painterResource = painterResource(id = R.drawable.ic_undo),
            enabled = undoState,
            modifier = Modifier
                .constrainAs(undoButton) {
                    start.linkTo(
                        parent.start,
                        margin = defaultPadding.calculateStartPadding(LayoutDirection.Ltr)
                            .plus(16.dp)
                    )
                    end.linkTo(redoButton.start, margin = 16.dp)
                    bottom.linkTo(
                        parent.bottom,
                        margin = defaultPadding.calculateBottomPadding().plus(16.dp)
                    )
                }
        )

        // Redo
        CircularButtonWithText(
            onClick = { viewModel.redo() },
            painterResource = painterResource(id = R.drawable.ic_redo),
            text = stringResource(id = R.string.redo),
            enabled = redoState,
            modifier = Modifier
                .constrainAs(redoButton) {
                    start.linkTo(undoButton.end, margin = 16.dp)
                    end.linkTo(resetButton.start, margin = 16.dp)
                    bottom.linkTo(
                        parent.bottom,
                        margin = defaultPadding.calculateBottomPadding().plus(16.dp)
                    )
                }
        )

        // Reset
        CircularButtonWithText(
            onClick = { viewModel.reset() },
            text = stringResource(id = R.string.reset),
            painterResource = painterResource(id = R.drawable.ic_reset),
            modifier = Modifier
                .constrainAs(resetButton) {
                    start.linkTo(redoButton.end, margin = 16.dp)
                    end.linkTo(saveButton.start, margin = 16.dp)
                    bottom.linkTo(
                        parent.bottom,
                        margin = defaultPadding.calculateBottomPadding().plus(16.dp)
                    )
                }
        )

        // Save
        CircularButtonWithText(
            onClick = { onComplete(mainTextState) },
            painterResource = painterResource(id = R.drawable.ic_save),
            text = stringResource(id = R.string.apply),
            modifier = Modifier
                .constrainAs(saveButton) {
                    start.linkTo(resetButton.end, margin = 16.dp)
                    end.linkTo(
                        parent.end,
                        margin = defaultPadding.calculateEndPadding(LayoutDirection.Ltr).plus(16.dp)
                    )
                    bottom.linkTo(
                        parent.bottom,
                        margin = defaultPadding.calculateBottomPadding().plus(16.dp)
                    )
                }
        )

        // Replace current
        IconButton(
            onClick = { viewModel.replaceFirst() },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = Color.Gray
            ),
            enabled = replaceText.isNotEmpty() && counterTotal > 0,
            modifier = Modifier
                .constrainAs(replaceThisButton) {
                    start.linkTo(replaceTextField.end, margin = 8.dp)
                    top.linkTo(replaceTextField.top)
                    bottom.linkTo(replaceTextField.bottom)
                    end.linkTo(replaceAllButton.start, margin = 4.dp)
                }
                .clip(CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_done),
                contentDescription = "",
                modifier = Modifier.size(16.dp),
            )
        }

        // Replace all
        IconButton(
            onClick = { viewModel.replaceAll() },
            enabled = replaceText.isNotEmpty() && counterTotal > 0,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = Color.Gray
            ),
            modifier = Modifier
                .constrainAs(replaceAllButton) {
                    start.linkTo(replaceThisButton.end, margin = 4.dp)
                    top.linkTo(replaceTextField.top)
                    bottom.linkTo(replaceTextField.bottom)
                    end.linkTo(
                        parent.end,
                        margin = defaultPadding
                            .calculateEndPadding(LayoutDirection.Ltr)
                            .plus(16.dp)
                    )
                }
                .clip(CircleShape)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_done_all),
                contentDescription = "",
                modifier = Modifier.size(16.dp)
            )
        }

        // Find previous
        IconButton(
            onClick = { viewModel.decrementCounter() },
            enabled = findText.isNotEmpty(),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = Color.Gray
            ),
            modifier = Modifier
                .constrainAs(findPreviousButton) {
                    start.linkTo(findTextField.end, margin = 8.dp)
                    top.linkTo(findTextField.top)
                    bottom.linkTo(findTextField.bottom)
                    end.linkTo(findNextButton.start, margin = 4.dp)
                }
                .clip(CircleShape)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_previous),
                contentDescription = "",
                modifier = Modifier.size(16.dp)
            )
        }

        // Find next
        IconButton(
            onClick = { viewModel.incrementCounter() },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = Color.Gray
            ),
            enabled = findText.isNotEmpty(),
            modifier = Modifier
                .constrainAs(findNextButton) {
                    start.linkTo(findPreviousButton.end, margin = 4.dp)
                    top.linkTo(findTextField.top)
                    bottom.linkTo(findTextField.bottom)
                    end.linkTo(
                        parent.end,
                        margin = defaultPadding
                            .calculateEndPadding(LayoutDirection.Ltr)
                            .plus(16.dp)
                    )
                }
                .clip(CircleShape)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_next),
                contentDescription = "",
                modifier = Modifier.size(16.dp)
            )
        }

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
                    bottom.linkTo(undoButton.top, margin = 16.dp)
                    end.linkTo(replaceThisButton.start, margin = 8.dp)
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
                    bottom.linkTo(caseCheckBox.top, margin = 4.dp)
                    end.linkTo(findPreviousButton.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                },
            label = {
                Text(
                    text = stringResource(
                        id = R.string.find
                    )
                )
            })

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(caseCheckBox) {
                start.linkTo(
                    parent.start,
                    margin = defaultPadding.calculateStartPadding(LayoutDirection.Ltr).plus(16.dp)
                )
                end.linkTo(
                    parent.end,
                    margin = defaultPadding.calculateEndPadding(LayoutDirection.Ltr).plus(16.dp)
                )
                bottom.linkTo(replaceTextField.top, margin = 4.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Checkbox(
                checked = ignoreCase,
                onCheckedChange = { viewModel.setIgnoreCase(it) }
            )
            Text(text = stringResource(id = R.string.ignore_case))
        }

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