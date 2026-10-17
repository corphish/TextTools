package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.data.TextInputType
import com.corphish.quicktools.ui.common.AppScreenHeader
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.common.CardTextField
import com.corphish.quicktools.ui.common.CompactLandscapeLayout
import com.corphish.quicktools.ui.common.FormDialog
import com.corphish.quicktools.ui.common.wordCountTextStyle
import com.corphish.quicktools.ui.theme.AppTheme
import com.corphish.quicktools.viewmodels.TextTransformViewModel
import com.corphish.quicktools.viewmodels.ThemeViewModel
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.surface.SelectablePillRow
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.body
import com.tabotabo.ui.typography.label
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransformActivity : ComponentActivity() {

    // Same instance as the theme's; used to pass this screen's hue to the screens it opens
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            val forceCopy = intent.getBooleanExtra(Constants.INTENT_FORCE_COPY, false)

            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()

            setContent {
                AppTheme {
                    TextTransformUI(
                        textToTransform = text,
                        onBack = { finish() },
                        onNext = { text ->
                            val nextIntent = Intent(this, TextActionActivity::class.java)
                            nextIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, text)
                            nextIntent.putExtra(
                                Intent.EXTRA_PROCESS_TEXT_READONLY, intent.getBooleanExtra(
                                    Intent.EXTRA_PROCESS_TEXT_READONLY, true
                                )
                            )
                            nextIntent.putExtra(Constants.INTENT_FORCE_COPY, forceCopy)
                            nextIntent.putExtra(Constants.INTENT_HUE, themeViewModel.hue.value)
                            startActivity(nextIntent)
                            finish()
                        },
                    )
                }
            }
        } else {
            finish()
        }
    }
}

/** Minimum time between two "preview is read-only" toasts, in milliseconds. */
private const val READ_ONLY_TOAST_INTERVAL_MS = 2000L

/**
 * Transform screen: one text card showing the original (editable) or the preview (read-only),
 * a row with the Original/Preview toggle and a button naming the current transform, and the
 * Next button. The transform button opens a dialog with every transform option.
 * - Portrait: text card with the toolbar row and Next button below.
 * - Landscape: header and Next button in one row, the text card filling the rest, the toolbar
 *   row underneath.
 *
 * @param textToTransform Initial input text.
 * @param onBack Called when the user leaves the screen.
 * @param onNext Called with the transformed text to continue to the text actions.
 */
