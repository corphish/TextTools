package com.corphish.quicktools.repository

import android.content.Context
import androidx.core.content.edit
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_APPEND
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_MODE_ASK_NEXT_TIME
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : SettingsRepository {
    private val _prependCCEnabledKey = "prepend_country_code_enabled"
    private val _prependCCKey = "prepend_country_code"
    private val _decimalPoints = "decimal_points"
    private val _evaluateResultMode = "eval_result_mode"
    private val _onboardingDone = "onboarding_done"

    private val _sharedPreferenceManager = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)

    override fun getPrependCountryCodeEnabled() =
        _sharedPreferenceManager.getBoolean(_prependCCEnabledKey, false)

    override fun setPrependCountryCodeEnabled(enabled: Boolean) {
        _sharedPreferenceManager.edit {
            putBoolean(_prependCCEnabledKey, enabled)
        }
    }

    override fun getPrependCountryCode() =
        _sharedPreferenceManager.getString(_prependCCKey, "")

    override fun setPrependCountryCode(code: String) {
        _sharedPreferenceManager.edit {
            putString(_prependCCKey, code)
        }
    }

    override fun getDecimalPoints() =
        _sharedPreferenceManager.getInt(_decimalPoints, 2)

    override fun setDecimalPoints(points: Int) {
        _sharedPreferenceManager.edit {
            putInt(_decimalPoints, points)
        }
    }

    /**
     * Returns the saved eval result mode. Unknown values, such as the removed
     * "copy to clipboard" mode (3) saved by older versions, fall back to
     * [EVAL_RESULT_MODE_ASK_NEXT_TIME].
     */
    override fun getEvaluateResultMode(): Int {
        val mode = _sharedPreferenceManager.getInt(_evaluateResultMode, EVAL_RESULT_MODE_ASK_NEXT_TIME)
        return if (mode in EVAL_RESULT_MODE_ASK_NEXT_TIME..EVAL_RESULT_APPEND) mode else EVAL_RESULT_MODE_ASK_NEXT_TIME
    }

    override fun setEvaluateResultMode(mode: Int) {
        _sharedPreferenceManager.edit {
            putInt(_evaluateResultMode, mode)
        }
    }

    override fun getOnboardingDone() =
        _sharedPreferenceManager.getBoolean(_onboardingDone, false)

    override fun setOnboardingDone(done: Boolean) {
        _sharedPreferenceManager.edit {
            putBoolean(_onboardingDone, done)
        }
    }
}
