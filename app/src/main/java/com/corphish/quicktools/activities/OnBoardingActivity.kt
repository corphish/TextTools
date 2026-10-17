package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.R
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.common.FocusedLastWord
import com.corphish.quicktools.ui.theme.AppTheme
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.viewmodels.OnBoardingViewModel
import com.tabotabo.ui.button.ActionStack
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.layout.LocalScreenLayoutInfo
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.motion.PopInContent
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.text.HeroTitle
import com.tabotabo.ui.text.ScreenTitle
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import dagger.hilt.android.AndroidEntryPoint

/** Height of the app logo on the welcome page. */
private val LogoHeight = 72.dp

/** Width to height ratio of the cropped app logo (ic_logo). */
private const val LOGO_ASPECT_RATIO = 19f / 15f

/** Size of the back arrow on the mode selection page. */
private val BackArrowSize = 32.dp

/**
 * Space between the edge of an icon action's 48dp touch target and the back arrow, used to
 * line the arrow up with text.
 */
private val BackArrowInset = (48.dp - BackArrowSize) / 2

/** Space reserved for the check on a mode card. */
private val CheckSize = 24.dp

/** Index of the welcome page. */
private const val PAGE_WELCOME = 0

/** Index of the context menu mode selection page. */
private const val PAGE_MODE_SELECTION = 1

@AndroidEntryPoint
class OnBoardingActivity : ComponentActivity() {
    private val viewModel: OnBoardingViewModel by viewModels()

    private fun switchToMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                OnBoarding(
                    onAppModeSelected = {
                        viewModel.setAppMode(it)
                    },
                    onFinish = {
                        viewModel.setOnBoardingDone(true)
                        switchToMain()
                        finish()
                    }
                )
            }
        }
    }
}

/**
 * Two-page on-boarding: a welcome page, then the context menu mode selection.
 *
 * @param onAppModeSelected Called when the user picks a mode.
 * @param onFinish Called when the user completes on-boarding.
 */
@Composable
fun OnBoarding(
    onAppModeSelected: (AppMode) -> Unit = {},
    onFinish: () -> Unit = {},
) {
    var page by rememberSaveable { mutableIntStateOf(PAGE_WELCOME) }

    BackHandler(enabled = page == PAGE_MODE_SELECTION) { page = PAGE_WELCOME }

    PopInContent(targetState = page, modifier = Modifier.fillMaxSize()) { currentPage ->
        when (currentPage) {
            PAGE_WELCOME -> InitialPage { page = PAGE_MODE_SELECTION }
            else -> ModeSelectionScreen(
                onBack = { page = PAGE_WELCOME },
                onModeSelected = onAppModeSelected,
                onFinish = onFinish
            )
        }
    }
}

/**
 * Welcome page.
 * - Portrait: logo, app name, short description and the Next button stacked at the bottom,
 *   left aligned.
 * - Landscape: logo centred on the left; name, description and Next button on the right.
 *
 * @param onNextPressed Called when the user continues.
 */
@Composable
fun InitialPage(
    onNextPressed: () -> Unit = {}
) {
    ScreenScaffold(
        header = {
            if (LocalScreenLayoutInfo.current.isLandscape) {
                WelcomeText()
            }
        },
        actions = {
            ActionStack {
                PrimaryButton(
                    text = stringResource(R.string.next),
                    onClick = onNextPressed,
                    trailingIcon = { ButtonIcon(R.drawable.ic_arrow_right) },
                )
            }
        }
    ) {
        if (LocalScreenLayoutInfo.current.isLandscape) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AppLogo()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = TabotaboTheme.dimensions.spacing.xl),
                verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.xl, Alignment.Bottom),
                horizontalAlignment = Alignment.Start,
            ) {
                AppLogo()
                WelcomeText()
            }
        }
    }
}

/**
 * App name as a hero title above the short app description.
 */
@Composable
private fun WelcomeText() {
    val appName = stringResource(R.string.app_name)

    Column(verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s)) {
        HeroTitle { FocusedLastWord(appName) }
        Subtitle(text = stringResource(R.string.app_desc_short), maxLines = Int.MAX_VALUE)
    }
}

/**
 * App logo in the primary color, cropped so it aligns with neighbouring content.
 */
@Composable
private fun AppLogo() {
    Icon(
        painter = painterResource(R.drawable.ic_logo),
        contentDescription = null,
        tint = TabotaboTheme.colors.primary,
        modifier = Modifier.size(width = LogoHeight * LOGO_ASPECT_RATIO, height = LogoHeight)
    )
}

