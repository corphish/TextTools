package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.corphish.quicktools.R
import com.corphish.quicktools.text.TextTransformer
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2

// Index mapping
const val INDEX_NONE = 0
const val INDEX_WRAP_TEXT = 1
const val INDEX_CHANGE_CASE = 2
const val INDEX_SORT_LINES = 3
const val INDEX_REPEAT_TEXT = 4
const val INDEX_REMOVE_TEXT = 5
const val INDEX_ADD_PREFIX_SUFFIX = 6
const val INDEX_NUMBER_LINES = 7
const val INDEX_REVERSE_TEXT = 8
const val INDEX_REVERSE_WORDS = 9
const val INDEX_REVERSE_LINES = 10
const val INDEX_DECORATE_TEXT = 11

private val transformOptions = listOf(
    R.string.none,
    R.string.wrap_text,
    R.string.change_case,
    R.string.sort_lines,
    R.string.repeat_text,
    R.string.remove_text,
    R.string.add_prefix_suffix,
    R.string.number_lines,
    R.string.reverse_text,
    R.string.reverse_words,
    R.string.reverse_lines,
    R.string.text_decorate
)

private val wrapOptions = listOf(
    R.string.wrap_single_inverted,
    R.string.wrap_double_inverted,
    R.string.wrap_first_bracket,
    R.string.wrap_curly_braces,
    R.string.wrap_square_bracket,
    R.string.custom_wrapper_text
)

private val caseOptions = listOf(
    R.string.upper_case,
    R.string.lower_case,
    R.string.title_case_first_word_only,
    R.string.title_case_all_words,
    R.string.random_case
)

private val removeOptions = listOf(
    R.string.remove_first,
    R.string.remove_last,
    R.string.remove_all,
    R.string.remove_whitespaces,
    R.string.remove_line_breaks,
    R.string.remove_empty_lines,
    R.string.remove_duplicate_words,
    R.string.remove_duplicate_words_case_sensitive,
    R.string.remove_duplicate_lines,
    R.string.remove_duplicate_lines_case_sensitive
)

private val prefixSuffixOptions = listOf(
    R.string.prefix,
    R.string.suffix
)

private val decorateOptions = listOf(
    R.string.bold_serif,
    R.string.italic_serif,
    R.string.bold_italic_serif,
    R.string.bold_sans,
    R.string.italic_sans,
    R.string.bold_italic_sans,
    R.string.strikethrough_short,
    R.string.strikethrough_long,
    R.string.cursive,
)

private val optionsWithSecondaryDropDown = listOf(
    INDEX_WRAP_TEXT,
    INDEX_CHANGE_CASE,
    INDEX_REMOVE_TEXT,
    INDEX_ADD_PREFIX_SUFFIX,
    INDEX_DECORATE_TEXT,
)

class TransformActivity : ComponentActivity() {

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
            val resultIntent = Intent()

