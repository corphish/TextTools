package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.corphish.quicktools.R
import com.corphish.quicktools.data.WalkthroughTargets
import com.corphish.quicktools.data.Walkthroughs
import com.corphish.quicktools.features.Feature
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.repository.ContextMenuOptionsRepositoryImpl
import com.corphish.quicktools.repository.FeatureIds
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.common.FocusedLastWord
import com.corphish.quicktools.ui.common.walkthrough.WalkthroughHost
import com.corphish.quicktools.ui.common.walkthrough.walkthroughTarget
import com.corphish.quicktools.ui.theme.AppTheme
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.viewmodels.MainViewModel
import com.tabotabo.ui.button.ActionStack
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.surface.TabotaboDialog
import com.tabotabo.ui.surface.Tag
import com.tabotabo.ui.text.ScreenTitle
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.caption
import dagger.hilt.android.AndroidEntryPoint

/** Minimum width of a feature tile; the grid fits as many columns as this allows. */
private val TileMinWidth = 140.dp

/** Size of a feature's icon on its tile. */
private val FeatureIconSize = 28.dp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                WalkthroughHost(walkthroughs = listOf(Walkthroughs.MAIN)) {
                    Greeting(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.init()
    }
}

/**
 * Main screen: screen title with edit and settings actions, a grid of feature tiles and a
 * Simulate button. Tapping a tile shows its details; in edit mode it turns the feature on or
 * off instead, and enabled features glow. The first tile, the actions and the Simulate button
 * are targets of [Walkthroughs.MAIN].
 *
 * @param viewModel Source of enabled features and app mode.
 */
@Composable
fun Greeting(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val enabledFeatures by viewModel.enabledFeatures.collectAsState()
    val appMode by viewModel.appMode.collectAsState()
    var shouldEdit by rememberSaveable { mutableStateOf(false) }
    var detailsFeatureId by rememberSaveable { mutableStateOf<FeatureIds?>(null) }
    val spacing = TabotaboTheme.dimensions.spacing

    ScreenScaffold(
        header = {
            MainHeader(
                shouldEdit = shouldEdit,
                onEditToggle = { shouldEdit = !shouldEdit },
                onSettings = { context.startActivity(Intent(context, SettingsActivity::class.java)) },
            )
        },
        actions = {
            ActionStack {
                PrimaryButton(
                    text = stringResource(R.string.simulate),
                    onClick = { context.startActivity(Intent(context, SimulationActivity::class.java)) },
                    modifier = Modifier.walkthroughTarget(WalkthroughTargets.MAIN_SIMULATE),
                    leadingIcon = { ButtonIcon(R.drawable.ic_simulate) },
                )
            }
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = TileMinWidth),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(spacing.m),
            verticalArrangement = Arrangement.spacedBy(spacing.m),
        ) {
            items(Feature.LIST, key = { it.id }, contentType = { "feature_tile" }) { feature ->
                FeatureTile(
                    feature = feature,
                    shouldEdit = shouldEdit,
                    enabled = enabledFeatures.contains(feature.id),
                    onFeatureEnabledOrDisabled = viewModel::enableOrDisableFeature,
                    onClick = { detailsFeatureId = feature.id },
                    modifier = if (feature == Feature.LIST.first()) {
                        Modifier.walkthroughTarget(WalkthroughTargets.MAIN_FEATURE_TILE)
                    } else {
                        Modifier
                    },
                )
            }
        }
    }

    Feature.LIST.firstOrNull { it.id == detailsFeatureId }?.let { feature ->
        FeatureDetailsDialog(
            feature = feature,
            appMode = appMode,
            onDismiss = { detailsFeatureId = null },
            onManageTemplates = {
                detailsFeatureId = null
                context.startActivity(Intent(context, TextTemplateActivity::class.java))
            },
        )
    }
}

/**
 * Screen title with edit and settings actions, above a short description (or, in edit mode, an
 * explanation of editing).
 *
 * @param shouldEdit Whether the screen is in edit mode.
 * @param onEditToggle Turns edit mode on or off.
 * @param onSettings Opens the settings.
 */
