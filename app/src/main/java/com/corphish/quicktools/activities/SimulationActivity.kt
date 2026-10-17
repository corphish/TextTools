package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.ui.common.AppScreenHeader
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.common.CardTextField
import com.corphish.quicktools.ui.common.CompactLandscapeLayout
import com.corphish.quicktools.ui.common.FocusedLastWord
import com.corphish.quicktools.ui.common.SectionLabel
import com.corphish.quicktools.ui.theme.AppTheme
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.viewmodels.ThemeViewModel
import com.tabotabo.ui.button.ActionStack
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.surface.TabotaboDialog
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SimulationActivity : ComponentActivity() {

    // Same instance as the theme's; used to pass this screen's hue to the screens it opens
    private val themeViewModel: ThemeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Simulation(
                    onBack = { finish() },
                    onSimulate = {
                        val intent = Intent(Intent.ACTION_PROCESS_TEXT).apply {
                            type = "text/plain"
                            setPackage(packageName)
                            putExtra(Intent.EXTRA_PROCESS_TEXT, it)
                            putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
                            putExtra(Constants.INTENT_FORCE_COPY, true)
                            putExtra(Constants.INTENT_HUE, themeViewModel.hue.value)
                        }

                        startActivity(intent)
                    }
                )
            }
        }
    }
}

/**
 * Simulation screen: text typed here is sent to the app's text selection tools, for apps where
 * the Text Tools menu entries do not show up. In landscape the header and the Simulate button
 * share one row so the input gets the rest of the screen.
 *
 * @param onBack Called when the user leaves the screen.
 * @param onSimulate Called with the text to simulate a selection with.
 */
@Composable
fun Simulation(onBack: () -> Unit = {}, onSimulate: (String) -> Unit = {}) {
    var inputText by rememberSaveable { mutableStateOf("") }
    var showInfoDialog by rememberSaveable { mutableStateOf(false) }
    val simulateTitle = stringResource(R.string.simulate)

    if (showInfoDialog) {
        TabotaboDialog(
            onDismissRequest = { showInfoDialog = false },
            confirmText = stringResource(R.string.done),
            onConfirm = { showInfoDialog = false },
            modifier = Modifier.testTag("info_dialog"),
            title = { FocusedLastWord(simulateTitle) },
        ) {
            Subtitle(text = stringResource(R.string.simulate_details), maxLines = Int.MAX_VALUE)
        }
    }

    val header: @Composable () -> Unit = {
        AppScreenHeader(
            id = R.string.title_activity_simulation,
            onBack = onBack,
            actions = {
                IconAction(
                    onClick = { showInfoDialog = true },
                    contentDescription = stringResource(R.string.info),
                    modifier = Modifier.testTag("info_button")
                ) {
                    ButtonIcon(R.drawable.ic_info)
                }
            }
        )
    }
    val simulateButton: @Composable () -> Unit = {
        PrimaryButton(
            text = simulateTitle,
            onClick = { onSimulate(inputText) },
            modifier = Modifier.testTag("simulate_button"),
            leadingIcon = { ButtonIcon(R.drawable.ic_simulate) },
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        if (maxWidth > maxHeight) {
            CompactLandscapeLayout(header = header, headerAction = simulateButton) {
                SimulationInputSection(
                    text = inputText,
                    onValueChange = { inputText = it },
                    showLabel = false,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            }
        } else {
            ScreenScaffold(
                header = header,
                actions = { ActionStack { simulateButton() } }
            ) {
                SimulationInputSection(
                    text = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/**
 * Labelled card holding the text to simulate with.
 *
 * @param text Current text.
 * @param onValueChange Called when the user edits the text.
 * @param modifier Modifier for the section.
 * @param showLabel Whether to show the "Input" label above the card.
 */
@Composable
fun SimulationInputSection(
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
) {
    Column(modifier = modifier) {
        if (showLabel) SectionLabel(
            text = stringResource(id = R.string.input),
            modifier = Modifier
                .padding(bottom = TabotaboTheme.dimensions.spacing.s)
                .testTag("input_label")
        )
        CardTextField(
            value = text,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize(),
            textFieldModifier = Modifier
                .fillMaxSize()
                .testTag("simulation_input"),
            placeholder = stringResource(R.string.input),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SimulationPreview() {
    QuickToolsTheme {
        Simulation()
    }
}
