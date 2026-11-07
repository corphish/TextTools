package com.corphish.quicktools.ui.common.walkthrough

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Walkthrough
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.common.FocusedLastWord
import com.corphish.quicktools.viewmodels.WalkthroughUiState
import com.corphish.quicktools.viewmodels.WalkthroughViewModel
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.text.EmphasisText
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.EmphasisWeights
import com.tabotabo.ui.typography.caption
import kotlinx.coroutines.flow.distinctUntilChanged

/** Widest the walkthrough card gets, so it stays readable on tablets and in landscape. */
private val CardMaxWidth = 400.dp

/** Space between a target's edges and the spotlight around it. */
private val SpotlightPadding = 6.dp

/** Opacity of the scrim that dims everything but the spotlight. */
private const val SCRIM_ALPHA = 0.88f

/** Starts walkthroughs of the enclosing [WalkthroughHost] on demand. */
fun interface WalkthroughController {
    /**
     * Shows the walkthrough with [key] once its targets are on screen, even if it was
     * completed before.
     *
     * @return false when the host has no walkthrough with [key].
     */
    fun start(key: String): Boolean
}

/** Controller of the enclosing [WalkthroughHost]; outside one, starting does nothing. */
val LocalWalkthroughController = staticCompositionLocalOf { WalkthroughController { false } }

/**
 * Hosts the [walkthroughs] of a screen over its [content]. Composables inside [content] are
 * tagged with `Modifier.walkthroughTarget`. Due walkthroughs then start by themselves (see
 * [WalkthroughViewModel]), and [LocalWalkthroughController] starts one on demand.
 *
 * While a walkthrough is shown, the screen is dimmed except for a spotlight on the current
 * step's target, and a card explains the step. Touches on the screen and its accessibility
 * content are blocked, and back skips the walkthrough. Use one host per screen.
 *
 * @param walkthroughs Walkthroughs of the screen, in the order they should be shown.
 * @param modifier Modifier for the host.
 * @param viewModel Runs the walkthroughs; scoped to the activity by default.
 * @param content Screen content.
 */