@Composable
private fun MainHeader(
    shouldEdit: Boolean,
    onEditToggle: () -> Unit,
    onSettings: () -> Unit,
) {
    val appName = stringResource(R.string.app_name)

    Column(verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ScreenTitle(modifier = Modifier.weight(1f)) { FocusedLastWord(appName) }
            IconAction(
                onClick = onEditToggle,
                contentDescription = stringResource(if (shouldEdit) R.string.done else R.string.edit),
                modifier = Modifier.walkthroughTarget(WalkthroughTargets.MAIN_EDIT),
            ) {
                ButtonIcon(if (shouldEdit) R.drawable.ic_done else R.drawable.ic_edit)
            }
            IconAction(
                onClick = onSettings,
                contentDescription = stringResource(R.string.title_activity_settings),
                modifier = Modifier.walkthroughTarget(WalkthroughTargets.MAIN_SETTINGS),
            ) {
                ButtonIcon(R.drawable.ic_settings)
            }
        }
        Subtitle(
            text = stringResource(if (shouldEdit) R.string.edit_modes else R.string.app_desc_short),
            maxLines = Int.MAX_VALUE,
        )
    }
}

/**
 * Compact tile of one feature: its icon and name. In edit mode the tile toggles the feature
 * and glows while it is enabled; otherwise tapping it calls [onClick].
 *
 * @param feature Feature to show.
 * @param shouldEdit Whether the screen is in edit mode.
 * @param enabled Whether the feature is enabled.
 * @param onFeatureEnabledOrDisabled Called with the feature and its new state in edit mode.
 * @param onClick Called when the tile is tapped outside edit mode.
 * @param modifier Modifier for the tile, e.g. to make it a walkthrough target.
 */
@Composable
fun FeatureTile(
    feature: Feature,
    shouldEdit: Boolean,
    enabled: Boolean,
    onFeatureEnabledOrDisabled: (FeatureIds, Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = TabotaboTheme.colors
    val dimmed = shouldEdit && !enabled

    GlowCard(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (shouldEdit) {
                    Modifier.toggleable(value = enabled, role = Role.Checkbox) {
                        onFeatureEnabledOrDisabled(feature.id, it)
                    }
                } else {
                    Modifier
                }
            ),
        highlighted = shouldEdit && enabled,
        onClick = if (shouldEdit) null else onClick,
    ) {
        Icon(
            painterResource(id = feature.icon),
            contentDescription = null,
            modifier = Modifier.size(FeatureIconSize),
            tint = if (dimmed) colors.muted else colors.primary
        )
        Spacer(modifier = Modifier.height(TabotaboTheme.dimensions.spacing.m))
        Text(
            text = stringResource(id = feature.featureTitle),
            style = TabotaboTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (dimmed) colors.muted else colors.accent,
            // Reserve two lines so tiles in a row keep the same height
            minLines = 2,
            maxLines = 3,
        )
    }
}

/**
 * Details of a feature: its description and, in multiple-options mode, its context menu
 * label. The text template feature also offers to manage templates.
 *
 * @param feature Feature to describe.
 * @param appMode Current context menu mode.
 * @param onDismiss Called when the dialog is closed.
 * @param onManageTemplates Opens the template manager.
 */
@Composable
private fun FeatureDetailsDialog(
    feature: Feature,
    appMode: AppMode,
    onDismiss: () -> Unit,
    onManageTemplates: () -> Unit,
) {
    val title = stringResource(feature.featureTitle)
    val isTemplate = feature.id == FeatureIds.TEXT_TEMPLATE
    val colors = TabotaboTheme.colors

    TabotaboDialog(
        onDismissRequest = onDismiss,
        confirmText = stringResource(if (isTemplate) R.string.manage_templates else R.string.done),
        onConfirm = if (isTemplate) onManageTemplates else onDismiss,
        dismissText = if (isTemplate) stringResource(R.string.done) else null,
        title = { FocusedLastWord(title) },
    ) {
        Subtitle(text = stringResource(feature.featureDesc), maxLines = Int.MAX_VALUE)

        // Don't show context menu option in single option flavor
        if (appMode == AppMode.MULTI) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = TabotaboTheme.dimensions.spacing.m),
                horizontalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.context_menu_option).trim(),
                    style = TabotaboTheme.typography.caption,
                    color = colors.muted,
                )
                Tag(text = stringResource(id = feature.contextMenuText), color = colors.primary)
            }
        }
    }
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