@Composable
fun TextTransformUI(
    textToTransform: String,
    onBack: () -> Unit = {},
    onNext: (String) -> Unit = {},
) {
    val viewModel: TextTransformViewModel = hiltViewModel()
    val inputText by viewModel.mainText.collectAsState()
    val previewText by viewModel.previewText.collectAsState()

    var optionsExpanded by rememberSaveable { mutableStateOf(false) }
    var showPreview by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.initializeText(textToTransform)

        viewModel.decorateTextErrorFlow.collect {
            if (it) {
                Toast.makeText(context, R.string.generic_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val header: @Composable () -> Unit = {
        AppScreenHeader(id = R.string.title_activity_transform, onBack = onBack)
    }
    val nextButton: @Composable () -> Unit = {
        PrimaryButton(
            text = stringResource(id = R.string.next),
            onClick = { onNext(previewText) },
            trailingIcon = { ButtonIcon(R.drawable.ic_arrow_right) },
        )
    }
    val toolbar: @Composable () -> Unit = {
        TransformToolbar(
            viewModel = viewModel,
            showPreview = showPreview,
            onShowPreviewChange = { showPreview = it },
            onOptionsClick = { optionsExpanded = true },
        )
    }
    val textArea: @Composable (Modifier) -> Unit = { modifier ->
        TransformTextArea(
            inputText = inputText,
            previewText = previewText,
            showPreview = showPreview,
            onInputTextChanged = { viewModel.initializeText(it) },
            modifier = modifier,
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth > maxHeight) {
            CompactLandscapeLayout(
                header = header,
                headerAction = nextButton,
                bottomBar = toolbar,
            ) {
                textArea(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        } else {
            ScreenScaffold(
                header = header,
                actions = {
                    toolbar()
                    nextButton()
                }
            ) {
                textArea(Modifier.fillMaxSize())
            }
        }
    }

    if (optionsExpanded) {
        TransformOptionsDialog(
            viewModel = viewModel,
            onDismiss = {
                optionsExpanded = false
                showPreview = true
            },
        )
    }
}

/**
 * The text card: the original text (editable) or the preview (read-only). Typing while the
 * preview is shown is ignored and a toast explains how to edit.
 *
 * @param inputText Original text.
 * @param previewText Transformed text.
 * @param showPreview Whether the preview is shown instead of the original.
 * @param onInputTextChanged Called when the user edits the original text.
 * @param modifier Modifier for the card.
 */
@Composable
private fun TransformTextArea(
    inputText: String,
    previewText: String,
    showPreview: Boolean,
    onInputTextChanged: (String) -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current
    var lastReadOnlyToastAt by remember { mutableLongStateOf(0L) }
    val text = if (showPreview) previewText else inputText

    CardTextField(
        value = text,
        onValueChange = {
            if (showPreview) {
                // Edits only go to the original; tell the user, without stacking toasts
                val now = SystemClock.elapsedRealtime()
                if (now - lastReadOnlyToastAt > READ_ONLY_TOAST_INTERVAL_MS) {
                    lastReadOnlyToastAt = now
                    Toast.makeText(context, R.string.preview_read_only, Toast.LENGTH_SHORT).show()
                }
            } else {
                onInputTextChanged(it)
            }
        },
        modifier = modifier,
        textFieldModifier = Modifier.fillMaxSize(),
        placeholder = stringResource(if (showPreview) R.string.preview_text_placeholder else R.string.enter_text_here),
        highlighted = showPreview,
        textStyle = wordCountTextStyle(text),
    )
}

/**
 * Original/Preview toggle beside a button naming the current transform ("transform ·
 * sub-option"); the button opens the transform options.
 *
 * @param viewModel Source of the selected transform.
 * @param showPreview Whether the preview is shown.
 * @param onShowPreviewChange Called to switch between original and preview.
 * @param onOptionsClick Opens the transform options.
 */
@Composable
private fun TransformToolbar(
    viewModel: TextTransformViewModel,
    showPreview: Boolean,
    onShowPreviewChange: (Boolean) -> Unit,
    onOptionsClick: () -> Unit,
) {
    val selectedPrimaryIndex by viewModel.selectedPrimaryIndex.collectAsState()
    val selectedSecondaryIndex by viewModel.selectedSecondaryIndex.collectAsState()
    val secondaryList by viewModel.secondaryOptionList.collectAsState()

    val modeLabels = mapOf(
        false to stringResource(R.string.original),
        true to stringResource(R.string.preview),
    )
    val primaryText = stringResource(TextTransformViewModel.transformOptions[selectedPrimaryIndex])
    val summary = if (secondaryList.isEmpty()) {
        primaryText
    } else {
        "$primaryText · ${stringResource(secondaryList[selectedSecondaryIndex])}"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SelectablePillRow(
            options = listOf(false, true),
            selected = showPreview,
            onSelect = onShowPreviewChange,
            label = { modeLabels.getValue(it) },
            modifier = Modifier.weight(1f),
        )
        // Long summaries scroll as a marquee inside the button
        SecondaryButton(
            text = summary,
            onClick = onOptionsClick,
            modifier = Modifier.weight(1f),
            leadingIcon = { ButtonIcon(R.drawable.ic_auto_fix_high) },
        )
    }
}

/**
 * Dialog with all transform options: a selector for the transform, below it a selector for
 * its sub-option (if any) and the extra input (if needed). Tapping a selector reveals its
 * options. Choices apply immediately, so the preview behind the dialog updates live.
 *
 * @param viewModel Holds and applies the transform options.
 * @param onDismiss Called when the dialog is closed.
 */
@Composable
private fun TransformOptionsDialog(
    viewModel: TextTransformViewModel,
    onDismiss: () -> Unit,
) {
    val selectedPrimaryIndex by viewModel.selectedPrimaryIndex.collectAsState()
    val selectedSecondaryIndex by viewModel.selectedSecondaryIndex.collectAsState()
    val secondaryList by viewModel.secondaryOptionList.collectAsState()

    val secondaryFunctionText by viewModel.secondaryFunctionText.collectAsState()
    val secondaryFunctionTextLabel by viewModel.secondaryFunctionTextLabel.collectAsState()
    val secondaryFunctionTextInputType by viewModel.secondaryFunctionTextInputType.collectAsState()
    val secondaryFunctionTextEnabled by viewModel.secondaryFunctionTextEnabled.collectAsState()
    val secondaryFunctionTextVisible by viewModel.secondaryFunctionTextVisible.collectAsState()

    val spacing = TabotaboTheme.dimensions.spacing

    FormDialog(
        title = stringResource(R.string.transform_long),
        confirmText = stringResource(R.string.done),
        onConfirm = onDismiss,
        onDismissRequest = onDismiss,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.m)) {
            ExpandableSelector(
                icon = R.drawable.ic_auto_fix_high,
                options = TextTransformViewModel.transformOptions,
                selectedIndex = selectedPrimaryIndex,
                onSelect = { viewModel.selectPrimaryIndex(it) },
            )

            if (secondaryList.isNotEmpty()) {
                // Keyed so the sub-option card starts collapsed for each transform
                key(selectedPrimaryIndex) {
                    ExpandableSelector(
                        icon = R.drawable.ic_auto_awesome,
                        options = secondaryList,
                        selectedIndex = selectedSecondaryIndex,
                        onSelect = { viewModel.selectSecondaryIndex(it) },
                    )
                }
            }

            if (secondaryFunctionTextVisible) {
                OutlinedTextField(
                    value = secondaryFunctionText,
                    enabled = secondaryFunctionTextEnabled,
                    onValueChange = { viewModel.setSecondaryText(it) },
                    keyboardOptions = KeyboardOptions(keyboardType = secondaryFunctionTextInputType.toKeyboardType()),
                    label = { Text(stringResource(id = secondaryFunctionTextLabel)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * Card showing the selected option with a chevron. Tapping it reveals all options inside the
 * card; picking one selects it and collapses the card again.
 *
 * @param icon Drawable resource shown before the selected option.
 * @param options String resources of the options.
 * @param selectedIndex Index of the selected option.
 * @param onSelect Called with the index of the picked option.
 */
@Composable
private fun ExpandableSelector(
    @DrawableRes icon: Int,
    options: List<Int>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val colors = TabotaboTheme.colors
    val spacing = TabotaboTheme.dimensions.spacing
    val chevronRotation by animateFloatAsState(if (expanded) 90f else 0f, label = "chevron")

    GlowCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        highlighted = expanded,
        glow = false,
        contentPadding = PaddingValues(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(role = Role.DropdownList) { expanded = !expanded }
                .padding(horizontal = spacing.l, vertical = spacing.m),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ButtonIcon(icon, tint = colors.primary)
            Text(
                text = stringResource(options[selectedIndex.coerceIn(options.indices)]),
                style = TabotaboTheme.typography.body,
                color = colors.accent,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = spacing.m)
            )
            Box(modifier = Modifier.rotate(chevronRotation)) {
                ButtonIcon(R.drawable.ic_arrow_right, tint = colors.muted)
            }
        }

        if (expanded) {
            Column(modifier = Modifier.selectableGroup()) {
                options.forEachIndexed { index, option ->
                    val selected = index == selectedIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(selected = selected, role = Role.RadioButton) {
                                onSelect(index)
                                expanded = false
                            }
                            .padding(horizontal = spacing.l, vertical = spacing.m),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Reserve the check's space so all options line up
                        Box(modifier = Modifier.size(OptionCheckSize)) {
                            if (selected) ButtonIcon(R.drawable.ic_done, tint = colors.primary)
                        }
                        Text(
                            text = stringResource(option),
                            style = TabotaboTheme.typography.body,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) colors.primary else colors.accent,
                            modifier = Modifier.padding(start = spacing.m)
                        )
                    }
                }
            }
        }
    }
}

/** Space reserved for the check next to each expanded option. */
private val OptionCheckSize = 20.dp

private fun TextInputType.toKeyboardType(): KeyboardType = when (this) {
    TextInputType.NUMBER -> KeyboardType.Number
    TextInputType.TEXT -> KeyboardType.Text
}

@Composable
@Preview
fun TextTransformUIPreview() {
    TextTransformUI(
        textToTransform = "Text to transform",
    )
}
