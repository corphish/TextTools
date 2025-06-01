package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.features.Feature
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.repository.ContextMenuOptionsRepositoryImpl
import com.corphish.quicktools.repository.FeatureIds
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.Typography
import com.corphish.quicktools.ui.theme.TypographyV2
import com.corphish.quicktools.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickToolsTheme {
                Greeting(
                    viewModel = viewModel
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.init()
    }
}

@Composable
fun Greeting(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val mainScrollState = rememberScrollState()
    val contributorScrollState = rememberScrollState()
    val enabledFeatures = viewModel.enabledFeatures.collectAsState()
    val appMode = viewModel.appMode.collectAsState()
    var shouldEdit by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                },
            ) {
                Icon(Icons.Filled.Settings, "Settings")
            }
        }) {
        Column(
            modifier = Modifier
                .padding(
                    top = it
                        .calculateTopPadding()
                        .plus(16.dp),
                    bottom = it
                        .calculateBottomPadding()
                        .plus(16.dp),
                    start = it
                        .calculateStartPadding(LayoutDirection.Ltr)
                        .plus(16.dp),
                    end = it
                        .calculateEndPadding(LayoutDirection.Ltr)
                        .plus(16.dp)
                )
                .verticalScroll(mainScrollState)
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = TypographyV2.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = BrandFontFamily,
                modifier = Modifier.padding(bottom = 8.dp)
            )


            Text(
                text = stringResource(id = R.string.app_desc), style = Typography.bodyMedium
            )

            Text(
                text = stringResource(id = R.string.features),
                style = TypographyV2.labelSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                fontFamily = BrandFontFamily,
                color = MaterialTheme.colorScheme.primary
            )

            Surface(
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column {
                    for ((index, feature) in Feature.LIST.withIndex()) {
                        FeatureItem(
                            feature = feature,
                            appMode = appMode.value,
                            enabledFeatures = enabledFeatures.value,
                            shouldEdit = shouldEdit,
                            onFeatureEnabledOrDisabled = { featureId, enabled ->
                                viewModel.enableOrDisableFeature(featureId, enabled)
                            }
                        )

                        if (index < Feature.LIST.size - 1) {
                            HorizontalDivider(
                                thickness = 2.dp,
                                color = MaterialTheme.colorScheme.background
                            )
                        }
                    }
                }
            }

            Text(
                text = stringResource(id = R.string.edit_modes),
                style = Typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = { shouldEdit = !shouldEdit },
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    if (shouldEdit) Icons.Default.Done else Icons.Default.Edit,
                    contentDescription = ""
                )
                Text(
                    text = stringResource(id = if (shouldEdit) R.string.done else R.string.edit),
                    modifier = Modifier.padding(start = 16.dp),
                    style = TypographyV2.labelMedium,
                    fontWeight = FontWeight.W600
                )
            }

            Text(
                text = stringResource(id = R.string.oss_info),
                style = TypographyV2.labelSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                fontFamily = BrandFontFamily,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(id = R.string.oss_desc), style = Typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .horizontalScroll(contributorScrollState)
            ) {
                Button(
                    onClick = { uriHandler.openUri(Constants.SOURCE_LINK) },
                ) {
                    Icon(painterResource(id = R.drawable.ic_open_in_new), contentDescription = "")
                    Text(
                        text = stringResource(id = R.string.oss_check),
                        modifier = Modifier.padding(start = 16.dp),
                        style = TypographyV2.labelMedium,
                        fontWeight = FontWeight.W600
                    )
                }

                Button(
                    onClick = { uriHandler.openUri(Constants.CONTRIBUTORS_LINK) },
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Icon(painterResource(id = R.drawable.ic_open_in_new), contentDescription = "")
                    Text(
                        text = stringResource(id = R.string.contributors),
                        modifier = Modifier.padding(start = 16.dp),
                        style = TypographyV2.labelMedium,
                        fontWeight = FontWeight.W600
                    )
                }
            }

            // Placeholder so that FAB does not overlap the contents
            Box(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun FeatureItem(
    feature: Feature,
    appMode: AppMode,
    shouldEdit: Boolean = false,
    enabledFeatures: List<FeatureIds> = emptyList(),
    onFeatureEnabledOrDisabled: (FeatureIds, Boolean) -> Unit = { _, _ -> }
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(id = feature.featureTitle),
                style = TypographyV2.labelMedium,
                fontFamily = BrandFontFamily,
                fontWeight = FontWeight.W600
            )
        },
        supportingContent = {
            Column {
                Text(text = stringResource(id = feature.featureDesc), style = Typography.bodyMedium)

                // Don't show context menu option in single option flavor
                if (appMode == AppMode.MULTI) {
                    Row(
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.context_menu_option),
                            style = Typography.labelMedium,
                        )
                        Text(
                            text = stringResource(id = feature.contextMenuText),
                            style = Typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

        },
        leadingContent = {
            if (shouldEdit) {
                Checkbox(
                    checked = enabledFeatures.contains(feature.id),
                    onCheckedChange = {
                        onFeatureEnabledOrDisabled(feature.id, it)
                    },
                    modifier = Modifier.padding(end = 4.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center
                ) {
                    Image(
                        painterResource(id = feature.icon),
                        contentDescription = "",
                        modifier = Modifier.size(32.dp),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimary)
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuickToolsTheme {
        val context = LocalContext.current
        val viewModel = viewModel { MainViewModel(ContextMenuOptionsRepositoryImpl(context)) }
        Greeting(viewModel)
    }
}