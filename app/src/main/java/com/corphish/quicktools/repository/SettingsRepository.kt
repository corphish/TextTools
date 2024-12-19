package com.corphish.quicktools.repository

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_MODE_ASK_NEXT_TIME

class SettingsRepository(
    context: Context
) {
    private val _prependCCEnabledKey = "prepend_country_code_enabled"
    private val _prependCCKey = "prepend_country_code"
    private val _decimalPoints = "decimal_points"
    private val _evaluateResultMode = "eval_result_mode"
    private val _onboardingDone = "onboarding_done"

    private val _sharedPreferenceManager = PreferenceManager.getDefaultSharedPreferences(context)

    fun getPrependCountryCodeEnabled() =
        _sharedPreferenceManager.getBoolean(_prependCCEnabledKey, false)

    fun setPrependCountryCodeEnabled(enabled: Boolean) {
        _sharedPreferenceManager.edit {
            putBoolean(_prependCCEnabledKey, enabled)
        }
    }

    fun getPrependCountryCode() =
        _sharedPreferenceManager.getString(_prependCCKey, "")

    fun setPrependCountryCode(code: String) {
        _sharedPreferenceManager.edit {
            putString(_prependCCKey, code)
        }
    }

    fun getDecimalPoints() =
        _sharedPreferenceManager.getInt(_decimalPoints, 2)

    fun setDecimalPoints(points: Int) {
        _sharedPreferenceManager.edit {
            putInt(_decimalPoints, points)
        }
    }

    fun getEvaluateResultMode() =
        _sharedPreferenceManager.getInt(_evaluateResultMode, EVAL_RESULT_MODE_ASK_NEXT_TIME)

    fun setEvaluateResultMode(mode: Int) {
        _sharedPreferenceManager.edit {
            putInt(_evaluateResultMode, mode)
        }
    }

    fun getOnboardingDone() =
        _sharedPreferenceManager.getBoolean(_onboardingDone, false)

    fun setOnboardingDone(done: Boolean) {
        _sharedPreferenceManager.edit {
            putBoolean(_onboardingDone, done)
        }
    }
}