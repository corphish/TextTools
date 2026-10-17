package com.corphish.quicktools.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Appearance
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.ui.common.AppScreenHeader
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.theme.AppTheme
import com.corphish.quicktools.ui.theme.LocalHueState
import com.corphish.quicktools.viewmodels.SettingsViewModel
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.settings.AppearanceSetting
import com.tabotabo.ui.settings.SettingsSection
import com.tabotabo.ui.settings.ShuffleColorsButton
import com.tabotabo.ui.surface.SelectablePill
import com.tabotabo.ui.surface.SelectablePillRow
import com.tabotabo.ui.text.Fact
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.label
import dagger.hilt.android.AndroidEntryPoint

/** Minimum content width at which settings groups are laid out in two columns. */
private val TwoColumnMinWidth = 600.dp

/** Size of the icon next to a settings group label. */
private val GroupIconSize = 16.dp

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme { Settings(settingsViewModel) }
        }
    }

    override fun onStop() {
        super.onStop()

        settingsViewModel.invalidateCountryCodePrependSetting()
    }
}

/**
 * Settings screen: labelled groups of settings in one column, or two when the content is wide.
 * Portrait keeps the header fixed and scrolls the groups; landscape puts the header on top and
 * scrolls the whole page.
 *
 * @param settingsViewModel Source and sink of all settings.
 */
@Composable
fun Settings(settingsViewModel: SettingsViewModel) {
    val uriHandler = LocalUriHandler.current
    val activity = LocalActivity.current
    val versionName by settingsViewModel.appVersionName.collectAsState()
    val versionCode by settingsViewModel.appVersionCode.collectAsState()
    val spacing = TabotaboTheme.dimensions.spacing

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        // In landscape, put the header on top and scroll the whole page instead of the
        // scaffold's side-by-side panes, which would leave the header pane mostly empty
        val isLandscape = maxWidth > maxHeight

        ScreenScaffold(
            header = {
                AppScreenHeader(id = R.string.title_activity_settings, onBack = { activity?.finish() })
            },
            webLayout = isLandscape,
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                // Groups have no cards, so space them further apart than the settings inside them
                val groupSpacing = Arrangement.spacedBy(spacing.xl + spacing.l)
                // The page scrolls as a whole in landscape, so only scroll here in portrait
                val scrollModifier = if (isLandscape) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                }

                if (maxWidth >= TwoColumnMinWidth) {
                    Row(modifier = scrollModifier, horizontalArrangement = Arrangement.spacedBy(spacing.xl + spacing.l)) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = groupSpacing) {
                            AppSettings(settingsViewModel)
                            EvaluateSettings(settingsViewModel)
                            TemplateSettings(settingsViewModel)
                        }
                        Column(modifier = Modifier.weight(1f), verticalArrangement = groupSpacing) {
                            TextSettings(settingsViewModel)
                            AppInfoSettings(versionName, versionCode, uriHandler)
                            DonateSettings(uriHandler)
                        }
                    }
                } else {
                    Column(modifier = scrollModifier, verticalArrangement = groupSpacing) {
                        AppSettings(settingsViewModel)
                        TextSettings(settingsViewModel)
                        EvaluateSettings(settingsViewModel)
                        TemplateSettings(settingsViewModel)
                        AppInfoSettings(versionName, versionCode, uriHandler)
                        DonateSettings(uriHandler)
                    }
                }
            }
        }
    }
}

/**
 * A group of settings: a small primary label with an icon above the group's settings.
 *
 * @param title String resource of the group label.
 * @param icon Drawable resource of the group icon.
 * @param content Settings of the group, spaced apart.
 */
@Composable
fun SettingsGroup(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = TabotaboTheme.colors
    val spacing = TabotaboTheme.dimensions.spacing

    Column {
        Row(
            modifier = Modifier.padding(bottom = spacing.m),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(GroupIconSize),
                tint = colors.primary
            )
            Text(
                text = stringResource(id = title),
                style = TabotaboTheme.typography.label,
                color = colors.primary,
                modifier = Modifier.padding(start = spacing.s)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing.xl),
            content = content
        )
    }
}

/**
 * App-wide settings: appearance, color shuffle and how context menu options appear.
 *
 * @param settingsViewModel Source and sink of the settings.
 */
