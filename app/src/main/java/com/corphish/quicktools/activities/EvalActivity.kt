package com.corphish.quicktools.activities

import android.content.Intent
import android.widget.Toast
import com.corphish.quicktools.R
import com.corphish.quicktools.settings.SettingsHelper
import net.objecthunter.exp4j.ExpressionBuilder
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.floor

class EvalActivity : NoUIActivity() {
    override fun handleIntent(intent: Intent): Boolean {
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
            if (readonly) {
                // We are only interested in editable text
                Toast.makeText(this, R.string.editable_error, Toast.LENGTH_LONG).show()
                return true
            }

            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
            val resultText: String = try {
                val expression = ExpressionBuilder(text).build()
                val result = expression.evaluate()

                if (ceil(result) == floor(result)) {
                    result.toInt().toString()
                } else {
                    val settingsHelper = SettingsHelper(this)
                    val decimalPoints = settingsHelper.getDecimalPoints()
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

            val resultIntent = Intent()
            resultIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, resultText)
            setResult(RESULT_OK, resultIntent)
        }

        return true
    }
}