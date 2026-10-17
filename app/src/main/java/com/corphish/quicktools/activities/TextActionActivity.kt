package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.functions.IntentAction
import com.corphish.quicktools.functions.ParsedTextAction
import com.corphish.quicktools.ui.common.AppScreenHeader
import com.corphish.quicktools.ui.common.ButtonIcon
import com.corphish.quicktools.ui.theme.AppTheme
import com.corphish.quicktools.viewmodels.TexActionState
import com.corphish.quicktools.viewmodels.TextActionViewModel
import com.tabotabo.ui.layout.CardList
import com.tabotabo.ui.layout.ScreenScaffold
import com.tabotabo.ui.motion.PopInContent
import com.tabotabo.ui.surface.GlowCard
import com.tabotabo.ui.text.Subtitle
import com.tabotabo.ui.theme.TabotaboTheme
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

/**
 * Text actions screen: what can be done with the text (apply, copy, open, dial, ...), shown as
 * tappable cards once detected.
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

    ScreenScaffold(
        header = { AppScreenHeader(id = R.string.title_activity_text_action, onBack = onBack) }
    ) {
        PopInContent(targetState = actionState, modifier = Modifier.fillMaxSize()) { state ->
            when (state) {
                is TexActionState.Loading -> LoadingLayout()
                is TexActionState.Result -> ActionsLayout(
                    actions = state.actions,
                    onApply = onApply,
                    onAction = {
                        val res = viewModel.performAction(it)

                        if (res) {
                            onActionSuccess(it.type)
                        } else {
                            onActionFailed(it.type)
                        }
                    }
                )
            }
        }
    }
}

/**
 * Progress indicator shown while actions are being detected.
 */
@Composable
fun LoadingLayout() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = TabotaboTheme.colors.primary)
    }
}

/**
 * Description followed by one card per detected action.
 *
 * @param actions Detected actions.
 * @param onApply Called with the text for the apply action.
 * @param onAction Called for any other action.
 */
@Composable
fun ActionsLayout(
    actions: List<ParsedTextAction>,
    onApply: (String) -> Unit,
    onAction: (ParsedTextAction) -> Unit
) {
    val spacing = TabotaboTheme.dimensions.spacing

    Column(modifier = Modifier.fillMaxSize()) {
        Subtitle(
            text = stringResource(id = R.string.text_actions_desc),
            maxLines = Int.MAX_VALUE,
            modifier = Modifier.padding(bottom = spacing.l)
        )

        CardList(items = actions, modifier = Modifier.weight(1f)) { item ->
            ActionItem(
                action = item,
                onClick = {
                    if (item.type == IntentAction.APPLY) {
                        onApply(item.parsedText)
                    } else {
                        onAction(item)
                    }
                }
            )
        }
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
