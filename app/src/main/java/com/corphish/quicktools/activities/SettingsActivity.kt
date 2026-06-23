package com.corphish.quicktools.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.window.core.layout.WindowSizeClass
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.ui.common.CustomTopAppBar
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2
import com.corphish.quicktools.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickToolsTheme { Settings(settingsViewModel) }
        }
    }

    override fun onStop() {
        super.onStop()

        settingsViewModel.invalidateCountryCodePrependSetting()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(settingsViewModel: SettingsViewModel) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isWideScreen =
        adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    val uriHandler = LocalUriHandler.current
    val activity = LocalActivity.current
    val versionName by settingsViewModel.appVersionName.collectAsState()
    val versionCode by settingsViewModel.appVersionCode.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CustomTopAppBar(
                id = R.string.title_activity_settings,
                onNavigationClick = { activity?.finish() },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        val contentModifier = Modifier
            .padding(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr).plus(16.dp),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr).plus(16.dp),
                top = innerPadding.calculateTopPadding().plus(16.dp),
                bottom = innerPadding.calculateBottomPadding().plus(16.dp)
            )
            .fillMaxSize()
            .verticalScroll(rememberScrollState())

        if (isWideScreen) {
            Row(
                modifier = contentModifier,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SettingsSection(title = R.string.app, icon = R.drawable.ic_settings) {
                        AppSettings(settingsViewModel)
                    }
                    SettingsSection(
                        title = R.string.eval_title_small,
                        icon = R.drawable.ic_numbers,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        EvaluateSettings(settingsViewModel)
                    }
                    SettingsSection(
                        title = R.string.text_template,
                        icon = R.drawable.ic_edit_note,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        TemplateSettings(settingsViewModel)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    SettingsSection(title = R.string.messaging, icon = R.drawable.ic_whatsapp) {
                        TextSettings(settingsViewModel)
                    }
                    SettingsSection(
                        title = R.string.app_info,
                        icon = R.drawable.ic_info,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        AppInfoSettings(versionName, versionCode, uriHandler)
                    }
                    SettingsSection(
                        title = R.string.donate,
                        icon = R.drawable.ic_volunteer,
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        DonateSettings(uriHandler)
                    }
                }
            }
        } else {
            Column(modifier = contentModifier) {
                SettingsSection(title = R.string.app, icon = R.drawable.ic_settings) {
                    AppSettings(settingsViewModel)
                }
                SettingsSection(
                    title = R.string.messaging,
                    icon = R.drawable.ic_whatsapp,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    TextSettings(settingsViewModel)
                }
                SettingsSection(
                    title = R.string.eval_title_small,
                    icon = R.drawable.ic_numbers,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    EvaluateSettings(settingsViewModel)
                }
                SettingsSection(
                    title = R.string.text_template,
                    icon = R.drawable.ic_edit_note,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    TemplateSettings(settingsViewModel)
                }
                SettingsSection(
                    title = R.string.app_info,
                    icon = R.drawable.ic_info,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    AppInfoSettings(versionName, versionCode, uriHandler)
                }
                SettingsSection(
                    title = R.string.donate,
                    icon = R.drawable.ic_volunteer,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    DonateSettings(uriHandler)
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: Int,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(id = title),
                style = TypographyV2.labelSmall,
                fontFamily = BrandFontFamily,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Surface(
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    content()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettings(settingsViewModel: SettingsViewModel) {
    val appMode by settingsViewModel.appMode.collectAsState()
    val options = mapOf(
        AppMode.SINGLE to R.string.mode_single_title,
        AppMode.MULTI to R.string.mode_multi_title
    )
    var expanded by rememberSaveable { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column {
            Text(
                text = stringResource(id = R.string.mode_select_title),
                style = TypographyV2.labelMedium,
                fontWeight = FontWeight.W600,
            )
            Text(
                text = stringResource(id = R.string.mode_select_desc),
                style = TypographyV2.bodySmall
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = stringResource(id = options[appMode]!!),
                onValueChange = { },
                label = { Text(stringResource(id = R.string.mode_select_title)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        true
                    )
                    .fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier.exposedDropdownSize()
            ) {
                options.forEach { (mode, resId) ->
                    DropdownMenuItem(
                        text = {
                            Text(text = stringResource(resId))
                        },
                        onClick = {
                            settingsViewModel.updateAppMode(mode)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TextSettings(settingsViewModel: SettingsViewModel) {
    val prependCountryCodeEnabled by settingsViewModel.prependCountryCodeEnabled.collectAsState()
    val prependCountryCode by settingsViewModel.prependCountryCode.collectAsState()
    val prependCountryCodeIsValid by settingsViewModel.prependCountryCodeIsValid.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ConstraintLayout(
            modifier = Modifier
                .clickable {
                    settingsViewModel.updatePrependCountryCodeEnabled(!prependCountryCodeEnabled)
                }
                .fillMaxWidth()
        ) {
            val (switch, texts) = createRefs()

            Switch(
                checked = prependCountryCodeEnabled,
                onCheckedChange = {
                    settingsViewModel.updatePrependCountryCodeEnabled(it)
                },
                modifier = Modifier.constrainAs(switch) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
            )

            Column(
                modifier = Modifier.constrainAs(texts) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(switch.start, margin = 16.dp)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text(
                    text = stringResource(id = R.string.prepend_country_code_title),
                    style = TypographyV2.labelMedium,
                    fontWeight = FontWeight.W600,
                )
                Text(
                    text = stringResource(id = R.string.prepend_country_code_desc),
                    style = TypographyV2.bodySmall
                )
            }
        }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluateSettings(settingsViewModel: SettingsViewModel) {
    val decimalPoints by settingsViewModel.decimalPoints.collectAsState()

    val options = listOf(
        stringResource(id = R.string.eval_mode_ask_next_time),
        stringResource(id = R.string.eval_mode_result),
        stringResource(id = R.string.eval_mode_append),
        stringResource(id = R.string.eval_mode_copy)
    )
    var expanded by rememberSaveable { mutableStateOf(false) }
    val selectedOptionText by settingsViewModel.evalResultMode.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column {
            Text(
                text = stringResource(id = R.string.decimal_points_title),
                style = TypographyV2.labelMedium,
                fontWeight = FontWeight.W600,
            )
            Text(
                text = stringResource(id = R.string.decimal_points_desc),
                style = TypographyV2.bodySmall
            )
        }

        Text(
            text = "$decimalPoints",
            style = TypographyV2.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Slider(
            value = decimalPoints.toFloat(),
            onValueChange = {
                settingsViewModel.updateDecimalPoints(it.toInt())
            },
            valueRange = 1f..5f,
            steps = 3
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Column {
            Text(
                text = stringResource(id = R.string.eval_result_handling),
                style = TypographyV2.labelMedium,
                fontWeight = FontWeight.W600,
            )
            Text(
                text = stringResource(id = R.string.eval_result_desc),
                style = TypographyV2.bodySmall
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = options[selectedOptionText],
                onValueChange = { },
                label = { Text(stringResource(id = R.string.eval_result_handling)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor(
                        ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        true
                    )
                    .fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier.exposedDropdownSize()
            ) {
                options.forEachIndexed { index, selectionOption ->
                    DropdownMenuItem(
                        text = {
                            Text(text = selectionOption)
                        },
                        onClick = {
                            settingsViewModel.updateEvaluateResultMode(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TemplateSettings(settingsViewModel: SettingsViewModel) {
    val listItemColors = ListItemDefaults.colors(containerColor = Color.Transparent)
    val context = LocalContext.current

    ListItem(
        headlineContent = {
            Text(
                text = stringResource(id = R.string.delete_all_templates),
                style = TypographyV2.labelMedium,
                fontWeight = FontWeight.W600
            )
        },
        supportingContent = {
            Text(
                text = stringResource(id = R.string.delete_all_templates_desc),
                style = TypographyV2.bodySmall
            )
        },
        leadingContent = {
            Icon(
                painterResource(R.drawable.ic_delete),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
        },
        modifier = Modifier.clickable {
            settingsViewModel.clearAllTemplates()
            Toast.makeText(context, R.string.text_template_all_deleted, Toast.LENGTH_SHORT).show()
        },
        colors = listItemColors
    )
}

@Composable
fun AppInfoSettings(
    versionName: String,
    versionCode: Int,
    uriHandler: androidx.compose.ui.platform.UriHandler
) {
    val listItemColors = ListItemDefaults.colors(containerColor = Color.Transparent)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.version),
                    style = TypographyV2.labelMedium,
                    fontWeight = FontWeight.W600
                )
            },
            supportingContent = {
                Text(
                    text = "$versionName ($versionCode)",
                    style = TypographyV2.bodySmall
                )
            },
            leadingContent = {
                Icon(
                    painterResource(R.drawable.ic_info),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            },
            colors = listItemColors
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.releases),
                    style = TypographyV2.labelMedium,
                    fontWeight = FontWeight.W600
                )
            },
            supportingContent = {
                Text(
                    text = stringResource(id = R.string.releases_desc),
                    style = TypographyV2.bodySmall
                )
            },
            leadingContent = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_open_in_new),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier.clickable { uriHandler.openUri(Constants.RELEASES_PAGE_LINK) },
            colors = listItemColors
        )
    }
}

@Composable
fun DonateSettings(uriHandler: androidx.compose.ui.platform.UriHandler) {
    val listItemColors = ListItemDefaults.colors(containerColor = Color.Transparent)
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = stringResource(id = R.string.donate_msg),
            style = TypographyV2.bodySmall
        )

        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.donate),
                    style = TypographyV2.labelMedium,
                    fontWeight = FontWeight.W600
                )
            },
            leadingContent = {
                Icon(
                    painterResource(R.drawable.ic_volunteer),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier.clickable { uriHandler.openUri(Constants.DONATE_LINK) },
            colors = listItemColors
        )
    }
}
