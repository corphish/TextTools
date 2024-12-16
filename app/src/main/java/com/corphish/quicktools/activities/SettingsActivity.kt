package com.corphish.quicktools.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import com.corphish.quicktools.R
import com.corphish.quicktools.ui.common.CustomTopAppBar
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.Typography
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(settingsViewModel: SettingsViewModel) {
    val uriHandler = LocalUriHandler.current
    val activity = (LocalContext.current as? Activity)
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
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = it.calculateStartPadding(LayoutDirection.Ltr).plus(16.dp),
                    end = it.calculateEndPadding(LayoutDirection.Ltr).plus(16.dp),
                    top = it.calculateTopPadding().plus(16.dp),
                    bottom = it.calculateBottomPadding().plus(16.dp)
                )
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(id = R.string.messaging),
                style = TypographyV2.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp),
                fontFamily = BrandFontFamily,
                color = MaterialTheme.colorScheme.primary
            )

            TextSettings(settingsViewModel)

            Text(
                text = stringResource(id = R.string.eval_title_small),
                style = TypographyV2.labelSmall,
                modifier = Modifier.padding(top = 24.dp, bottom = 4.dp),
                fontFamily = BrandFontFamily,
                color = MaterialTheme.colorScheme.primary
            )

            EvaluateSettings(settingsViewModel)

            Text(
                text = stringResource(id = R.string.app_info),
                style = TypographyV2.labelSmall,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                fontFamily = BrandFontFamily,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(
                    id = R.string.app_version,
                    versionName,
                    versionCode
                ),
                style = Typography.bodyMedium
            )

            Button(
                onClick = { uriHandler.openUri("https://github.com/corphish/TextTools/releases") },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(painterResource(id = R.drawable.ic_open_in_new), contentDescription = "")
                Text(
                    text = stringResource(id = R.string.releases),
                    modifier = Modifier.padding(start = 16.dp),
                    style = TypographyV2.labelMedium,
                    fontWeight = FontWeight.W600
                )
            }

            Text(
                text = stringResource(id = R.string.donate),
                style = TypographyV2.labelSmall,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
                fontFamily = BrandFontFamily,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(id = R.string.donate_msg),
                style = Typography.bodyMedium
            )

            Button(
                onClick = { uriHandler.openUri("https://www.paypal.com/paypalme/corphish") },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(Icons.Default.ThumbUp, contentDescription = "")
                Text(
                    text = stringResource(id = R.string.donate),
                    modifier = Modifier.padding(start = 16.dp),
                    style = TypographyV2.labelMedium,
                    fontWeight = FontWeight.W600
                )
            }
        }
    }
}

@Composable
fun TextSettings(settingsViewModel: SettingsViewModel) {
    val prependCountryCodeEnabled by settingsViewModel.prependCountryCodeEnabled.collectAsState()
    val prependCountryCode by settingsViewModel.prependCountryCode.collectAsState()

    Column {
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
                    end.linkTo(parent.end)
                }
            )

            Column(
                modifier = Modifier.constrainAs(texts) {
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            enabled = prependCountryCodeEnabled
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
    var expanded by remember { mutableStateOf(false) }
    val selectedOptionText by settingsViewModel.evalResultMode.collectAsState()

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
        Text(
            text = "$decimalPoints",
            style = TypographyV2.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        Slider(
            value = decimalPoints.toFloat(),
            onValueChange = {
                settingsViewModel.updateDecimalPoints(it.toInt())
            },
            valueRange = 1f..5f,
            steps = 3
        )
        
        Box(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = stringResource(id = R.string.eval_result_handling),
            style = TypographyV2.labelMedium,
            fontWeight = FontWeight.W600,
        )
        Text(
            text = stringResource(id = R.string.eval_result_desc),
            style = TypographyV2.bodySmall
        )
        Box(modifier = Modifier.padding(vertical = 4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                readOnly = true,
                value = options[selectedOptionText],
                onValueChange = { },
                label = { Text(stringResource(id = R.string.eval_result_handling)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth()
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