            setContent {
                QuickToolsTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        TextTransformUI(
                            textToTransform = text,
                            textTransformer = TextTransformer(),
                            onCancel = { finish() },
                            onApply = {
                                resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, it)
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

        // Do not finish or else the dialog will go away
        // return false
    }
}

/**
 * Dialog that lets the user select the custom wrapper text.
 */
@Composable
fun CustomWrapperTextDialog(
    onTextConfirmed: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var text by remember { mutableStateOf("") }
    var isError by remember {
        mutableStateOf(false)
    }

    Dialog(
        onDismissRequest = { onDismissRequest() },
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.wrap_text),
                    style = TypographyV2.headlineMedium,
                    fontFamily = BrandFontFamily,
                    fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = stringResource(id = R.string.custom_wrapper_desc),
                    style = TypographyV2.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp, top = 4.dp)
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    value = text,
                    isError = isError,
                    onValueChange = {
                        text = it
                        isError = text.length % 2 == 1
                    },
                    supportingText = {
                        if (isError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.custom_wrapper_error),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    singleLine = true,
                    label = { Text("Enter text here") },
                )

                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { if (!isError) onTextConfirmed(text) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isError
                    ) {
                        Text(
                            text = stringResource(id = android.R.string.ok),
                            fontFamily = BrandFontFamily,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextTransformUI(
    textToTransform: String,
    textTransformer: TextTransformer,
    onCancel: () -> Unit = {},
    onApply: (String) -> Unit = {}
) {
    var inputText by remember { mutableStateOf(textToTransform) }
    var previewText by remember { mutableStateOf(textToTransform) }

    var primaryFunctionExpanded by remember { mutableStateOf(false) }
    var selectedPrimaryIndex by remember { mutableIntStateOf(0) }

    var secondaryFunctionExpanded by remember { mutableStateOf(false) }
    var selectedSecondaryIndex by remember { mutableIntStateOf(0) }

    var customWrapDialog by remember { mutableStateOf(false) }

    var secondaryFunctionText by remember { mutableStateOf("") }

    if (customWrapDialog) {
        CustomWrapperTextDialog(
            onTextConfirmed = {
                previewText = textTransformer.customWrap(textToTransform, it)
                customWrapDialog = false
            }
        ) {
            customWrapDialog = false
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxHeight()
            .then(
                Modifier.blur(if (customWrapDialog) 2.dp else 0.dp)
            )
    ) {
        val (
            header,
            inputTextField,
            previewTextField,
            functionSheet
        ) = createRefs()

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
                text = stringResource(id = R.string.transform_long),
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
                previewText = processTextOperation(
                    textTransformer,
                    inputText,
                    selectedPrimaryIndex,
                    selectedSecondaryIndex,
                    secondaryFunctionText
                )
            },
            modifier = Modifier.constrainAs(inputTextField) {
                top.linkTo(header.bottom, margin = 16.dp)
                bottom.linkTo(previewTextField.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            },
            label = { Text(text = stringResource(id = R.string.input)) },
        )

        OutlinedTextField(
            value = previewText,
            onValueChange = { },
            modifier = Modifier.constrainAs(previewTextField) {
                top.linkTo(inputTextField.bottom, margin = 8.dp)
                bottom.linkTo(functionSheet.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            },
            label = { Text(text = stringResource(id = R.string.preview)) },
            readOnly = true
        )

        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier.constrainAs(functionSheet) {
                top.linkTo(previewTextField.bottom, margin = 8.dp)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.matchParent
            }
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_text_transform),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = stringResource(id = R.string.transform),
                    style = TypographyV2.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = BrandFontFamily
                )
            }

            // Function 1
            ExposedDropdownMenuBox(
                expanded = primaryFunctionExpanded,
                onExpandedChange = {
                    primaryFunctionExpanded = !primaryFunctionExpanded
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = stringResource(id = transformOptions[selectedPrimaryIndex]),
                    onValueChange = { },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = primaryFunctionExpanded
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth()
                )

                DropdownMenu(
                    expanded = primaryFunctionExpanded,
                    onDismissRequest = {
                        primaryFunctionExpanded = false
                    },
                    modifier = Modifier.exposedDropdownSize()
                ) {
                    transformOptions.forEachIndexed { index, selectionOption ->
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = selectionOption))
                            },
                            onClick = {
                                primaryFunctionExpanded = false
                                selectedPrimaryIndex = index

                                // Select the appropriate choice for those supporting secondary functions
                                when (selectedPrimaryIndex) {
                                    INDEX_WRAP_TEXT, INDEX_CHANGE_CASE, INDEX_REMOVE_TEXT, INDEX_ADD_PREFIX_SUFFIX, INDEX_DECORATE_TEXT -> {
                                        selectedSecondaryIndex = 0
                                        secondaryFunctionText = ""
                                    }

                                    INDEX_REPEAT_TEXT -> {
                                        // Default for repeat
                                        secondaryFunctionText = "1"
                                    }
                                }

                                previewText = processTextOperation(
                                    textTransformer = textTransformer,
                                    inputText = inputText,
                                    selectedPrimaryIndex = selectedPrimaryIndex,
                                    selectedSecondaryIndex = selectedSecondaryIndex,
                                    secondaryFunctionText = secondaryFunctionText
                                )
                            }
                        )
                    }
                }
            }

            // Text input for repeat/remove/add prefix or suffix
            if (selectedPrimaryIndex == INDEX_REPEAT_TEXT || selectedPrimaryIndex == INDEX_REMOVE_TEXT || selectedPrimaryIndex == INDEX_ADD_PREFIX_SUFFIX) {
                OutlinedTextField(
                    value = secondaryFunctionText,
                    // Disable when remove option is selected for preset characters
                    enabled = !(selectedPrimaryIndex == INDEX_REMOVE_TEXT && selectedSecondaryIndex > 2),
                    onValueChange = {
                        secondaryFunctionText = it
                        previewText = processTextOperation(
                            textTransformer = textTransformer,
                            inputText = inputText,
                            selectedPrimaryIndex = selectedPrimaryIndex,
                            selectedSecondaryIndex = selectedSecondaryIndex,
                            secondaryFunctionText = secondaryFunctionText
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = if (selectedPrimaryIndex == INDEX_REPEAT_TEXT) KeyboardType.Number else KeyboardType.Text),
                    label = {
                        Text(
                            stringResource(
                                id = when (selectedPrimaryIndex) {
                                    INDEX_REPEAT_TEXT -> R.string.repeat_text
                                    INDEX_REMOVE_TEXT -> R.string.remove_text
                                    INDEX_ADD_PREFIX_SUFFIX -> R.string.add_prefix_suffix
                                    else -> R.string.transform
                                }
                            )
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }

            if (optionsWithSecondaryDropDown.contains(selectedPrimaryIndex)) {
                val secondaryList = when (selectedPrimaryIndex) {
                    INDEX_WRAP_TEXT -> wrapOptions
                    INDEX_CHANGE_CASE -> caseOptions
                    INDEX_REMOVE_TEXT -> removeOptions
                    INDEX_ADD_PREFIX_SUFFIX -> prefixSuffixOptions
                    INDEX_DECORATE_TEXT -> decorateOptions
                    else -> listOf()
                }

                // Function 2
                ExposedDropdownMenuBox(
                    expanded = secondaryFunctionExpanded,
                    onExpandedChange = {
                        secondaryFunctionExpanded = !secondaryFunctionExpanded
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = stringResource(id = secondaryList[selectedSecondaryIndex]),
                        onValueChange = { },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = secondaryFunctionExpanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = secondaryFunctionExpanded,
                        onDismissRequest = {
                            secondaryFunctionExpanded = false
                        },
                        modifier = Modifier.exposedDropdownSize()
                    ) {
                        secondaryList.forEachIndexed { index, selectionOption ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(id = selectionOption))
                                },
                                onClick = {
                                    secondaryFunctionExpanded = false
                                    selectedSecondaryIndex = index

                                    if (selectedPrimaryIndex == INDEX_WRAP_TEXT && selectedSecondaryIndex == 5) {
                                        customWrapDialog = true
                                    } else {
                                        previewText = processTextOperation(
                                            textTransformer = textTransformer,
                                            inputText = inputText,
                                            selectedPrimaryIndex = selectedPrimaryIndex,
                                            selectedSecondaryIndex = selectedSecondaryIndex,
                                            secondaryFunctionText = secondaryFunctionText
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { onApply(previewText) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.apply), fontFamily = BrandFontFamily)
            }
        }
    }
}

fun processTextOperation(
    textTransformer: TextTransformer,
    inputText: String,
    selectedPrimaryIndex: Int,
    selectedSecondaryIndex: Int,
    secondaryFunctionText: String
) = when (selectedPrimaryIndex) {
    INDEX_NONE -> {
        // None
        inputText
    }

    INDEX_WRAP_TEXT -> {
        // Wrap
        textTransformer.presetWrap(inputText, selectedSecondaryIndex)
    }

    INDEX_CHANGE_CASE -> {
        // Change case
        textTransformer.changeCase(inputText, selectedSecondaryIndex)
    }

    INDEX_SORT_LINES -> {
        // Sort lines
        textTransformer.sortLines(inputText)
    }

    INDEX_REPEAT_TEXT -> {
        // Repeat text
        textTransformer.repeatText(inputText, secondaryFunctionText.toIntOrNull() ?: 1)
    }

    INDEX_REMOVE_TEXT -> {
        // Remove text
        when (selectedSecondaryIndex) {
            0, 1, 2 -> textTransformer.removeText(inputText, secondaryFunctionText, selectedSecondaryIndex)
            3 -> textTransformer.removeWhiteSpaces(inputText)
            4 -> textTransformer.removeLineBreaks(inputText)
            5 -> textTransformer.removeEmptyLines(inputText)
            6 -> textTransformer.removeDuplicateWords(inputText, ignoreCase = true)
            7 -> textTransformer.removeDuplicateWords(inputText, ignoreCase = false)
            8 -> textTransformer.removeDuplicateLines(inputText, ignoreCase = true)
            9 -> textTransformer.removeDuplicateLines(inputText, ignoreCase = false)
            else -> inputText
        }

    }

    INDEX_ADD_PREFIX_SUFFIX -> {
        // Add prefix or suffix
        when (selectedSecondaryIndex) {
            0 -> {
                textTransformer.addPrefix(inputText, secondaryFunctionText)
            }

            1 -> {
                textTransformer.addSuffix(inputText, secondaryFunctionText)
            }

            else -> {
                inputText
            }
        }
    }

    INDEX_NUMBER_LINES -> {
        // Number lines
        textTransformer.numberLines(inputText)
    }

    INDEX_REVERSE_TEXT -> {
        // Reverse text
        textTransformer.reverseText(inputText)
    }

    INDEX_REVERSE_WORDS -> {
        // Reverse words
        textTransformer.reverseWords(inputText)
    }

    INDEX_REVERSE_LINES -> {
        // Reverse lines
        textTransformer.reverseLines(inputText)
    }

    INDEX_DECORATE_TEXT -> {
        when (selectedSecondaryIndex) {
            0 -> textTransformer.boldSerif(inputText)
            1 -> textTransformer.italicSerif(inputText)
            2 -> textTransformer.boldItalicSerif(inputText)
            3 -> textTransformer.boldSans(inputText)
            4 -> textTransformer.italicSans(inputText)
            5 -> textTransformer.boldItalicSans(inputText)
            6 -> textTransformer.shortStrikethrough(inputText)
            7 -> textTransformer.longStrikethrough(inputText)
            8 -> textTransformer.cursive(inputText)
            else -> inputText
        }
    }

    else -> {
        inputText
    }
}


@Composable
@Preview
fun TextTransformUIPreview() {
    TextTransformUI(textToTransform = "Text to transform", textTransformer = TextTransformer())
}