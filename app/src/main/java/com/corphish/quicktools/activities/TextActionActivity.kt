package com.corphish.quicktools.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.data.WalkthroughTargets
import com.corphish.quicktools.data.Walkthroughs
import com.corphish.quicktools.functions.IntentAction
import com.corphish.quicktools.functions.ParsedTextAction
import com.corphish.quicktools.ui.common.AppScreenHeader
import com.corphish.quicktools.ui.common.AppScreenScaffold
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.common.SectionLabel
import com.corphish.quicktools.ui.common.walkthrough.WalkthroughHost
import com.corphish.quicktools.ui.common.walkthrough.walkthroughTarget
import com.corphish.quicktools.ui.theme.AppTheme
import com.corphish.quicktools.viewmodels.TexActionState
import com.corphish.quicktools.viewmodels.TextActionViewModel
import com.tabotabo.ui.layout.CardList
import com.tabotabo.ui.layout.LocalScreenLayoutInfo
import com.tabotabo.ui.motion.PopInContent
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.body
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextActionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT) ?: ""
        val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
        val forceCopy = intent.getBooleanExtra(Constants.INTENT_FORCE_COPY, false)

        setContent {
            AppTheme {
                WalkthroughHost(walkthroughs = listOf(Walkthroughs.TEXT_ACTIONS)) {
                    TextActionUI(
                        text = text.toString(),
                        canBeApplied = !readonly && !forceCopy,
                        onApply = {
                            val resultIntent = Intent()
                            resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, it)
                            setResult(RESULT_OK, resultIntent)
                            finish()
                        },
                        onActionSuccess = {
                            if (it == IntentAction.COPY_TO_CLIPBOARD) {
                                Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
                            }

                            finish()
                        },
                        onActionFailed = {
                            Toast.makeText(
                                this,
                                when (it) {
                                    IntentAction.EMAIL -> R.string.send_email_fail
                                    IntentAction.PHONE -> R.string.dial_fail
                                    IntentAction.URL -> R.string.open_in_web_fail
                                    IntentAction.MAP -> R.string.open_in_maps_fail
                                    IntentAction.FLIGHT -> R.string.track_flight_fail
                                    IntentAction.CALENDAR -> R.string.add_to_calendar_fail
                                    IntentAction.APPLY, IntentAction.COPY_TO_CLIPBOARD -> R.string.generic_error
                                },
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onBack = { finish() },
                    )
                }
            }
        }
    }

    companion object {
        /**
         * Builds the intent a feature uses to hand its result to Text Actions and then finish.
         *
         * [Intent.FLAG_ACTIVITY_FORWARD_RESULT] makes Text Actions reply directly to whoever
         * started that feature (the source app or the options dialog), so Apply reaches the
         * source even though the feature finishes right after the hand off. The caller must
         * use startActivity (not start it for result) for the forwarding to work.
         *
         * @param context Context used to build the intent.
         * @param text Text to act on.
         * @param readonly Whether the source text is read-only.
         * @param forceCopy Whether only copying should be offered.
         * @param hue Theme hue of the calling screen, so Text Actions keeps its colors; null
         *            lets Text Actions pick its own.
         */
        fun handOffIntent(
            context: Context,
            text: String,
            readonly: Boolean,
            forceCopy: Boolean,
            hue: Float? = null,
        ) = Intent(context, TextActionActivity::class.java).apply {
            putExtra(Intent.EXTRA_PROCESS_TEXT, text)
            putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, readonly)
            putExtra(Constants.INTENT_FORCE_COPY, forceCopy)
            hue?.let { putExtra(Constants.INTENT_HUE, it) }
            addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
        }
    }
}

/**
 * Text actions screen: what can be done with the text (apply, copy, open, dial, ...), shown as
 * tappable cards once detected.
 * - Portrait: description, a short preview of the text and the action cards in one column.
 * - Landscape: the full preview on the left; the header, description and action cards at the
 *   bottom of the right pane (see [AppScreenScaffold]).
 * The preview and the action cards are targets of [Walkthroughs.TEXT_ACTIONS].
 *
 * @param text Text to act on.
 * @param canBeApplied Whether the text may replace the selected text.
 * @param onApply Called with the text to apply.
 * @param onActionSuccess Called after an action completed.
 * @param onActionFailed Called when an action could not be performed.
 * @param onBack Called when the user leaves the screen.
 */
