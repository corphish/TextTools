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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowSizeClass
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.data.NumberAnalysisResult
import com.corphish.quicktools.ui.common.CustomTopAppBar
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.viewmodels.NumberAnalysisViewModel
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
            QuickToolsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CustomTopAppBar(
                            id = R.string.number_analysis,
                            onNavigationClick = { finish() }
                        )
                    }
                ) { innerPadding ->
                    NumberAnalysisScreen(
                        initialText = initialText,
                        innerPadding = innerPadding,
                        allowApply = allowApply,
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

@Composable
fun NumberAnalysisScreen(
    initialText: String,
    innerPadding: PaddingValues,
    allowApply: Boolean,
    onApply: (String) -> Unit,
    viewModel: NumberAnalysisViewModel = hiltViewModel()
) {
    val inputText by viewModel.inputText.collectAsState()
    val analysisResult by viewModel.analysisResult.collectAsState()
    val selectedBase by viewModel.selectedBase.collectAsState()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isWideScreen = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    LaunchedEffect(Unit) {
        if (initialText.isNotEmpty()) {
            viewModel.setInput(initialText)
        }
    }

    if (isWideScreen) {
        Row(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(0.4f)) {
                InputSection(
                    text = inputText,
                    selectedBase = selectedBase ?: analysisResult?.base ?: 10,
                    supportedBases = analysisResult?.supportedBases ?: emptyList(),
                    onTextChange = { viewModel.setInput(it) },
                    onBaseChange = { viewModel.setBase(it) }
                )
            }

            if (analysisResult == null) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxHeight().weight(0.6f)) {
                    Text(stringResource(R.string.invalid_number))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(0.6f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    analysisContent(analysisResult, allowApply, onApply)
                }
            }
        }
    } else {
        Column(modifier = Modifier.padding(innerPadding)) {
            InputSection(
                text = inputText,
                selectedBase = selectedBase ?: analysisResult?.base ?: 10,
                supportedBases = analysisResult?.supportedBases ?: emptyList(),
                onTextChange = { viewModel.setInput(it) },
                onBaseChange = { viewModel.setBase(it) }
            )

            if (analysisResult == null) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(stringResource(R.string.invalid_number))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    analysisContent(analysisResult, allowApply, onApply)
                }
            }
        }
    }
}

fun LazyListScope.analysisContent(
    result: NumberAnalysisResult?,
    allowApply: Boolean,
    onApply: (String) -> Unit
) {
    result?.let { res ->
        item { SectionHeader(stringResource(R.string.conversions), painterResource(R.drawable.ic_auto_fix_high)) }
        item {
            AnalysisSection {
                res.conversions.toList().forEachIndexed { index, (base, value) ->
                    ResultItem(
                        label = "Base $base",
                        value = value,
                        allowApply = allowApply,
                        onApply = onApply
                    )
                    if (index < res.conversions.size - 1) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.surface,
                            thickness = 2.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }

        if (res.mathTransformations.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.math_transformations), painterResource(R.drawable.ic_numbers)) }
            item {
                AnalysisSection {
                    res.mathTransformations.toList().forEachIndexed { index, (labelRes, value) ->
                        val isError = value.startsWith("@string/")
                        val displayValue = if (isError) {
                            val resId = when (value) {
                                "@string/result_too_large" -> R.string.result_too_large
                                else -> R.string.generic_error
                            }
                            stringResource(resId)
                        } else value
                        ResultItem(
                            label = stringResource(labelRes),
                            value = displayValue,
                            allowApply = allowApply && !isError,
                            allowCopy = !isError,
                            onApply = onApply
                        )
                        if (index < res.mathTransformations.size - 1) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surface,
                                thickness = 2.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        if (res.digitDerivations.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.digit_derivations), painterResource(R.drawable.ic_bar_chart)) }
            item {
                AnalysisSection {
                    res.digitDerivations.toList().forEachIndexed { index, (labelRes, value) ->
                        ResultItem(
                            label = stringResource(labelRes),
                            value = value,
                            allowApply = allowApply,
                            onApply = onApply
                        )
                        if (index < res.digitDerivations.size - 1) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surface,
                                thickness = 2.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        if (res.factors.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.factors), painterResource(R.drawable.ic_auto_awesome)) }
            item {
                AnalysisSection {
                    res.factors.toList().forEachIndexed { index, (labelRes, value) ->
                        ResultItem(
                            label = stringResource(labelRes),
                            value = value,
                            allowApply = allowApply,
                            onApply = onApply
                        )
                        if (index < res.factors.size - 1) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surface,
                                thickness = 2.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        if (res.primeInfo.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.prime_number), painterResource(R.drawable.ic_auto_fix_high)) }
            item {
                AnalysisSection {
                    res.primeInfo.toList().forEachIndexed { index, (labelRes, value) ->
                        val isError = value == "@string/result_too_large"
                        val displayValue = if (value.startsWith("@string/")) {
                            val resId = when (value) {
                                "@string/yes" -> R.string.yes
                                "@string/no" -> R.string.no
                                "@string/result_too_large" -> R.string.result_too_large
                                else -> R.string.generic_error
                            }
                            stringResource(resId)
                        } else value
                        ResultItem(
                            label = stringResource(labelRes),
                            value = displayValue,
                            allowApply = allowApply && !value.startsWith("@string/"),
                            allowCopy = !isError,
                            onApply = onApply
                        )
                        if (index < res.primeInfo.size - 1) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surface,
                                thickness = 2.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        if (res.languageConversions.isNotEmpty()) {
            item { SectionHeader(stringResource(R.string.language_conversion), painterResource(R.drawable.ic_title)) }
            item {
                AnalysisSection {
                    res.languageConversions.toList().forEachIndexed { index, (labelRes, value) ->
                        ResultItem(
                            label = stringResource(labelRes),
                            value = value,
                            allowApply = allowApply,
                            onApply = onApply
                        )
                        if (index < res.languageConversions.size - 1) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.surface,
                                thickness = 2.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InputSection(
    text: String,
    selectedBase: Int,
    supportedBases: List<Int>,
    onTextChange: (String) -> Unit,
    onBaseChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)) {
        Text(
            text = stringResource(R.string.input),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = text,
                        onValueChange = onTextChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.enter_text_here)) },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Box {
                        Surface(
                            onClick = { expanded = true },
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "Base $selectedBase",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            supportedBases.forEach { base ->
                                DropdownMenuItem(
                                    text = { Text("Base $base") },
                                    onClick = {
                                        onBaseChange(base)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalysisSection(
    title: String = "",
    icon: Painter? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Spacer(modifier = Modifier.height(8.dp))
            if (icon != null) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = icon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
            content()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: Painter? = null) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = if (icon != null) 8.dp else 0.dp)
            )
        }
    }
}

@Composable
fun ResultItem(
    label: String,
    value: String,
    allowApply: Boolean,
    allowCopy: Boolean = true,
    onApply: (String) -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
        if (allowCopy) {
            IconButton(onClick = {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(label, value)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
            }, modifier = Modifier.size(32.dp)) {
                Icon(
                    painterResource(R.drawable.ic_copy),
                    contentDescription = stringResource(R.string.copy_to_clipboard),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        if (allowApply) {
            IconButton(onClick = { onApply(value) }, modifier = Modifier.size(32.dp)) {
                Icon(
                    painterResource(R.drawable.ic_done),
                    contentDescription = stringResource(R.string.apply),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
