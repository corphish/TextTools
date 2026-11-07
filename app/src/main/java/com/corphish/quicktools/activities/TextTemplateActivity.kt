package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.data.TemplateType
import com.corphish.quicktools.data.TextTemplate
import com.corphish.quicktools.data.WalkthroughTargets
import com.corphish.quicktools.data.Walkthroughs
import com.corphish.quicktools.ui.common.AppScreenHeader
import com.corphish.quicktools.ui.common.AppScreenScaffold
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.common.FormDialog
import com.corphish.quicktools.ui.common.SectionLabel
import com.corphish.quicktools.ui.common.walkthrough.WalkthroughHost
import com.corphish.quicktools.ui.common.walkthrough.walkthroughTarget
import com.corphish.quicktools.ui.theme.AppTheme
import com.corphish.quicktools.viewmodels.TextTemplateViewModel
import com.corphish.quicktools.viewmodels.ThemeViewModel
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.layout.CardList
import com.tabotabo.ui.layout.LocalScreenLayoutInfo
import com.tabotabo.ui.settings.SettingsSection
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.surface.SelectablePillRow
import com.tabotabo.ui.text.Fact
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.body
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextTemplateActivity : ComponentActivity() {

    // Same instance as the theme's; used to pass this screen's hue to the screens it opens
    private val themeViewModel: ThemeViewModel by viewModels()

    // Same instance as the screen's; decides which walkthroughs to offer while managing
    private val templateViewModel: TextTemplateViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isProcessText = intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)
        val text = if (isProcessText) {
            intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
        } else ""

        setContent {
            AppTheme {
                val manageWalkthroughs by templateViewModel.manageWalkthroughs.collectAsState()
                val walkthroughs = if (isProcessText) listOf(Walkthroughs.TEMPLATE_APPLY) else manageWalkthroughs

                WalkthroughHost(walkthroughs = walkthroughs) {
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
}

/**
 * Text template screen. In selection mode (opened with text) tapping a template applies it, and
 * each card previews its result; otherwise templates can be created, edited and deleted.
 * In landscape the right pane, aligned to the bottom (see [AppScreenScaffold]), also shows the
 * selected text in selection mode, or how templates work with an example otherwise.
 * The Create button and the first template are targets of [Walkthroughs.TEMPLATE_APPLY] in
 * selection mode, and of [TextTemplateViewModel.manageWalkthroughs] otherwise.
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
    val userInput by viewModel.userInput.collectAsState()
    val previews by viewModel.previews.collectAsState()

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

    val exampleInput = stringResource(R.string.template_example_input)
    val exampleResult = remember(exampleInput) {
        viewModel.render(EXAMPLE_TEMPLATE, exampleInput, TemplateType.URL)
    }

    AppScreenScaffold(
        header = { AppScreenHeader(id = R.string.title_activity_text_template, onBack = onBack) },
        actions = {
            // The scaffold reports the orientation to its slots; the extra sections only fit
            // in the landscape right pane
            if (LocalScreenLayoutInfo.current.isLandscape) {
                if (isSelectionMode) {
                    SelectedTextSection(text = userInput)
                } else {
                    TemplateHelpSection(exampleInput = exampleInput, exampleResult = exampleResult)
                }
            }
            if (!isSelectionMode) {
                PrimaryButton(
                    text = stringResource(R.string.create_template),
                    onClick = {
                        templateToEditId = null
                        showAddDialog = true
                    },
                    modifier = Modifier.walkthroughTarget(WalkthroughTargets.TEMPLATE_CREATE),
                    leadingIcon = { ButtonIcon(R.drawable.ic_edit_note) },
                )
            }
        },
        landscapePaneArrangement = Arrangement.Bottom,
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
                    preview = if (isSelectionMode) previews[template.id] else null,
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
                    },
                    modifier = if (template == templates.first()) {
                        Modifier.walkthroughTarget(WalkthroughTargets.TEMPLATE_FIRST)
                    } else {
                        Modifier
                    },
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

/** Example template shown in [TemplateHelpSection]. */
private const val EXAMPLE_TEMPLATE = "https://www.google.com/search?q=%s"

/** Most lines of selected text shown in [SelectedTextSection]. */
private const val SELECTED_TEXT_MAX_LINES = 6

/**
 * The text templates will be applied to, shown read-only.
 *
 * @param text Selected text.
 */
@Composable
private fun SelectedTextSection(text: String) {
    Column(verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s)) {
        SectionLabel(text = stringResource(R.string.template_selected_text))
        GlowCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = text,
                style = TabotaboTheme.typography.body,
                color = TabotaboTheme.colors.accent,
                maxLines = SELECTED_TEXT_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * How templates work: the placeholder rules and a worked URL example.
 *
 * @param exampleInput Sample selected text.
 * @param exampleResult [EXAMPLE_TEMPLATE] applied to [exampleInput].
 */
@Composable
private fun TemplateHelpSection(exampleInput: String, exampleResult: String) {
    val spacing = TabotaboTheme.dimensions.spacing

    Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
        SectionLabel(text = stringResource(R.string.template_help_title))
        Subtitle(text = stringResource(R.string.template_help_body), maxLines = Int.MAX_VALUE)
        GlowCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                Fact(title = stringResource(R.string.template_example_template), value = EXAMPLE_TEMPLATE)
                Fact(title = stringResource(R.string.template_example_text), value = exampleInput)
                Fact(title = stringResource(R.string.template_example_result), value = exampleResult)
            }
        }
    }
}

/**
 * Card of one template: its name and value (or, when given, its [preview]), with a delete
 * action outside selection mode.
 *
 * @param template Template to show.
 * @param isSelectionMode Whether tapping applies the template (no delete action then).
 * @param preview Result of applying the template to the selected text, shown instead of its
 * value; null to show the value.
 * @param onClick Called when the card is tapped.
 * @param onDelete Called when the delete action is tapped.
 * @param modifier Modifier for the card, e.g. to make it a walkthrough target.
 */
@Composable
fun TemplateItem(
    template: TextTemplate,
    isSelectionMode: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    preview: String? = null,
) {
    GlowCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template.name,
                    style = TabotaboTheme.typography.titleMedium,
                    color = TabotaboTheme.colors.accent,
                )
                Subtitle(text = preview ?: template.template)
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