@Composable
fun TextActionUI(
    text: String,
    canBeApplied: Boolean,
    onApply: (String) -> Unit,
    onActionSuccess: (IntentAction) -> Unit,
    onActionFailed: (IntentAction) -> Unit,
    onBack: () -> Unit,
) {
    val viewModel = hiltViewModel<TextActionViewModel>()
    val actionState by viewModel.textActionsFlow.collectAsStateWithLifecycle()

    LaunchedEffect(text) {
        viewModel.determineActions(text, canBeApplied)
    }

    val onActionClick: (ParsedTextAction) -> Unit = { action ->
        if (action.type == IntentAction.APPLY) {
            onApply(action.parsedText)
        } else if (viewModel.performAction(action)) {
            onActionSuccess(action.type)
        } else {
            onActionFailed(action.type)
        }
    }

    AppScreenScaffold(
        header = { AppScreenHeader(id = R.string.title_activity_text_action, onBack = onBack) },
        // The scaffold reports the orientation to its slots: the actions sit in the right pane
        // in landscape, and under the preview in portrait
        actions = {
            if (LocalScreenLayoutInfo.current.isLandscape) {
                PopInContent(targetState = actionState) { state ->
                    when (state) {
                        is TexActionState.Loading -> LoadingLayout(modifier = Modifier.fillMaxWidth())
                        is TexActionState.Result -> ActionsPane(actions = state.actions, onActionClick = onActionClick)
                    }
                }
            }
        },
        landscapePaneArrangement = Arrangement.Bottom,
    ) {
        if (LocalScreenLayoutInfo.current.isLandscape) {
            TextPreview(
                text = text,
                maxLines = Int.MAX_VALUE,
                modifier = Modifier
                    .fillMaxSize()
                    .walkthroughTarget(WalkthroughTargets.TEXT_ACTIONS_PREVIEW),
            )
        } else {
            PopInContent(targetState = actionState, modifier = Modifier.fillMaxSize()) { state ->
                when (state) {
                    is TexActionState.Loading -> LoadingLayout(modifier = Modifier.fillMaxSize())
                    is TexActionState.Result -> ActionsLayout(
                        text = text,
                        actions = state.actions,
                        onActionClick = onActionClick,
                    )
                }
            }
        }
    }
}

/**
 * Progress indicator shown while actions are being detected.
 *
 * @param modifier Modifier for the area the indicator is centred in.
 */
@Composable
fun LoadingLayout(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = TabotaboTheme.colors.primary)
    }
}

/**
 * Portrait layout: description and a preview of the text, followed by one card per detected
 * action.
 *
 * @param text Text the actions apply to, shown as a preview.
 * @param actions Detected actions.
 * @param onActionClick Called with the tapped action.
 */
@Composable
fun ActionsLayout(
    text: String,
    actions: List<ParsedTextAction>,
    onActionClick: (ParsedTextAction) -> Unit,
) {
    val spacing = TabotaboTheme.dimensions.spacing

    Column(modifier = Modifier.fillMaxSize()) {
        Subtitle(
            text = stringResource(id = R.string.text_actions_desc),
            maxLines = Int.MAX_VALUE,
            modifier = Modifier.padding(bottom = spacing.l)
        )

        TextPreview(
            text = text,
            modifier = Modifier
                .padding(bottom = spacing.l)
                .walkthroughTarget(WalkthroughTargets.TEXT_ACTIONS_PREVIEW),
        )

        CardList(
            items = actions,
            modifier = Modifier
                .weight(1f)
                .walkthroughTarget(WalkthroughTargets.TEXT_ACTIONS_LIST),
        ) { item ->
            ActionItem(action = item, onClick = { onActionClick(item) })
        }
    }
}

