package com.corphish.quicktools.ui.theme

import android.app.Activity
import android.os.Build
import android.view.View
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.corphish.quicktools.viewmodels.ThemeViewModel
import com.tabotabo.ui.color.Hue
import com.tabotabo.ui.color.HueState
import com.tabotabo.ui.color.rememberHueState
import com.tabotabo.ui.theme.SystemBarsAppearance
import com.tabotabo.ui.theme.TabotaboTheme

/**
 * Hue state of the enclosing [QuickToolsTheme], e.g. for a "Shuffle colors" button.
 * Shuffling it recolors the screen and, under [AppTheme], is saved for the session.
 */
val LocalHueState = staticCompositionLocalOf<HueState> {
    error("LocalHueState is only available inside QuickToolsTheme")
}

/**
 * App theme for activities. Reads the appearance and hue for the hosting activity from
 * [ThemeViewModel] and applies them through [QuickToolsTheme], using a single [HueState] whose
 * hue follows the [viewModel]. Hue changes made by the UI (e.g. "Shuffle colors") are reported
 * back to the [viewModel].
 *
 * Requires a Hilt entry point (`@AndroidEntryPoint`) activity; previews should use
 * [QuickToolsTheme] directly.
 *
 * @param viewModel Source of the theme inputs, scoped to the hosting activity.
 * @param content Screen content.
 */
@Composable
fun AppTheme(
    viewModel: ThemeViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val appearance by viewModel.appearance.collectAsState()
    val hue by viewModel.hue.collectAsState()
    // One HueState for the whole lifetime, so light/dark switches keep the same colors
    val hueState = remember { HueState(Hue(hue)) }

    LaunchedEffect(hue) {
        if (hueState.hue.degrees != hue) hueState.hue = Hue(hue)
    }

    LaunchedEffect(hueState) {
        snapshotFlow { hueState.hue.degrees }.collect(viewModel::onHueChanged)
    }

    QuickToolsTheme(
        darkTheme = appearance.isDark ?: isSystemInDarkTheme(),
        hueState = hueState,
        content = content
    )
}

/**
 * Tabotabo-based theme without app state, usable from previews. Provides [hueState] as
 * [LocalHueState].
 *
 * @param darkTheme Whether to use the dark scheme.
 * @param hueState Hue the color scheme is built from.
 * @param content Screen content.
 */
@Composable
fun QuickToolsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    hueState: HueState = rememberHueState(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val activity = LocalActivity.current
    val systemBars = remember(view, activity) {
        if (view.isInEditMode || activity == null) null else systemBarsAppearance(activity, view)
    }

    TabotaboTheme(
        darkTheme = darkTheme,
        hueState = hueState,
        systemBars = systemBars,
    ) {
        CompositionLocalProvider(LocalHueState provides hueState, content = content)
    }
}

/**
 * Matches the system bar icons to the scheme. Below API 35 (no enforced edge-to-edge) the status
 * bar is also filled with the background color.
 *
 * @param activity Activity whose window is styled.
 * @param view Any view in that window.
 */
private fun systemBarsAppearance(activity: Activity, view: View) = SystemBarsAppearance { colors ->
    val window = activity.window
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        @Suppress("DEPRECATION")
        window.statusBarColor = colors.background.toArgb()
    }
    WindowCompat.getInsetsController(window, view).apply {
        isAppearanceLightStatusBars = !colors.isDark
        isAppearanceLightNavigationBars = !colors.isDark
    }
}
