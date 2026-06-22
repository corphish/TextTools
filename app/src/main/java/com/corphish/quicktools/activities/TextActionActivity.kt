package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.functions.IntentAction
import com.corphish.quicktools.functions.ParsedTextAction
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.Typography
import com.corphish.quicktools.ui.theme.TypographyV2
import com.corphish.quicktools.viewmodels.TexActionState
import com.corphish.quicktools.viewmodels.TextActionViewModel
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
            QuickToolsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                        innerPadding = innerPadding,
                    )
                }
            }
        }
    }
}

@Composable
fun TextActionUI(
    text: String,
    canBeApplied: Boolean,
    onApply: (String) -> Unit,
    onActionSuccess: (IntentAction) -> Unit,
    onActionFailed: (IntentAction) -> Unit,
    innerPadding: PaddingValues
) {
    val viewModel = hiltViewModel<TextActionViewModel>()
    val actionState by viewModel.textActionsFlow.collectAsStateWithLifecycle()

    LaunchedEffect(text) {
        viewModel.determineActions(text, canBeApplied)
    }

    Column(modifier = Modifier
        .padding(innerPadding)
        .fillMaxSize()
        .padding(all = 16.dp)) {
        Text(
            text = stringResource(id = R.string.text_actions),
            style = TypographyV2.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = BrandFontFamily,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = stringResource(id = R.string.text_actions_desc),
            style = Typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when (actionState) {
            is TexActionState.Loading -> LoadingLayout()
            is TexActionState.Result -> ActionsLayout(
                actions = (actionState as TexActionState.Result).actions,
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

@Composable
fun ColumnScope.LoadingLayout() {
    Spacer(modifier = Modifier.weight(1f))
    CircularProgressIndicator()
    Spacer(modifier = Modifier.weight(1f))
}

@Composable
fun ActionsLayout(
    actions: List<ParsedTextAction>,
    onApply: (String) -> Unit,
    onAction: (ParsedTextAction) -> Unit
) {
    val cornerRadius = 16.dp
    LazyColumn {
        itemsIndexed(actions) { index, item ->
            // 1. Determine the shape based on the index
            val shape = when {
                actions.size == 1 -> RoundedCornerShape(cornerRadius)
                index == 0 -> RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
                index == actions.lastIndex -> RoundedCornerShape(
                    bottomStart = cornerRadius,
                    bottomEnd = cornerRadius
                )

                else -> RectangleShape
            }

            // 2. The Item Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape) // Clips the ripple effect and background to the dynamic shape
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp))
                    .clickable {
                        if (item.type == IntentAction.APPLY) {
                            onApply(item.parsedText)
                        } else {
                            onAction(item)
                        }
                    }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(
                            when (item.type) {
                                IntentAction.EMAIL -> R.drawable.ic_send
                                IntentAction.PHONE -> R.drawable.ic_phone
                                IntentAction.URL -> R.drawable.ic_open_in_new
                                IntentAction.MAP -> R.drawable.ic_map
                                IntentAction.FLIGHT -> R.drawable.ic_flight
                                IntentAction.CALENDAR -> R.drawable.ic_calendar
                                IntentAction.APPLY -> R.drawable.ic_done
                                IntentAction.COPY_TO_CLIPBOARD -> R.drawable.ic_copy
                            }
                        ),
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    if (item.type != IntentAction.COPY_TO_CLIPBOARD && item.type != IntentAction.APPLY) {
                        Column {
                            Text(
                                text = stringResource(
                                    when (item.type) {
                                        IntentAction.EMAIL -> R.string.send_email
                                        IntentAction.PHONE -> R.string.dial
                                        IntentAction.URL -> R.string.open_in_browser
                                        IntentAction.MAP -> R.string.open_in_maps
                                        IntentAction.FLIGHT -> R.string.track_flight
                                        IntentAction.CALENDAR -> R.string.add_to_calendar
                                    }
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = BrandFontFamily
                            )

                            Text(
                                text = item.parsedText,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Text(
                            text = stringResource(
                                if (item.type == IntentAction.COPY_TO_CLIPBOARD) {
                                    R.string.copy_to_clipboard
                                } else {
                                    R.string.apply
                                }
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = BrandFontFamily
                        )
                    }
                }
            }

            // 3. The Divider (Only show if it's NOT the last item)
            if (index < actions.lastIndex) {
                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.background
                )
            }
        }
    }
}