@Composable
fun AppSettings(settingsViewModel: SettingsViewModel) {
    val appMode by settingsViewModel.appMode.collectAsState()
    val appearance by settingsViewModel.appearance.collectAsState()
    val appearanceLabels = mapOf(
        Appearance.SYSTEM to stringResource(R.string.appearance_system),
        Appearance.LIGHT to stringResource(R.string.appearance_light),
        Appearance.DARK to stringResource(R.string.appearance_dark),
    )
    val modeLabels = mapOf(
        AppMode.SINGLE to stringResource(R.string.mode_single_title),
        AppMode.MULTI to stringResource(R.string.mode_multi_title)
    )

    SettingsGroup(title = R.string.app, icon = R.drawable.ic_settings) {
        Column(verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m)) {
            AppearanceSetting(
                options = Appearance.entries,
                selected = appearance,
                onSelect = settingsViewModel::updateAppearance,
                label = { appearanceLabels.getValue(it) },
                title = stringResource(R.string.appearance),
                description = stringResource(R.string.appearance_desc),
            )
            ShuffleColorsButton(
                hueState = LocalHueState.current,
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.shuffle_colors),
            )
        }

        SettingsSection(
            title = stringResource(R.string.mode_select_title),
            description = stringResource(R.string.mode_select_desc),
        ) {
            SelectablePillRow(
                options = modeLabels.keys.toList(),
                selected = appMode,
                onSelect = settingsViewModel::updateAppMode,
                label = { modeLabels.getValue(it) },
            )
        }
    }
}

/**
 * Messaging settings: automatic country code and its value.
 *
 * @param settingsViewModel Source and sink of the settings.
 */
@Composable
fun TextSettings(settingsViewModel: SettingsViewModel) {
    val prependCountryCodeEnabled by settingsViewModel.prependCountryCodeEnabled.collectAsState()
    val prependCountryCode by settingsViewModel.prependCountryCode.collectAsState()
    val prependCountryCodeIsValid by settingsViewModel.prependCountryCodeIsValid.collectAsState()

    SettingsGroup(title = R.string.messaging, icon = R.drawable.ic_whatsapp) {
        SwitchSetting(
            title = stringResource(id = R.string.prepend_country_code_title),
            description = stringResource(id = R.string.prepend_country_code_desc),
            checked = prependCountryCodeEnabled,
            onCheckedChange = settingsViewModel::updatePrependCountryCodeEnabled,
        )

        OutlinedTextField(
            value = prependCountryCode ?: "",
            onValueChange = {
                settingsViewModel.updatePrependCountryCode(it)
            },
            label = { Text(stringResource(id = R.string.preset_country_code)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = prependCountryCodeEnabled,
            isError = prependCountryCodeEnabled && !prependCountryCodeIsValid,
            singleLine = true,
            supportingText = {
                if (prependCountryCodeEnabled && !prependCountryCodeIsValid) {
                    Text(text = stringResource(id = R.string.invalid_country_code))
                }
            }
        )
    }
}

/**
 * A setting toggled by a switch; the whole row is tappable.
 *
 * @param title Title of the setting.
 * @param description Muted explanation under the title.
 * @param checked Whether the setting is on.
 * @param onCheckedChange Called with the new state.
 */
@Composable
fun SwitchSetting(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = TabotaboTheme.typography.labelLarge, color = TabotaboTheme.colors.accent)
            Subtitle(text = description, maxLines = Int.MAX_VALUE)
        }
        Switch(
            checked = checked,
            onCheckedChange = null,
            modifier = Modifier.padding(start = TabotaboTheme.dimensions.spacing.l)
        )
    }
}

/**
 * Evaluate settings: decimal points and what to do with the result.
 *
 * @param settingsViewModel Source and sink of the settings.
 */
