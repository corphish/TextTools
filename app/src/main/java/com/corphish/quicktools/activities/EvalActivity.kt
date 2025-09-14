package com.corphish.quicktools.activities

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.data.Result
import com.corphish.quicktools.ui.common.ListDialog
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2
import com.corphish.quicktools.utils.ClipboardHelper
import com.corphish.quicktools.viewmodels.EvalViewModel
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_APPEND
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_COPY_TO_CLIPBOARD
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_MODE_ASK_NEXT_TIME
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_REPLACE
import com.corphish.quicktools.viewmodels.EvaluateResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EvalActivity : NoUIActivity() {
    // Result generator
    private val _replaceResultGenerator: (String, String) -> String = { _, result -> result }
    private val _appendResultGenerator: (String, String) -> String =
        { input, result -> "$input = $result" }

    private val evalViewModel: EvalViewModel by viewModels()

    override fun handleIntent(intent: Intent): Boolean {
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
            val forceCopy = intent.getBooleanExtra(Constants.INTENT_FORCE_COPY, false)

            if (readonly) {
                // We are only interested in editable text
                Toast.makeText(this, R.string.editable_error, Toast.LENGTH_LONG).show()
                return true
            }

            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()

            lifecycleScope.launch {
                evalViewModel.shouldForceCopy(forceCopy)
            }

            lifecycleScope.launch {
                evalViewModel.evalMode.collect { mode ->
                    when (mode) {
                        EVAL_RESULT_MODE_ASK_NEXT_TIME -> {
                            setContent {
                                UserSelectionDialog(
                                    onSelected = { selectedMode, rememberChoice ->
                                        evalViewModel.denoteModeSelectionByUser(selectedMode)
                                        evalViewModel.denoteUserRememberChoice(rememberChoice)
                                        evalViewModel.evaluate(text)
                                    },
                                    onDismiss = { finish() }
                                )
                            }
                        }

                        else -> {
                            evalViewModel.evaluate(text)
                        }
                    }
                }
            }

            lifecycleScope.launch {
                evalViewModel.evalResult.collect { result: Result<EvaluateResult> ->
                    when (result) {
                        is Result.Success -> {
                            val str = result.value.resultString
                            when (val mode = result.value.finalMode) {
                                EVAL_RESULT_REPLACE, EVAL_RESULT_APPEND -> {
                                    handleEvaluationIntent(text, str, mode)
                                }

                                EVAL_RESULT_COPY_TO_CLIPBOARD -> {
                                    ClipboardHelper.copyToClipboard(this@EvalActivity, str)
                                }
                            }

                            finish()
                        }

                        Result.Error -> {
                            Toast.makeText(
                                this@EvalActivity,
                                R.string.invalid_expression,
                                Toast.LENGTH_LONG
                            ).show()

                            finish()
                        }

                        Result.Initial -> {
                            // Do nothing
                        }
                    }
                }
            }
        }

        return false
    }

    private fun handleEvaluationIntent(text: String, result: String, mode: Int) {
        val resultGenerator: (String, String) -> String =
            if (mode == EVAL_RESULT_APPEND) _appendResultGenerator else _replaceResultGenerator

        val resultIntent = Intent()
        resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, resultGenerator(text, result))
        setResult(RESULT_OK, resultIntent)
    }
}

@Composable
fun UserSelectionDialog(
    onSelected: (Int, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val rememberUserChoiceEnabled = remember {
        mutableStateOf(false)
    }

    QuickToolsTheme {
        ListDialog(
            title = stringResource(id = R.string.eval_title_small),
            message = stringResource(id = R.string.eval_result_desc),
            list = listOf(
                R.string.eval_mode_result,
                R.string.eval_mode_append,
                R.string.eval_mode_copy
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
                        checked = rememberUserChoiceEnabled.value,
                        onCheckedChange = { rememberUserChoiceEnabled.value = it },
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
                when (it) {
                    0 -> {
                        onSelected(EVAL_RESULT_REPLACE, rememberUserChoiceEnabled.value)
                    }

                    1 -> {
                        onSelected(EVAL_RESULT_APPEND, rememberUserChoiceEnabled.value)
                    }

                    2 -> {
                        onSelected(EVAL_RESULT_COPY_TO_CLIPBOARD, rememberUserChoiceEnabled.value)
                    }
                }
            }) { onDismiss() }
    }
}