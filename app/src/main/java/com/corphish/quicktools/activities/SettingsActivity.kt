package com.corphish.quicktools.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.corphish.quicktools.BuildConfig
import com.corphish.quicktools.R
import com.corphish.quicktools.settings.SettingsHelper
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.Typography
import com.corphish.quicktools.ui.theme.TypographyV2

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsHelper = SettingsHelper(this)
        setContent {
            QuickToolsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Settings(settingsHelper)
                }
            }
        }
    }
}

@Composable
fun Settings(settingsHelper: SettingsHelper) {
    val uriHandler = LocalUriHandler.current
    val activity = (LocalContext.current as? Activity)

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            IconButton(
                onClick = { activity?.finish() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = "",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Text(
                text = stringResource(id = R.string.title_activity_settings),
                style = TypographyV2.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = BrandFontFamily,
                modifier = Modifier.padding(start = 16.dp)
            )
        }


        Text(
            text = stringResource(id = R.string.messaging),
            style = TypographyV2.labelSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            fontFamily = BrandFontFamily,
            color = MaterialTheme.colorScheme.primary
        )

        TextSettings(settingsHelper)

        Text(
            text = stringResource(id = R.string.eval_title_small),
            style = TypographyV2.labelSmall,
            modifier = Modifier.padding(top = 24.dp, bottom = 4.dp),
            fontFamily = BrandFontFamily,
            color = MaterialTheme.colorScheme.primary
        )

        EvaluateSettings(settingsHelper)

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
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
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

@Composable
fun TextSettings(settingsHelper: SettingsHelper) {
    var prependCountryCodeEnabled by remember {
        mutableStateOf(settingsHelper.getPrependCountryCodeEnabled())
    }
    var prependCountryCode by remember {
        mutableStateOf(
            settingsHelper.getPrependCountryCode() ?: ""
        )
    }

    Column {
        ConstraintLayout(
            modifier = Modifier
                .clickable {
                    prependCountryCodeEnabled = !prependCountryCodeEnabled
                    settingsHelper.setPrependCountryCodeEnabled(prependCountryCodeEnabled)
                }
                .fillMaxWidth()
        ) {
            val (switch, texts) = createRefs()

            Switch(
                checked = prependCountryCodeEnabled,
                onCheckedChange = {
                    prependCountryCodeEnabled = it
                    settingsHelper.setPrependCountryCodeEnabled(it)
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

        TextField(
            value = prependCountryCode,
            onValueChange = {
                prependCountryCode = it
                settingsHelper.setPrependCountryCode(it)
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
fun EvaluateSettings(settingsHelper: SettingsHelper) {
    var decimalPoints by remember {
        mutableFloatStateOf(settingsHelper.getDecimalPoints().toFloat())
    }

    val options = listOf(
        stringResource(id = R.string.eval_mode_ask_next_time),
        stringResource(id = R.string.eval_mode_result),
        stringResource(id = R.string.eval_mode_append),
        stringResource(id = R.string.eval_mode_copy)
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(options[settingsHelper.getEvaluateResultMode()]) }

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
            text = "${decimalPoints.toInt()}",
            style = TypographyV2.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
        Slider(
            value = decimalPoints,
            onValueChange = {
                decimalPoints = it
                settingsHelper.setDecimalPoints(it.toInt())
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
                value = selectedOptionText,
                onValueChange = { },
                label = { Text(stringResource(id = R.string.eval_result_handling)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor().fillMaxWidth()
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
                            settingsHelper.setEvaluateResultMode(index)
                            selectedOptionText = selectionOption
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    val context = LocalContext.current
    QuickToolsTheme {
        Settings(SettingsHelper(context))
    }
}