@Composable
fun EvaluateSettings(settingsViewModel: SettingsViewModel) {
    val decimalPoints by settingsViewModel.decimalPoints.collectAsState()
    val selectedMode by settingsViewModel.evalResultMode.collectAsState()
    val options = listOf(
        stringResource(id = R.string.eval_mode_ask_next_time),
        stringResource(id = R.string.eval_mode_result),
        stringResource(id = R.string.eval_mode_append),
        stringResource(id = R.string.eval_mode_copy)
    )

    SettingsGroup(title = R.string.eval_title_small, icon = R.drawable.ic_numbers) {
        SettingsSection(
            title = stringResource(id = R.string.decimal_points_title),
            description = stringResource(id = R.string.decimal_points_desc),
        ) {
            Text(
                text = "$decimalPoints",
                style = TabotaboTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TabotaboTheme.colors.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            val sliderState = rememberSliderState(value = decimalPoints.toFloat(), steps = 3, trackRange = 1f..5f)
            Slider(
                state = sliderState,
                onValueChange = {
                    // A custom onValueChange replaces the state's own update, so apply it here.
                    sliderState.value = it
                    settingsViewModel.updateDecimalPoints(it.toInt())
                }
            )
        }

        SettingsSection(
            title = stringResource(id = R.string.eval_result_handling),
            description = stringResource(id = R.string.eval_result_desc),
        ) {
            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s),
            ) {
                options.forEachIndexed { index, option ->
                    SelectablePill(
                        text = option,
                        selected = index == selectedMode,
                        onClick = { settingsViewModel.updateEvaluateResultMode(index) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

/**
 * Text template settings: delete all templates.
 *
 * @param settingsViewModel Performs the deletion.
 */
@Composable
fun TemplateSettings(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current

    SettingsGroup(title = R.string.text_template, icon = R.drawable.ic_edit_note) {
        SettingsSection(
            title = stringResource(id = R.string.delete_all_templates),
            description = stringResource(id = R.string.delete_all_templates_desc),
        ) {
            SecondaryButton(
                text = stringResource(R.string.delete),
                onClick = {
                    settingsViewModel.clearAllTemplates()
                    Toast.makeText(context, R.string.text_template_all_deleted, Toast.LENGTH_SHORT).show()
                },
                leadingIcon = { ButtonIcon(R.drawable.ic_delete) },
            )
        }
    }
}

/**
 * App info: version, a link to the releases page and open source links.
 *
 * @param versionName App version name.
 * @param versionCode App version code.
 * @param uriHandler Opens the linked pages.
 */
@Composable
fun AppInfoSettings(
    versionName: String,
    versionCode: Int,
    uriHandler: UriHandler
) {
    SettingsGroup(title = R.string.app_info, icon = R.drawable.ic_info) {
        Fact(title = stringResource(id = R.string.version), value = "$versionName ($versionCode)")

        Column(verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.m)) {
            Subtitle(text = stringResource(id = R.string.releases_desc), maxLines = Int.MAX_VALUE)
            SecondaryButton(
                text = stringResource(id = R.string.releases),
                onClick = { uriHandler.openUri(Constants.RELEASES_PAGE_LINK) },
                trailingIcon = { ButtonIcon(R.drawable.ic_open_in_new) },
            )
        }

        SettingsSection(
            title = stringResource(id = R.string.oss_info),
            description = stringResource(id = R.string.oss_desc),
        ) {
            // Side by side; labels that don't fit scroll as a marquee
            Row(horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s)) {
                SecondaryButton(
                    text = stringResource(id = R.string.oss_check),
                    onClick = { uriHandler.openUri(Constants.SOURCE_LINK) },
                    modifier = Modifier.weight(1f),
                    trailingIcon = { ButtonIcon(R.drawable.ic_open_in_new) },
                )
                SecondaryButton(
                    text = stringResource(id = R.string.contributors),
                    onClick = { uriHandler.openUri(Constants.CONTRIBUTORS_LINK) },
                    modifier = Modifier.weight(1f),
                    trailingIcon = { ButtonIcon(R.drawable.ic_open_in_new) },
                )
            }
        }
    }
}

/**
 * Donation prompt with a button to the donation page.
 *
 * @param uriHandler Opens the donation page.
 */
@Composable
fun DonateSettings(uriHandler: UriHandler) {
    SettingsGroup(title = R.string.donate, icon = R.drawable.ic_volunteer) {
        Subtitle(text = stringResource(id = R.string.donate_msg), maxLines = Int.MAX_VALUE)
        PrimaryButton(
            text = stringResource(id = R.string.donate),
            onClick = { uriHandler.openUri(Constants.DONATE_LINK) },
            leadingIcon = { ButtonIcon(R.drawable.ic_volunteer) },
        )
    }
}
