package com.corphish.quicktools.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tabotabo.ui.button.ActionStack
import com.tabotabo.ui.layout.LocalScreenLayoutInfo
import com.tabotabo.ui.layout.ScreenLayoutInfo
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.layout.SizeClass
import com.tabotabo.ui.theme.TabotaboTheme

/*
 * The values below mirror Tabotabo's internal ScreenScaffold landscape layout. Keep them in
 * sync when updating the library.
 */

/** Share of the width the landscape header/actions pane takes. */
private const val LANDSCAPE_HEADER_PANE_WEIGHT = 0.45f

/** Shorter side from which a screen counts as [SizeClass.Expanded]. */
private val ExpandedThreshold = 600.dp

/**
 * [SizeClass] of a space whose shorter side is [shorterSide], as `ScreenScaffold` reports it.
 *
 * @return [SizeClass.Expanded] from [ExpandedThreshold] up, else [SizeClass.Compact].
 */
internal fun sizeClassFor(shorterSide: Dp): SizeClass =
    if (shorterSide >= ExpandedThreshold) SizeClass.Expanded else SizeClass.Compact

/**
 * The app's screen scaffold: Tabotabo's `ScreenScaffold`, plus control over where the header
 * and [actions] sit in the landscape right-hand pane.
 *
 * With [landscapePaneArrangement] at its default ([Arrangement.Center]) this is exactly
 * `ScreenScaffold`. Any other arrangement (e.g. [Arrangement.Bottom]) keeps `ScreenScaffold`
 * for portrait and lays out landscape the same way it does (pane split, margins, gap, insets,
 * scrolling on overflow and [LocalScreenLayoutInfo]), but arranges the right pane as given.
 * The web layout is not supported, as the app only runs on Android.
 *
 * @param header Screen header.
 * @param modifier Modifier for the scaffold.
 * @param actions Buttons stacked under the content (portrait) or the header (landscape).
 * @param landscapePaneArrangement Vertical arrangement of the header and actions in landscape.
 * @param content Main content.
 */
@Composable
fun AppScreenScaffold(
    header: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    actions: (@Composable ColumnScope.() -> Unit)? = null,
    landscapePaneArrangement: Arrangement.Vertical = Arrangement.Center,
    content: @Composable BoxScope.() -> Unit,
) {
    if (landscapePaneArrangement == Arrangement.Center) {
        ScreenScaffold(header = header, modifier = modifier, actions = actions, content = content)
        return
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(TabotaboTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {
        if (maxWidth > maxHeight) {
            LandscapePanes(header, actions, landscapePaneArrangement, shorterSide = maxHeight, content)
        } else {
            // The insets are consumed above, so ScreenScaffold does not apply them twice
            ScreenScaffold(header = header, actions = actions, content = content)
        }
    }
}

/**
 * Landscape: [content] on the left, [header] and [actions] on the right, arranged by
 * [arrangement] over at least the pane's height and scrollable when they do not fit.
 *
 * @param shorterSide Shorter side of the space inside the insets, which sizes margins and gap.
 */
@Composable
private fun LandscapePanes(
    header: @Composable () -> Unit,
    actions: (@Composable ColumnScope.() -> Unit)?,
    arrangement: Arrangement.Vertical,
    shorterSide: Dp,
    content: @Composable BoxScope.() -> Unit,
) {
    val dimensions = TabotaboTheme.dimensions
    val margin = shorterSide * dimensions.landscapeMarginFraction
    val layoutInfo = ScreenLayoutInfo(
        isLandscape = true,
        sizeClass = sizeClassFor(shorterSide),
        horizontalMargin = margin,
    )

    CompositionLocalProvider(LocalScreenLayoutInfo provides layoutInfo) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = margin, vertical = dimensions.spacing.l),
            horizontalArrangement = Arrangement.spacedBy(shorterSide * dimensions.portraitMarginFraction),
        ) {
            Box(
                modifier = Modifier
                    .weight(1f - LANDSCAPE_HEADER_PANE_WEIGHT)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center,
                content = content,
            )
            BoxWithConstraints(
                modifier = Modifier
                    .weight(LANDSCAPE_HEADER_PANE_WEIGHT)
                    .fillMaxHeight()
            ) {
                val paneHeight = maxHeight
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // At least the pane's height, so the arrangement has room to act
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = paneHeight),
                        verticalArrangement = arrangement,
                    ) {
                        header()
                        if (actions != null) {
                            Spacer(modifier = Modifier.height(dimensions.spacing.xl))
                            ActionStack(content = actions)
                        }
                    }
                }
            }
        }
    }
}
