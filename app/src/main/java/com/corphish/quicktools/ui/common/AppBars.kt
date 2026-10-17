package com.corphish.quicktools.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.corphish.quicktools.R
import com.tabotabo.ui.layout.ScreenHeader

/**
 * Screen header for screens laid out with `ScreenScaffold`, which already applies the window
 * insets: a back arrow, a title whose last word is emphasised, and optional trailing actions.
 *
 * @param id String resource of the title.
 * @param onBack Called when the back arrow is tapped; no arrow is shown when null.
 * @param modifier Modifier for the header.
 * @param subtitle Optional muted line under the title.
 * @param actions Trailing icon actions.
 */
@Composable
fun AppScreenHeader(
    @StringRes id: Int,
    onBack: (() -> Unit)?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val title = stringResource(id = id)

    ScreenHeader(
        modifier = modifier,
        onBack = onBack,
        backContentDescription = stringResource(R.string.back),
        subtitle = subtitle,
        actions = actions,
    ) {
        FocusedLastWord(title)
    }
}