/**
 * Landscape right pane content: description followed by one card per detected action. The
 * pane scrolls as a whole, so the cards are a plain column.
 *
 * @param actions Detected actions.
 * @param onActionClick Called with the tapped action.
 */
@Composable
private fun ActionsPane(
    actions: List<ParsedTextAction>,
    onActionClick: (ParsedTextAction) -> Unit,
) {
    val spacing = TabotaboTheme.dimensions.spacing

    Column(verticalArrangement = Arrangement.spacedBy(spacing.l)) {
        Subtitle(text = stringResource(id = R.string.text_actions_desc), maxLines = Int.MAX_VALUE)
        Column(
            modifier = Modifier.walkthroughTarget(WalkthroughTargets.TEXT_ACTIONS_LIST),
            verticalArrangement = Arrangement.spacedBy(spacing.m),
        ) {
            actions.forEach { item ->
                ActionItem(action = item, onClick = { onActionClick(item) })
            }
        }
    }
}

/** Lines of text the preview shows in portrait, to keep the actions visible. */
private const val PREVIEW_MAX_LINES = 4

/**
 * Shows the text the actions apply to, so the user can check it (e.g. an evaluated result)
 * before copying or applying it. The text scrolls when it does not fit the card.
 *
 * @param text Text to preview.
 * @param modifier Modifier for the card.
 * @param maxLines Most lines shown; long text is truncated beyond them.
 */
@Composable
fun TextPreview(text: String, modifier: Modifier = Modifier, maxLines: Int = PREVIEW_MAX_LINES) {
    GlowCard(modifier = modifier.fillMaxWidth(), glow = false) {
        SectionLabel(text = stringResource(id = R.string.preview))
        Text(
            text = text,
            style = TabotaboTheme.typography.body,
            color = TabotaboTheme.colors.accent,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = TabotaboTheme.dimensions.spacing.s)
                .verticalScroll(rememberScrollState())
        )
    }
}

/**
 * Card of one action: its icon, its label and, for actions that open something, the detected
 * text.
 *
 * @param action Action to show.
 * @param onClick Called when the card is tapped.
 */
@Composable
private fun ActionItem(action: ParsedTextAction, onClick: () -> Unit) {
    val showsDetectedText = action.type != IntentAction.COPY_TO_CLIPBOARD && action.type != IntentAction.APPLY

    GlowCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        highlighted = action.type == IntentAction.APPLY,
        glow = false,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ButtonIcon(action.type.icon(), tint = TabotaboTheme.colors.primary)

            Column(modifier = Modifier.padding(start = TabotaboTheme.dimensions.spacing.l)) {
                Text(
                    text = stringResource(action.type.label()),
                    style = TabotaboTheme.typography.titleMedium,
                    color = TabotaboTheme.colors.accent,
                )
                if (showsDetectedText) {
                    Subtitle(text = action.parsedText)
                }
            }
        }
    }
}

/** Drawable resource of the icon for this action. */
@DrawableRes
private fun IntentAction.icon(): Int = when (this) {
    IntentAction.EMAIL -> R.drawable.ic_send
    IntentAction.PHONE -> R.drawable.ic_phone
    IntentAction.URL -> R.drawable.ic_open_in_new
    IntentAction.MAP -> R.drawable.ic_map
    IntentAction.FLIGHT -> R.drawable.ic_flight
    IntentAction.CALENDAR -> R.drawable.ic_calendar
    IntentAction.APPLY -> R.drawable.ic_done
    IntentAction.COPY_TO_CLIPBOARD -> R.drawable.ic_copy
}

/** String resource of the label for this action. */
@StringRes
private fun IntentAction.label(): Int = when (this) {
    IntentAction.EMAIL -> R.string.send_email
    IntentAction.PHONE -> R.string.dial
    IntentAction.URL -> R.string.open_in_browser
    IntentAction.MAP -> R.string.open_in_maps
    IntentAction.FLIGHT -> R.string.track_flight
    IntentAction.CALENDAR -> R.string.add_to_calendar
    IntentAction.APPLY -> R.string.apply
    IntentAction.COPY_TO_CLIPBOARD -> R.string.copy_to_clipboard
}
