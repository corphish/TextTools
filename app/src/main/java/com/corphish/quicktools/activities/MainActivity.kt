package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.window.core.layout.WindowSizeClass
import com.corphish.quicktools.ui.theme.AppTheme
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
            AppTheme {
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
    val enabledFeatures = viewModel.enabledFeatures.collectAsState()
    val appMode = viewModel.appMode.collectAsState()
    var shouldEdit by remember { mutableStateOf(false) }

    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isWideScreen =
        adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                },
            ) {
                Icon(
                    painterResource(R.drawable.ic_settings),
                    stringResource(R.string.title_activity_settings)
                )
            }
        }) { paddingValues ->
        val commonModifier = Modifier
            .padding(
                top = paddingValues.calculateTopPadding().plus(16.dp),
                bottom = paddingValues.calculateBottomPadding().plus(16.dp),
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr).plus(16.dp),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr).plus(16.dp)
            )

        if (isWideScreen) {
            Row(
                modifier = commonModifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    AppHeader()
                    FeaturesSection(
                        appMode = appMode.value,
                        enabledFeatures = enabledFeatures.value,
                        shouldEdit = shouldEdit,
                        onFeatureEnabledOrDisabled = { id, enabled ->
                            viewModel.enableOrDisableFeature(id, enabled)
                        }
                    )
                    Spacer(modifier = Modifier.height(64.dp))
                }

                Column(
                    modifier = Modifier
                        .weight(0.55f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    EditModesSection(
                        shouldEdit = shouldEdit,
                        onEditToggle = { shouldEdit = !shouldEdit }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SimulateSection()
                    Spacer(modifier = Modifier.height(16.dp))
                    OssSection()
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        } else {
            Column(
                modifier = commonModifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AppHeader()
                FeaturesSection(
                    appMode = appMode.value,
                    enabledFeatures = enabledFeatures.value,
                    shouldEdit = shouldEdit,
                    onFeatureEnabledOrDisabled = { id, enabled ->
                        viewModel.enableOrDisableFeature(id, enabled)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                EditModesSection(
                    shouldEdit = shouldEdit,
                    onEditToggle = { shouldEdit = !shouldEdit }
                )
                Spacer(modifier = Modifier.height(16.dp))
                SimulateSection()
                Spacer(modifier = Modifier.height(16.dp))
                OssSection()
                Box(modifier = Modifier.height(64.dp))
            }
        }
    }
}

@Composable
fun AppHeader() {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
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
    }
}

@Composable
fun FeaturesSection(
    appMode: AppMode,
    enabledFeatures: List<FeatureIds>,
    shouldEdit: Boolean,
    onFeatureEnabledOrDisabled: (FeatureIds, Boolean) -> Unit
) {
    val context = LocalContext.current
    Text(
        text = stringResource(id = R.string.features),
        style = TypographyV2.labelSmall,
        modifier = Modifier.padding(bottom = 8.dp),
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
                    appMode = appMode,
                    enabledFeatures = enabledFeatures,
                    shouldEdit = shouldEdit,
                    onFeatureEnabledOrDisabled = onFeatureEnabledOrDisabled,
                    onClick = {
                        if (feature.id == FeatureIds.TEXT_TEMPLATE) {
                            context.startActivity(Intent(context, TextTemplateActivity::class.java))
                        }
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
}

@Composable
fun EditModesSection(
    shouldEdit: Boolean,
    onEditToggle: () -> Unit
) {
    Column {
        Text(
            text = stringResource(id = R.string.edit),
            style = TypographyV2.labelSmall,
            fontFamily = BrandFontFamily,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.edit_modes),
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = onEditToggle,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painterResource(if (shouldEdit) R.drawable.ic_done else R.drawable.ic_edit),
                        contentDescription = stringResource(if (shouldEdit) R.string.done else R.string.edit)
                    )
                    Text(
                        text = stringResource(id = if (shouldEdit) R.string.done else R.string.edit),
                        modifier = Modifier.padding(start = 16.dp),
                        style = TypographyV2.labelMedium,
                        fontWeight = FontWeight.W600
                    )
                }
            }
        }
    }
}

@Composable
fun SimulateSection() {
    val context = LocalContext.current
    Column {
        Text(
            text = stringResource(id = R.string.simulate),
            style = TypographyV2.labelSmall,
            fontFamily = BrandFontFamily,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.simulate_desc),
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = {
                        context.startActivity(
                            Intent(
                                context,
                                SimulationActivity::class.java
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        painterResource(R.drawable.ic_simulate),
                        contentDescription = stringResource(R.string.simulate)
                    )
                    Text(
                        text = stringResource(id = R.string.simulate),
                        modifier = Modifier.padding(start = 16.dp),
                        style = TypographyV2.labelMedium,
                        fontWeight = FontWeight.W600
                    )
                }
            }
        }
    }
}

@Composable
fun OssSection() {
    val uriHandler = LocalUriHandler.current
    Column {
        Text(
            text = stringResource(id = R.string.oss_info),
            style = TypographyV2.labelSmall,
            fontFamily = BrandFontFamily,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(id = R.string.oss_desc),
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { uriHandler.openUri(Constants.SOURCE_LINK) }
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_open_in_new),
                            contentDescription = stringResource(R.string.oss_check)
                        )
                        Text(
                            text = stringResource(id = R.string.oss_check),
                            modifier = Modifier.padding(start = 8.dp),
                            style = TypographyV2.labelMedium,
                            fontWeight = FontWeight.W600,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { uriHandler.openUri(Constants.CONTRIBUTORS_LINK) }
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_open_in_new),
                            contentDescription = stringResource(R.string.contributors)
                        )
                        Text(
                            text = stringResource(id = R.string.contributors),
                            modifier = Modifier.padding(start = 8.dp),
                            style = TypographyV2.labelMedium,
                            fontWeight = FontWeight.W600,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureItem(
    feature: Feature,
    appMode: AppMode,
    shouldEdit: Boolean = false,
    enabledFeatures: List<FeatureIds> = emptyList(),
    onFeatureEnabledOrDisabled: (FeatureIds, Boolean) -> Unit = { _, _ -> },
    onClick: () -> Unit = {}
) {
    ListItem(
        modifier = Modifier.clickable(enabled = !shouldEdit) { onClick() },
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
                        modifier = Modifier.padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.context_menu_option),
                            style = Typography.labelMedium,
                        )
                        Text(
                            text = stringResource(id = feature.contextMenuText),
                            style = Typography.bodyMedium.copy(fontWeight = FontWeight.W500),
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .basicMarquee()
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
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painterResource(id = feature.icon),
                        contentDescription = stringResource(feature.featureTitle),
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