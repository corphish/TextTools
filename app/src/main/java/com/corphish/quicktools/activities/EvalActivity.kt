package com.corphish.quicktools.activities

import android.content.Intent
import android.widget.Toast
import com.corphish.quicktools.R
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor

class EvalActivity : NoUIActivity() {
    override fun handleIntent(intent: Intent) {
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
            if (readonly) {
                // We are only interested in editable text
                return
            }

            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
            val resultText: String = try {
                val expression = ExpressionBuilder(text).build()
                val result = expression.evaluate()

                if (ceil(result) == floor(result)) {
                    result.toInt().toString()
                } else {
                    String.format(Locale.getDefault(), "%.2f", result)
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
    }
}