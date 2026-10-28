package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.data.TemplateType
import com.corphish.quicktools.data.TextTemplate
import com.corphish.quicktools.ui.common.AppScreenHeader
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.common.FormDialog
import com.corphish.quicktools.ui.theme.AppTheme
import com.corphish.quicktools.viewmodels.TextTemplateViewModel
import com.corphish.quicktools.viewmodels.ThemeViewModel
import com.tabotabo.ui.button.ActionStack
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.layout.CardList
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.settings.SettingsSection
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.surface.SelectablePillRow
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextTemplateActivity : ComponentActivity() {

    // Same instance as the theme's; used to pass this screen's hue to the screens it opens
    private val themeViewModel: ThemeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isProcessText = intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)
        val text = if (isProcessText) {
            intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
        } else ""

        setContent {
            AppTheme {
                TextTemplateUI(
                    isSelectionMode = isProcessText,
                    initialText = text,
                    onBack = { finish() },
                    onTemplateSelected = { result ->
                        startActivity(
                            TextActionActivity.handOffIntent(
                                context = this,
                                text = result,
                                readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true),
                                forceCopy = intent.getBooleanExtra(Constants.INTENT_FORCE_COPY, false),
                                hue = themeViewModel.hue.value,
                            )
                        )
                        finish()
                    }
                )
            }
        }
    }
}

/**
 * Text template screen. In selection mode (opened with text) tapping a template applies it;
 * otherwise templates can be created, edited and deleted.
 *
 * @param isSelectionMode Whether the screen was opened to apply a template to text.
 * @param initialText Text to apply templates to in selection mode.
 * @param onBack Called when the user leaves the screen.
 * @param onTemplateSelected Called with the result of applying a template.
 */
@Composable
fun TextTemplateUI(
    isSelectionMode: Boolean,
    initialText: String,
    onBack: () -> Unit,
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

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    // Keep the id (not the template) so an open edit dialog survives configuration changes
    var templateToEditId by rememberSaveable { mutableStateOf<String?>(null) }
    val templateToEdit = templates.firstOrNull { it.id == templateToEditId }

    ScreenScaffold(
        header = { AppScreenHeader(id = R.string.title_activity_text_template, onBack = onBack) },
        actions = if (isSelectionMode) {
            null
        } else {
            {
                ActionStack {
                    PrimaryButton(
                        text = stringResource(R.string.create_template),
                        onClick = {
                            templateToEditId = null
                            showAddDialog = true
                        },
                        leadingIcon = { ButtonIcon(R.drawable.ic_edit_note) },
                    )
                }
            }
        }
    ) {
        if (templates.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Subtitle(text = stringResource(R.string.no_templates), maxLines = Int.MAX_VALUE)
            }
        } else {
            CardList(
                items = templates,
                key = { it.id },
            ) { template ->
                TemplateItem(
                    template = template,
                    isSelectionMode = isSelectionMode,
                    onClick = {
                        if (isSelectionMode) {
                            onTemplateSelected(viewModel.applyTemplate(template))
                        } else {
                            templateToEditId = template.id
                            showAddDialog = true
                        }
                    },
                    onDelete = {
                        viewModel.deleteTemplate(template.id)
                    }
                )
            }
        }
    }

    if (showAddDialog && (templateToEditId == null || templateToEdit != null)) {
        TemplateDialog(
            template = templateToEdit,
            onDismiss = {
                showAddDialog = false
                templateToEditId = null
            },
            onSave = { name, template, type ->
                if (templateToEdit == null) {
                    viewModel.addTemplate(name, template, type)
                } else {
                    viewModel.updateTemplate(templateToEdit.id, name, template, type)
                }
                showAddDialog = false
            }
        )
    }
}

/**
 * Card of one template: its name and value, with a delete action outside selection mode.
 *
 * @param template Template to show.
 * @param isSelectionMode Whether tapping applies the template (no delete action then).
 * @param onClick Called when the card is tapped.
 * @param onDelete Called when the delete action is tapped.
 */
@Composable
fun TemplateItem(
    template: TextTemplate,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    GlowCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.name,
                    style = TabotaboTheme.typography.titleMedium,
                    color = TabotaboTheme.colors.accent,
                )
                Subtitle(text = template.template)
            }
            if (!isSelectionMode) {
                IconAction(onClick = onDelete, contentDescription = stringResource(R.string.delete)) {
                    ButtonIcon(R.drawable.ic_delete)
                }
            }
        }
    }
}

/**
 * Dialog to create a template or edit [template].
 *
 * @param template Template to edit, or null to create one.
 * @param onDismiss Called when the dialog is cancelled.
 * @param onSave Called with the name, value and type to save.
 */
@Composable
fun TemplateDialog(
    template: TextTemplate?,
    onDismiss: () -> Unit,
    onSave: (String, String, TemplateType) -> Unit
) {
    var name by rememberSaveable { mutableStateOf(template?.name ?: "") }
    var value by rememberSaveable { mutableStateOf(template?.template ?: "") }
    var type by rememberSaveable { mutableStateOf(template?.type ?: TemplateType.PLAIN_TEXT) }
    val spacing = TabotaboTheme.dimensions.spacing
    val typeLabels = mapOf(
        TemplateType.PLAIN_TEXT to stringResource(R.string.plain_text),
        TemplateType.URL to stringResource(R.string.url),
    )

    FormDialog(
        title = stringResource(if (template == null) R.string.create_template else R.string.edit_template),
        confirmText = stringResource(R.string.save_template),
        onConfirm = { onSave(name, value, type) },
        onDismissRequest = onDismiss,
        confirmEnabled = name.isNotBlank() && value.isNotBlank(),
        dismissText = stringResource(android.R.string.cancel),
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(stringResource(R.string.template_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(spacing.s))
        OutlinedTextField(
            value = value,
            onValueChange = { value = it },
            label = { Text(stringResource(R.string.template_value)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(spacing.l))
        SettingsSection(title = stringResource(R.string.template_type)) {
            SelectablePillRow(
                options = TemplateType.entries,
                selected = type,
                onSelect = { type = it },
                label = { typeLabels.getValue(it) },
            )
        }
    }
}
