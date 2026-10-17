package com.corphish.quicktools.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tabotabo.ui.theme.TabotaboTheme

/**
 * Landscape layout for screens built around a large text area: one compact row with the
 * [header] and its [headerAction] (usually the screen's primary button), then the [content]
 * taking all remaining space, with an optional [bottomBar] below it. Applies the background and
 * safe-drawing insets itself, like `ScreenScaffold`.
 *
 * @param header Screen header.
 * @param headerAction Content at the end of the header row.
 * @param bottomBar Optional content under the main content, e.g. a toolbar.
 * @param content Main content; give the element that should grow `Modifier.weight(1f)`.
 */
@Composable
fun CompactLandscapeLayout(
    header: @Composable () -> Unit,
    headerAction: @Composable () -> Unit,
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = TabotaboTheme.dimensions.spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TabotaboTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = spacing.xl, vertical = spacing.m),
        verticalArrangement = Arrangement.spacedBy(spacing.m),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) { header() }
            Spacer(modifier = Modifier.width(spacing.l))
            headerAction()
        }

        content()

        bottomBar?.invoke()
    }
}
