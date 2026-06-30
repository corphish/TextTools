package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import com.corphish.quicktools.data.TemplateType
import com.corphish.quicktools.data.TextTemplate
import com.corphish.quicktools.ui.common.CustomTopAppBar
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2
import com.corphish.quicktools.viewmodels.TextTemplateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextTemplateActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isProcessText = intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)
        val text = if (isProcessText) {
            intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
        } else ""

        setContent {
            QuickToolsTheme {
                Scaffold(
                    topBar = {
                        CustomTopAppBar(
                            id = R.string.text_template,
                            onNavigationClick = { finish() }
                        )
                    }
                ) { padding ->
                    TextTemplateUI(
                        paddingValues = padding,
                        isSelectionMode = isProcessText,
                        initialText = text,
                        onTemplateSelected = { result ->
                            val nextIntent = Intent(this, TextActionActivity::class.java).apply {
                                putExtra(Intent.EXTRA_PROCESS_TEXT, result)
                                putExtra(
                                    Intent.EXTRA_PROCESS_TEXT_READONLY,
                                    intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
                                )
                                putExtra(
                                    Constants.INTENT_FORCE_COPY,
                                    intent.getBooleanExtra(Constants.INTENT_FORCE_COPY, false)
                                )
                            }
                            startActivity(nextIntent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TextTemplateUI(
    paddingValues: PaddingValues,
    isSelectionMode: Boolean,
    initialText: String,
    onTemplateSelected: (String) -> Unit
) {
    val viewModel: TextTemplateViewModel = hiltViewModel()
    val templates by viewModel.templates.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadTemplates()
        if (isSelectionMode) {
            viewModel.setUserInput(initialText)
        }
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var templateToEdit by remember { mutableStateOf<TextTemplate?>(null) }

    Box(modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()) {
        if (templates.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.no_templates),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!isSelectionMode) {
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.create_template),
                            fontFamily = BrandFontFamily
                        )
                    }
                }
            }
        } else {
            Surface(
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    itemsIndexed(templates) { index, template ->
                        TemplateItem(
                            template = template,
                            isSelectionMode = isSelectionMode,
                            onClick = {
                                if (isSelectionMode) {
                                    onTemplateSelected(viewModel.applyTemplate(template))
                                } else {
                                    templateToEdit = template
                                    showAddDialog = true
                                }
                            },
                            onDelete = {
                                viewModel.deleteTemplate(template.id)
                            }
                        )
                        if (index < templates.size - 1) {
                            HorizontalDivider(
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.background
                            )
                        }
                    }
                }
            }
        }

        if (!isSelectionMode) {
            FloatingActionButton(
                onClick = {
                    templateToEdit = null
                    showAddDialog = true
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(painterResource(R.drawable.ic_edit_note), contentDescription = "Add")
            }
        }
    }

    if (showAddDialog) {
        TemplateDialog(
            template = templateToEdit,
            onDismiss = {
                showAddDialog = false
                templateToEdit = null
            },
            onSave = { name, template, type ->
                if (templateToEdit == null) {
                    viewModel.addTemplate(name, template, type)
                } else {
                    viewModel.updateTemplate(templateToEdit!!.id, name, template, type)
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TemplateItem(
    template: TextTemplate,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable { onClick() },
        headlineContent = {
            Text(
                text = template.name,
                style = TypographyV2.labelMedium,
                fontFamily = BrandFontFamily,
                fontWeight = FontWeight.Bold
            )
        },
        supportingContent = {
            Text(
                text = template.template,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        },
        trailingContent = {
            if (!isSelectionMode) {
                IconButton(onClick = onDelete) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}

@Composable
fun TemplateDialog(
    template: TextTemplate?,
    onDismiss: () -> Unit,
    onSave: (String, String, TemplateType) -> Unit
) {
    var name by remember { mutableStateOf(template?.name ?: "") }
    var value by remember { mutableStateOf(template?.template ?: "") }
    var type by remember { mutableStateOf(template?.type ?: TemplateType.PLAIN_TEXT) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(if (template == null) R.string.create_template else R.string.edit_template),
                fontFamily = BrandFontFamily
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.template_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text(stringResource(R.string.template_value)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.template_type),
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = BrandFontFamily
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TemplateType.entries.forEach { templateType ->
                        FilterChip(
                            selected = type == templateType,
                            onClick = { type = templateType },
                            label = {
                                Text(
                                    text = when (templateType) {
                                        TemplateType.PLAIN_TEXT -> stringResource(R.string.plain_text)
                                        TemplateType.URL -> stringResource(R.string.url)
                                    }
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, value, type) },
                enabled = name.isNotBlank() && value.isNotBlank()
            ) {
                Text(
                    text = stringResource(R.string.save_template),
                    fontFamily = BrandFontFamily
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(android.R.string.cancel),
                    fontFamily = BrandFontFamily
                )
            }
        }
    )
}