@Composable
fun WalkthroughHost(
    walkthroughs: List<Walkthrough>,
    modifier: Modifier = Modifier,
    viewModel: WalkthroughViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {
    val registry = remember { WalkthroughTargetRegistry() }
    val state by viewModel.state.collectAsState()
    val controller = remember(viewModel) { WalkthroughController(viewModel::start) }
    var hostOffset by remember { mutableStateOf(Offset.Zero) }
    // Keeps the last step on screen while the overlay fades out
    val shownState = remember { LatestValue<WalkthroughUiState?>(null) }
    shownState.value = state ?: shownState.value

    LaunchedEffect(viewModel, walkthroughs) {
        viewModel.setWalkthroughs(walkthroughs)
    }
    LaunchedEffect(viewModel, registry) {
        snapshotFlow { registry.bounds.keys.toSet() }
            .distinctUntilChanged()
            .collect(viewModel::onTargetsChanged)
    }

    CompositionLocalProvider(
        LocalWalkthroughRegistry provides registry,
        LocalWalkthroughController provides controller,
    ) {
        Box(modifier.onGloballyPositioned { hostOffset = it.positionInWindow() }) {
            Box(if (state != null) Modifier.clearAndSetSemantics {} else Modifier) {
                content()
            }

            AnimatedVisibility(
                visible = state != null,
                enter = fadeIn(tween(TabotaboTheme.motion.crossfadeMillis)),
                exit = fadeOut(tween(TabotaboTheme.motion.crossfadeMillis)),
            ) {
                shownState.value?.let { shown ->
                    WalkthroughOverlay(
                        state = shown,
                        target = registry.bounds[shown.step.targetId]?.translate(-hostOffset),
                        onNext = viewModel::next,
                        onBack = viewModel::back,
                        onSkip = viewModel::skip,
                    )
                }
            }
        }
    }
}

/** Plain holder of the last value seen, read and written without triggering recomposition. */
private class LatestValue<T>(var value: T)

/**
 * Scrim with a spotlight on [target], plus the card of the current step placed next to it.
 *
 * @param state Step to show.
 * @param target Target bounds relative to the host, or null when it is not on screen.
 * @param onNext Moves to the next step or finishes.
 * @param onBack Moves to the previous step.
 * @param onSkip Ends the walkthrough.
 */
@Composable
private fun WalkthroughOverlay(
    state: WalkthroughUiState,
    target: Rect?,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
) {
    val colors = TabotaboTheme.colors
    val dimensions = TabotaboTheme.dimensions
    val motion = TabotaboTheme.motion
    val spotlight = remember { Animatable(Rect.Zero, Rect.VectorConverter) }
    var hasSpotlight by remember { mutableStateOf(false) }
    val insets = WindowInsets.safeDrawing

    BackHandler(onBack = onSkip)

    Box(Modifier.fillMaxSize()) {
        Canvas(
            Modifier
                .fillMaxSize()
                // Offscreen so the cleared spotlight shows the screen instead of black
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
        ) {
            drawRect(colors.background.copy(alpha = SCRIM_ALPHA))
            if (hasSpotlight) {
                val bounds = spotlight.value
                val corner = CornerRadius(dimensions.cardCorner.toPx())
                drawRoundRect(Color.Black, bounds.topLeft, bounds.size, corner, blendMode = BlendMode.Clear)
                drawRoundRect(
                    color = colors.primary,
                    topLeft = bounds.topLeft,
                    size = bounds.size,
                    cornerRadius = corner,
                    style = Stroke(dimensions.highlightOutlineWidth.toPx()),
                )
            }
        }

        Layout(
            content = { WalkthroughCard(state, onNext, onBack, onSkip) },
            modifier = Modifier.fillMaxSize(),
        ) { measurables, constraints ->
            val margin = dimensions.spacing.l.roundToPx()
            val area = IntRect(
                left = insets.getLeft(this, layoutDirection) + margin,
                top = insets.getTop(this) + margin,
                right = constraints.maxWidth - insets.getRight(this, layoutDirection) - margin,
                bottom = constraints.maxHeight - insets.getBottom(this) - margin,
            )
            val width = minOf(area.width, CardMaxWidth.roundToPx()).coerceAtLeast(0)
            val card = measurables.first().measure(
                Constraints(minWidth = width, maxWidth = width, maxHeight = area.height.coerceAtLeast(0))
            )
            val spotlightBounds = target?.inflate(SpotlightPadding.toPx())
            val position = walkthroughCardPosition(
                spotlight = spotlightBounds,
                cardSize = IntSize(card.width, card.height),
                area = area,
                gap = dimensions.spacing.m.roundToPx(),
            )
            layout(constraints.maxWidth, constraints.maxHeight) { card.place(position) }
        }
    }

    // Glide the spotlight between steps; jump on the first step or with reduced motion
    val density = LocalDensity.current
    LaunchedEffect(target) {
        val bounds = target?.inflate(with(density) { SpotlightPadding.toPx() })
        if (bounds == null) {
            hasSpotlight = false
        } else {
            if (hasSpotlight && motion.enabled) {
                spotlight.animateTo(bounds, tween(motion.highlightMillis))
            } else {
                spotlight.snapTo(bounds)
            }
            hasSpotlight = true
        }
    }
}

/**
 * Card of one step: position, title, explanation, and the back, skip and next actions.
 *
 * @param state Step to show.
 * @param onNext Moves to the next step or finishes.
 * @param onBack Moves to the previous step.
 * @param onSkip Ends the walkthrough.
 */
@Composable
private fun WalkthroughCard(
    state: WalkthroughUiState,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onSkip: () -> Unit,
) {
    val colors = TabotaboTheme.colors
    val spacing = TabotaboTheme.dimensions.spacing
    val title = stringResource(state.step.title)

    GlowCard(
        modifier = Modifier
            .semantics { paneTitle = title }
            .animateContentSize(tween(TabotaboTheme.motion.crossfadeMillis)),
    ) {
        Crossfade(
            targetState = state,
            animationSpec = tween(TabotaboTheme.motion.crossfadeMillis),
            label = "walkthroughStep",
        ) { shown ->
            val shownTitle = stringResource(shown.step.title)
            Column(verticalArrangement = Arrangement.spacedBy(spacing.s)) {
                Text(
                    text = stringResource(R.string.walkthrough_step_count, shown.stepIndex + 1, shown.walkthrough.steps.size),
                    style = TabotaboTheme.typography.caption,
                    color = colors.muted,
                )
                EmphasisText(
                    style = TabotaboTheme.typography.headlineSmall,
                    weights = EmphasisWeights.Title,
                ) {
                    FocusedLastWord(shownTitle)
                }
                Subtitle(text = stringResource(shown.step.body), maxLines = Int.MAX_VALUE)
            }
        }

        Spacer(Modifier.height(spacing.l))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.s),
        ) {
            if (state.hasPrevious) {
                IconAction(onClick = onBack, contentDescription = stringResource(R.string.back)) {
                    ButtonIcon(R.drawable.ic_arrow_left)
                }
            }
            Spacer(Modifier.weight(1f))
            if (state.hasNext) {
                SecondaryButton(text = stringResource(R.string.walkthrough_skip), onClick = onSkip)
            }
            PrimaryButton(
                text = stringResource(if (state.hasNext) R.string.next else R.string.done),
                onClick = onNext,
            )
        }
    }
}
