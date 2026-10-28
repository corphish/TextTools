package com.corphish.quicktools.activities

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.ui.common.ListDialog
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2
import com.corphish.quicktools.viewmodels.EvalUiState
import com.corphish.quicktools.viewmodels.EvalViewModel
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_APPEND
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_REPLACE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Evaluates the selected expression.
 *
 * Works on both editable and read-only text. The result is shown in Text Actions, where it can
 * always be copied and, for editable sources, applied (replace or append). If the user has
 * remembered Replace/Append and the source is editable, the result is applied instantly.
 * See [EvalViewModel] for the full flow.
 */
@AndroidEntryPoint
class EvalActivity : NoUIActivity() {
    private val evalViewModel: EvalViewModel by viewModels()

    // Guards against observing the flow twice when a new intent arrives
    private var _isObserving = false

    private val textActionsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val appliedText = if (it.resultCode == RESULT_OK) {
                it.data?.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString()
            } else null
            evalViewModel.onActionsClosed(appliedText)
        }

    override fun handleIntent(intent: Intent): Boolean {
        val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: return true
        val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
        val forceCopy = intent.getBooleanExtra(Constants.INTENT_FORCE_COPY, false)

        evalViewModel.start(text, canApply = !readonly && !forceCopy)

        if (!_isObserving) {
            _isObserving = true
            observeFlow(readonly, forceCopy)
        }

        return false
    }

    /**
     * Shows the Replace/Append dialog when needed and reacts to one-off steps of the flow
     * (launching Text Actions, errors, finishing).
     */
    private fun observeFlow(readonly: Boolean, forceCopy: Boolean) {
        setContent {
            val state by evalViewModel.uiState.collectAsState()
            if (state is EvalUiState.AskApplyMode) {
                UserSelectionDialog(
                    onSelected = evalViewModel::onApplyModeSelected,
                    onDismiss = evalViewModel::onApplyModeDismissed
                )
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                evalViewModel.uiState.collect { state ->
                    when (state) {
                        is EvalUiState.ShowActions -> {
                            textActionsLauncher.launch(
                                Intent(this@EvalActivity, TextActionActivity::class.java).apply {
                                    putExtra(Intent.EXTRA_PROCESS_TEXT, state.result)
                                    putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, readonly)
                                    putExtra(Constants.INTENT_FORCE_COPY, forceCopy)
                                }
                            )
                            evalViewModel.onActionsLaunched()
                        }

                        is EvalUiState.Error -> {
                            Toast.makeText(this@EvalActivity, R.string.invalid_expression, Toast.LENGTH_LONG).show()
                            finish()
                        }

                        is EvalUiState.Finished -> {
                            state.output?.let {
                                setResult(RESULT_OK, Intent().putExtra(Intent.EXTRA_PROCESS_TEXT, it))
                            }
                            finish()
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}

/**
 * Asks how the evaluated result should be applied to the source text.
 *
 * @param onSelected Called with [EVAL_RESULT_REPLACE] or [EVAL_RESULT_APPEND], and whether the
 *                   choice should be remembered.
 * @param onDismiss Called when the dialog is dismissed without a choice.
 */
@Composable
fun UserSelectionDialog(
    onSelected: (Int, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var rememberUserChoiceEnabled by rememberSaveable { mutableStateOf(false) }

    QuickToolsTheme {
        ListDialog(
            title = stringResource(id = R.string.eval_title_small),
            message = stringResource(id = R.string.eval_result_desc),
            list = listOf(
                R.string.eval_mode_result,
                R.string.eval_mode_append,
            ),
            supportBack = false,
            dismissible = true,
            stringSelector = {
                stringResource(id = it)
            },
            additionalContent = {
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Checkbox(
                        checked = rememberUserChoiceEnabled,
                        onCheckedChange = { rememberUserChoiceEnabled = it },
                        enabled = true,
                    )

                    Text(
                        text = stringResource(id = R.string.remember_this_choice),
                        style = TypographyV2.bodyMedium
                    )
                }
            },
            iconSelector = { R.drawable.ic_numbers },
            onItemSelected = {
                val mode = if (it == 0) EVAL_RESULT_REPLACE else EVAL_RESULT_APPEND
                onSelected(mode, rememberUserChoiceEnabled)
            }) { onDismiss() }
    }
}
