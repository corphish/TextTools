package com.corphish.quicktools.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.corphish.quicktools.R
import com.tabotabo.ui.layout.ScreenHeader
import com.tabotabo.ui.theme.TabotaboTheme

/**
 * Screen header with a back arrow and a title whose last word is emphasised.
 *
 * Meant for a Material3 `Scaffold`'s `topBar` slot, so it applies the top and horizontal
 * safe-drawing insets itself. Screens on `ScreenScaffold` should use `ScreenHeader` directly.
 *
 * @param id String resource of the title.
 * @param modifier Modifier for the header.
 * @param onNavigationClick Called when the back arrow is tapped.
 */
@Composable
fun CustomTopAppBar(
    @StringRes id: Int,
    modifier: Modifier = Modifier,
    onNavigationClick: () -> Unit = {},
) {
    val title = stringResource(id = id)
    val spacing = TabotaboTheme.dimensions.spacing

    ScreenHeader(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
            .padding(horizontal = spacing.s, vertical = spacing.m),
        onBack = onNavigationClick,
        backContentDescription = stringResource(R.string.back),
    ) {
        FocusedLastWord(title)
    }
}
