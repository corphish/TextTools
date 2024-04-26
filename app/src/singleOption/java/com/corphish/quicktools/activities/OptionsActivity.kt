package com.corphish.quicktools.activities

import android.content.Intent
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.corphish.quicktools.R
import com.corphish.quicktools.extensions.truncate
import com.corphish.quicktools.ui.common.ListDialog
import com.corphish.quicktools.ui.theme.QuickToolsTheme

class OptionsActivity : NoUIActivity() {

    private val _options = listOf(
        Option(R.string.whatsapp, WUPActivity::class.java),
        Option(R.string.eval_title_small, EvalActivity::class.java),
        Option(R.string.transform_long, TransformActivity::class.java),
    )

    private val router = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            setResult(RESULT_OK, it.data)
        }

        finish()
    }

    override fun handleIntent(intent: Intent): Boolean {
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT).toString()
            val readonly = intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
            setContent {
                QuickToolsTheme {
                    val optionDialog = remember {
                        mutableStateOf(true)
                    }

                    if (optionDialog.value) {
                        ListDialog(
                            title = stringResource(id = R.string.app_name),
                            message = stringResource(id = R.string.app_single_op, text.truncate()),
                            list = _options,
                            stringSelector = { stringResource(id = it.optionResourceId) },
                            onItemSelected = {
                                val routeIntent = Intent(this, _options[it].handlingClass)
                                routeIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, text)
                                routeIntent.putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, readonly)
                                router.launch(routeIntent)
                                optionDialog.value = false
                            }) {
                            optionDialog.value = false
                            finish()
                        }
                    }
                }
            }
            return false
        }

        return true
    }

    data class Option(
        @StringRes val optionResourceId: Int,
        val handlingClass: Class<out NoUIActivity>,
    )
}