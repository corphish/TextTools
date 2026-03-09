package com.corphish.quicktools.activities

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.extensions.truncate
import com.corphish.quicktools.repository.FeatureIds
import com.corphish.quicktools.ui.common.ListDialog
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.viewmodels.OptionsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OptionsActivity : NoUIActivity() {
    private val viewModel: OptionsViewModel by viewModels()

    private val _options = listOf(
        Option(FeatureIds.WHATSAPP, R.string.whatsapp, R.drawable.ic_whatsapp, WUPActivity::class.java, false),
        Option(FeatureIds.EVAL, R.string.eval_title_small, R.drawable.ic_numbers, EvalActivity::class.java, true),
        Option(FeatureIds.TRANSFORM, R.string.transform_long, R.drawable.ic_text_transform, TransformActivity::class.java, false),
        Option(FeatureIds.TEXT_COUNT, R.string.text_count, R.drawable.ic_text_count, TextCountActivity::class.java, false),
        Option(FeatureIds.SAVE_TEXT, R.string.save_text_title, R.drawable.ic_save, SaveTextActivity::class.java, false),
        Option(FeatureIds.FIND_AND_REPLACE, R.string.title_activity_find_and_replace, R.drawable.ic_find_and_replace, FindAndReplaceActivity::class.java, true)
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
            val forceCopy = intent.getBooleanExtra(Constants.INTENT_FORCE_COPY, false)
            setContent {
                QuickToolsTheme {
                    val enabledFeatures by viewModel.enabledFeatures.collectAsState()
                    val optionDialog = remember {
                        mutableStateOf(true)
                    }

                    if (optionDialog.value) {
                        val list = _options.filter { enabledFeatures.contains(it.id) && (!readonly || !it.requiresEditable) }
                        ListDialog(
                            title = stringResource(id = R.string.app_name),
                            message = stringResource(id = R.string.app_single_op, text.truncate()),
                            list = list,
                            stringSelector = { stringResource(id = it.optionResourceId) },
                            iconSelector = { it.icon },
                            onItemSelected = {
                                val routeIntent = Intent(this, list[it].handlingClass)
                                routeIntent.putExtra(Intent.EXTRA_PROCESS_TEXT, text)
                                routeIntent.putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, readonly)
                                routeIntent.putExtra(Constants.INTENT_FORCE_COPY, forceCopy)
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

    /**
     * Denotes an option.
     */
    data class Option(
        /**
         * Id of the option.
         */
        val id: FeatureIds,

        /**
         * String resource id of the option.
         */
        @StringRes val optionResourceId: Int,

        /**
         * Icon of this option
         */
        @DrawableRes val icon: Int,

        /**
         * Activity that handles the option function.
         */
        val handlingClass: Class<out Activity>,

        /**
         * Boolean indicating whether this option requires editable
         * text input.
         */
        val requiresEditable: Boolean
    )
}