/**
 * Context menu mode selection: back arrow, title, description and one card per mode (the
 * selected one glowing with a check), with a Done button.
 * - Portrait: everything stacked at the bottom, left aligned, scrolling when it doesn't fit.
 * - Landscape: the mode cards in a scrollable left column; back arrow, title, description and
 *   the button on the right.
 *
 * @param onBack Called when the user goes back to the welcome page.
 * @param onModeSelected Called when the user picks a mode.
 * @param onFinish Called when the user finishes.
 */
@Composable
fun ModeSelectionScreen(
    onBack: () -> Unit = {},
    onModeSelected: (AppMode) -> Unit = {},
    onFinish: () -> Unit = {},
) {
    var selectedMode by rememberSaveable { mutableStateOf(AppMode.SINGLE) }
    val spacing = TabotaboTheme.dimensions.spacing

    ScreenScaffold(
        header = {
            if (LocalScreenLayoutInfo.current.isLandscape) {
                ModeSelectionIntro(onBack)
            }
        },
        actions = {
            ActionStack {
                PrimaryButton(
                    text = stringResource(R.string.done),
                    onClick = onFinish,
                    leadingIcon = { ButtonIcon(R.drawable.ic_done) },
                )
            }
        }
    ) {
        val isLandscape = LocalScreenLayoutInfo.current.isLandscape

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    // At least the visible height, so short content can still sit at the bottom
                    // (portrait) or in the middle (landscape)
                    .heightIn(min = maxHeight)
                    .padding(vertical = spacing.l)
                    .selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(
                    spacing.m,
                    if (isLandscape) Alignment.CenterVertically else Alignment.Bottom
                ),
            ) {
                if (!isLandscape) {
                    ModeSelectionIntro(onBack, modifier = Modifier.padding(bottom = spacing.s))
                }

                AppMode.entries.forEach { mode ->
                    ModeCard(
                        mode = mode,
                        isSelected = selectedMode == mode,
                        onClick = {
                            selectedMode = mode
                            onModeSelected(mode)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Large back arrow above the mode selection title and its description, all left aligned. The
 * arrow sits on its own line so the title gets the full width.
 *
 * @param onBack Called when the back arrow is tapped.
 * @param modifier Modifier for the column.
 */
@Composable
private fun ModeSelectionIntro(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val title = stringResource(R.string.mode_select_title)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(TabotaboTheme.dimensions.spacing.s),
    ) {
        IconAction(
            onClick = onBack,
            contentDescription = stringResource(R.string.back),
            // Pull the touch target left so the arrow itself lines up with the text
            modifier = Modifier.offset(x = -BackArrowInset)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left),
                contentDescription = null,
                modifier = Modifier.size(BackArrowSize)
            )
        }
        ScreenTitle { FocusedLastWord(title) }
        Subtitle(text = stringResource(R.string.mode_select_desc), maxLines = Int.MAX_VALUE)
    }
}

/**
 * Selectable card describing one context menu mode; glows and shows a check while selected.
 *
 * @param mode Mode the card describes.
 * @param isSelected Whether this mode is selected.
 * @param onClick Called when the card is tapped.
 */
@Composable
private fun ModeCard(
    mode: AppMode,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = TabotaboTheme.colors
    val (title, desc) = when (mode) {
        AppMode.SINGLE -> R.string.mode_single_title to R.string.mode_single_desc
        AppMode.MULTI -> R.string.mode_multi_title to R.string.mode_multi_desc
    }

    GlowCard(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick),
        highlighted = isSelected,
    ) {
        Row(verticalAlignment = Alignment.Top) {
            // Always reserve the check's space so both cards' text lines up
            Box(modifier = Modifier.size(CheckSize), contentAlignment = Alignment.Center) {
                if (isSelected) {
                    ButtonIcon(R.drawable.ic_done, tint = colors.primary)
                }
            }

            Column(modifier = Modifier.padding(start = TabotaboTheme.dimensions.spacing.m)) {
                Text(
                    text = stringResource(title),
                    style = TabotaboTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) colors.primary else colors.accent,
                )
                Subtitle(text = stringResource(desc), maxLines = Int.MAX_VALUE)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnBoardingPreview() {
    QuickToolsTheme {
        OnBoarding()
    }
}
