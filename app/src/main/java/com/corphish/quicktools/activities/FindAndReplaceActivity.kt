package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.corphish.quicktools.R
import com.corphish.quicktools.text.TextReplacementManager
import com.corphish.quicktools.ui.common.CircularButtonWithText
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2

class FindAndReplaceActivity : ComponentActivity() {
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
            setContent {
                QuickToolsTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        FindAndReplace(
                            input = text,
                            textReplacementManager = TextReplacementManager(text),
                            onComplete = {
                                val resultIntent = Intent()
                                resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, it)
                                setResult(RESULT_OK, resultIntent)
                                finish()
                            },
                            onCancel = {
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
    input: String,
    textReplacementManager: TextReplacementManager,
    onComplete: (String) -> Unit,
    onCancel: () -> Unit
) {
    val highlightColor = MaterialTheme.colorScheme.secondary
    val highlightColorText = MaterialTheme.colorScheme.onSecondary
    val selectedColor = MaterialTheme.colorScheme.primary
    val selectedColorText = MaterialTheme.colorScheme.onPrimary

    var mainTextState by remember {
        mutableStateOf(input)
    }

    var findText by remember {
        mutableStateOf("")
    }

    var replaceText by remember {
        mutableStateOf("")
    }

    var counterIndex by remember {
        mutableIntStateOf(0)
    }

    var counterTotal by remember {
        mutableIntStateOf(0)
    }

    var findRanges by remember {
        mutableStateOf(listOf<TextRange>())
    }

    var ignoreCase by remember {
        mutableStateOf(false)
    }

    var undoState by remember {
        mutableStateOf(textReplacementManager.canUndo())
    }

    var redoState by remember {
        mutableStateOf(textReplacementManager.canRedo())
    }

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

    ConstraintLayout {
        val (
            header,
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

        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(header) {
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
                text = stringResource(id = R.string.title_activity_find_and_replace),
                style = TypographyV2.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = BrandFontFamily,
                modifier = Modifier.padding(start = 16.dp),
                maxLines = 1
            )
        }

        // Action buttons in the bottom row
        // Undo
        CircularButtonWithText(
            onClick = {
                mainTextState = textReplacementManager.undo()

                // Invoke find again
                if (findText.isNotEmpty()) {
                    findRanges = findText(mainTextState, findText, ignoreCase)
                    counterIndex = 0
                    counterTotal = findRanges.size
                } else {
                    findRanges = mutableListOf()
                    counterIndex = 0
                    counterTotal = 0
                }

                undoState = textReplacementManager.canUndo()
                redoState = textReplacementManager.canRedo()
            },
            text = stringResource(id = R.string.undo),
            painterResource = painterResource(id = R.drawable.ic_undo),
            enabled = undoState,
            modifier = Modifier
                .constrainAs(undoButton) {
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(redoButton.start, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                }
        )

        // Redo
        CircularButtonWithText(
            onClick = {
                mainTextState = textReplacementManager.redo()

                // Invoke find again
                if (findText.isNotEmpty()) {
                    findRanges = findText(mainTextState, findText, ignoreCase)
                    counterIndex = 0
                    counterTotal = findRanges.size
                } else {
                    findRanges = mutableListOf()
                    counterIndex = 0
                    counterTotal = 0
                }

                undoState = textReplacementManager.canUndo()
                redoState = textReplacementManager.canRedo()
            },
            painterResource = painterResource(id = R.drawable.ic_redo),
            text = stringResource(id = R.string.redo),
            enabled = redoState,
            modifier = Modifier
                .constrainAs(redoButton) {
                    start.linkTo(undoButton.end, margin = 16.dp)
                    end.linkTo(resetButton.start, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                }
        )

        // Reset
        CircularButtonWithText(
            onClick = {
                mainTextState = textReplacementManager.reset()
                undoState = textReplacementManager.canUndo()
                redoState = textReplacementManager.canRedo()
                findText = ""
                replaceText = ""
            },
            text = stringResource(id = R.string.reset),
            painterResource = painterResource(id = R.drawable.ic_reset),
            modifier = Modifier
                .constrainAs(resetButton) {
                    start.linkTo(redoButton.end, margin = 16.dp)
                    end.linkTo(saveButton.start, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
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
                    end.linkTo(parent.end, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                }
        )

        // Replace current
        IconButton(
            onClick = {
                // Replace first
                mainTextState =
                    textReplacementManager.replaceOne(findRanges[counterIndex], replaceText)

                // Invoke find again
                if (findText.isNotEmpty()) {
                    findRanges = findText(mainTextState, findText, ignoreCase)
                    counterIndex = 0
                    counterTotal = findRanges.size
                } else {
                    findRanges = mutableListOf()
                    counterIndex = 0
                    counterTotal = 0
                }

                undoState = textReplacementManager.canUndo()
                redoState = textReplacementManager.canRedo()
            },
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
            onClick = {
                mainTextState = textReplacementManager.replaceAll(findText, replaceText, ignoreCase)

                // Invoke find
                if (findText.isNotEmpty()) {
                    findRanges = findText(mainTextState, findText, ignoreCase)
                    counterIndex = 0
                    counterTotal = findRanges.size
                } else {
                    findRanges = mutableListOf()
                    counterIndex = 0
                    counterTotal = 0
                }

                undoState = textReplacementManager.canUndo()
                redoState = textReplacementManager.canRedo()
            },
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
                    end.linkTo(parent.end, margin = 16.dp)
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
            onClick = {
                counterIndex = (counterIndex - 1) % counterTotal
                if (counterIndex < 0) {
                    counterIndex = counterTotal - 1
                }
            },
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
            onClick = {
                counterIndex = (counterIndex + 1) % counterTotal
            },
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
                    end.linkTo(parent.end, margin = 16.dp)
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
            onValueChange = { replaceText = it },
            modifier = Modifier
                .constrainAs(replaceTextField) {
                    start.linkTo(parent.start, margin = 16.dp)
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
                if (it.isNotEmpty()) {
                    findText = it
                    findRanges = findText(mainTextState, findText, ignoreCase)
                    counterIndex = 0
                    counterTotal = findRanges.size
                } else {
                    findText = ""
                    findRanges = mutableListOf()
                    counterIndex = 0
                    counterTotal = 0
                }
            },
            suffix = {
                if (findText.isNotEmpty()) {
                    Text(text = "(${minOf(counterTotal, counterIndex + 1)}/$counterTotal)")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(findTextField) {
                    start.linkTo(parent.start, margin = 16.dp)
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
                start.linkTo(parent.start, margin = 16.dp)
                end.linkTo(parent.end, margin = 16.dp)
                bottom.linkTo(replaceTextField.top, margin = 4.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Checkbox(checked = ignoreCase, onCheckedChange = {
                ignoreCase = it
                // Invoke find again
                if (findText.isNotEmpty()) {
                    findRanges = findText(mainTextState, findText, ignoreCase)
                    counterIndex = 0
                    counterTotal = findRanges.size
                } else {
                    findRanges = mutableListOf()
                    counterIndex = 0
                    counterTotal = 0
                }
            })
            Text(text = stringResource(id = R.string.ignore_case))
        }

        OutlinedTextField(
            value = mainTextState,
            onValueChange = {
                mainTextState = it
                textReplacementManager.updateText(it)
                undoState = textReplacementManager.canUndo()
                redoState = textReplacementManager.canRedo()
            },
            modifier = Modifier.constrainAs(mainTextField) {
                start.linkTo(parent.start, margin = 16.dp)
                bottom.linkTo(findTextField.top, margin = 8.dp)
                end.linkTo(parent.end, margin = 16.dp)
                top.linkTo(header.bottom, margin = 16.dp)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            },
            visualTransformation = visualTransformation
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FindAndReplacePreview() {
    QuickToolsTheme {
        FindAndReplace("Android", TextReplacementManager(""), {}, {})
    }
}

private fun findText(mainInput: String, findText: String, ignoreCase: Boolean): List<TextRange> {
    val result = mutableListOf<TextRange>()
    var index = -findText.length
    do {
        index = mainInput.indexOf(
            findText,
            startIndex = index + findText.length,
            ignoreCase = ignoreCase
        )
        if (index != -1) {
            result += TextRange(start = index, end = index + findText.length)
        }
    } while (index >= 0)

    return result
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