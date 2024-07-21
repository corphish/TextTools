package com.corphish.quicktools.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.R
import com.corphish.quicktools.settings.SettingsHelper
import com.corphish.quicktools.ui.common.ListDialog
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.floor

class EvalActivity : NoUIActivity() {
    // Result generator
    private val _replaceResultGenerator: (String, String) -> String = { input, result -> result }
    private val _appendResultGenerator: (String, String) -> String = { input, result -> "$input = $result" }

    override fun handleIntent(intent: Intent): Boolean {
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
            if (readonly) {
                // We are only interested in editable text
                Toast.makeText(this, R.string.editable_error, Toast.LENGTH_LONG).show()
                return true
            }

            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
            val settingsHelper = SettingsHelper(this)
            val decimalPoints = settingsHelper.getDecimalPoints()
            when (val mode = settingsHelper.getEvaluateResultMode()) {
                EVAL_RESULT_MODE_ASK_NEXT_TIME -> {
                    // Show option to user
                    setContent {
                        val rememberUserChoiceEnabled = remember {
                            mutableStateOf(false)
                        }

                        QuickToolsTheme {
                            ListDialog(
                                title = stringResource(id = R.string.eval_title),
                                message = stringResource(id = R.string.eval_result_desc),
                                list = listOf(
                                    R.string.eval_mode_result,
                                    R.string.eval_mode_append,
                                    R.string.eval_mode_copy
                                ),
                                supportBack = false,
                                dismissible = false,
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
                                    var selectedMode = EVAL_RESULT_MODE_ASK_NEXT_TIME
                                    when (it) {
                                        0 -> {
                                            handleEvaluationIntent(EVAL_RESULT_REPLACE, text, decimalPoints)
                                            selectedMode = EVAL_RESULT_REPLACE
                                        }
                                        1 -> {
                                            handleEvaluationIntent(EVAL_RESULT_APPEND, text, decimalPoints)
                                            selectedMode = EVAL_RESULT_APPEND
                                        }
                                        2 -> {
                                            handleEvaluationCopy(text, decimalPoints)
                                            selectedMode = EVAL_RESULT_COPY_TO_CLIPBOARD
                                        }
                                    }

                                    if (rememberUserChoiceEnabled.value) {
                                        settingsHelper.setEvaluateResultMode(selectedMode)
                                    }

                                    finish()
                                }) {
                                finish()
                            }
                        }
                    }

                    return false
                }
                EVAL_RESULT_REPLACE, EVAL_RESULT_APPEND -> {
                    handleEvaluationIntent(mode, text, decimalPoints)
                }
                EVAL_RESULT_COPY_TO_CLIPBOARD -> {
                    handleEvaluationCopy(text, decimalPoints)
                }
            }
        }

        return true
    }

    private fun handleEvaluation(text: String, decimalPoints: Int): String {
        val resultText: String = try {
            val expression = ExpressionBuilder(text).build()
            val result = expression.evaluate()

            if (ceil(result) == floor(result)) {
                result.toInt().toString()
            } else {
                val decimalFormat = DecimalFormat("0.${"#".repeat(decimalPoints)}")
                decimalFormat.format(result)
            }
        } catch (e: Exception) {
            Toast.makeText(
                this,
                R.string.invalid_expression,
                Toast.LENGTH_LONG
            ).show()

            text
        }

        return resultText
    }

    private fun handleEvaluationIntent(mode: Int, text: String, decimalPoints: Int) {
        val result = handleEvaluation(text, decimalPoints)
        val resultGenerator: (String, String) -> String = if (mode == EVAL_RESULT_APPEND) _appendResultGenerator else _replaceResultGenerator

        val resultIntent = Intent()
        resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, resultGenerator(text, result))
        setResult(RESULT_OK, resultIntent)
    }

    private fun handleEvaluationCopy(text: String, decimalPoints: Int) {
        val result = handleEvaluation(text, decimalPoints)
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("evaluation_result", result)
        clipboard.setPrimaryClip(clip)
    }

    companion object {
        // Eval result mode choices will be shown to user next time
        const val EVAL_RESULT_MODE_ASK_NEXT_TIME = 0

        // Eval result will be replaced by the selected text
        const val EVAL_RESULT_REPLACE = 1

        // Eval result will be appended after the selected text with = sign
        const val EVAL_RESULT_APPEND = 2

        // Eval result will be copied to the clipboard
        const val EVAL_RESULT_COPY_TO_CLIPBOARD = 3
    }
}