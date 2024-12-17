package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.corphish.quicktools.R
import com.corphish.quicktools.ui.common.CustomTopAppBar
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2
import com.corphish.quicktools.viewmodels.TextTransformViewModel

class TransformActivity : ComponentActivity() {

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
            val resultIntent = Intent()

            setContent {
                QuickToolsTheme {
                    Scaffold(
                        topBar = { CustomTopAppBar(id = R.string.transform_long, onNavigationClick = { finish() }) }
                    ) {
                        TextTransformUI(
                            textToTransform = text,
                            paddingValues = it,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextTransformUI(
    textToTransform: String,
    paddingValues: PaddingValues,
    onApply: (String) -> Unit = {}
) {
    val viewModel = viewModel { TextTransformViewModel() }
    val inputText by viewModel.mainText.collectAsState()
    val previewText by viewModel.previewText.collectAsState()

    var primaryFunctionExpanded by remember { mutableStateOf(false) }
    val selectedPrimaryIndex by viewModel.selectedPrimaryIndex.collectAsState()

    var secondaryFunctionExpanded by remember { mutableStateOf(false) }
    val selectedSecondaryIndex by viewModel.selectedSecondaryIndex.collectAsState()
    val secondaryList by viewModel.secondaryOptionList.collectAsState()

    val secondaryFunctionText by viewModel.secondaryFunctionText.collectAsState()
    val secondaryFunctionTextLabel by viewModel.secondaryFunctionTextLabel.collectAsState()
    val secondaryFunctionTextInputType by viewModel.secondaryFunctionTextInputType.collectAsState()
    val secondaryFunctionTextEnabled by viewModel.secondaryFunctionTextEnabled.collectAsState()
    val secondaryFunctionTextVisible by viewModel.secondaryFunctionTextVisible.collectAsState()

    LaunchedEffect(true) { viewModel.initializeText(textToTransform) }

    ConstraintLayout(
        modifier = Modifier.fillMaxHeight()
    ) {
        val (
            inputTextField,
            previewTextField,
            functionSheet
        ) = createRefs()

        OutlinedTextField(
            value = inputText,
            onValueChange = {
                viewModel.initializeText(it)
            },
            modifier = Modifier.constrainAs(inputTextField) {
                top.linkTo(parent.top, margin = paddingValues.calculateTopPadding().plus(16.dp))
                bottom.linkTo(previewTextField.top, margin = 8.dp)
                start.linkTo(parent.start, margin = paddingValues.calculateStartPadding(LayoutDirection.Ltr).plus(8.dp))
                end.linkTo(parent.end, margin = paddingValues.calculateEndPadding(LayoutDirection.Ltr).plus(8.dp))
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
                start.linkTo(parent.start, margin = paddingValues.calculateStartPadding(LayoutDirection.Ltr).plus(8.dp))
                end.linkTo(parent.end, margin = paddingValues.calculateEndPadding(LayoutDirection.Ltr).plus(8.dp))
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
                    value = stringResource(id = TextTransformViewModel.transformOptions[selectedPrimaryIndex]),
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
                    TextTransformViewModel.transformOptions.forEachIndexed { index, selectionOption ->
                        DropdownMenuItem(
                            text = {
                                Text(text = stringResource(id = selectionOption))
                            },
                            onClick = {
                                primaryFunctionExpanded = false
                                viewModel.selectPrimaryIndex(index)
                            }
                        )
                    }
                }
            }

            // Text input for repeat/remove/add prefix or suffix
            if (secondaryFunctionTextVisible) {
                OutlinedTextField(
                    value = secondaryFunctionText,
                    enabled = secondaryFunctionTextEnabled,
                    onValueChange = { viewModel.setSecondaryText(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = secondaryFunctionTextInputType),
                    label = { Text(stringResource(id = secondaryFunctionTextLabel)) },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                )
            }

            if (secondaryList.isNotEmpty()) {
                // Function 2
                ExposedDropdownMenuBox(
                    expanded = secondaryFunctionExpanded,
                    onExpandedChange = { secondaryFunctionExpanded = !secondaryFunctionExpanded },
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
                                    viewModel.selectSecondaryIndex(index)
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

@Composable
@Preview
fun TextTransformUIPreview() {
    TextTransformUI(textToTransform = "Text to transform", paddingValues = PaddingValues(all = 0.dp))
}