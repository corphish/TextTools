package com.corphish.quicktools.settings

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.corphish.quicktools.activities.EvalActivity

class SettingsHelper(
    context: Context
) {
    private val _prependCCEnabledKey = "prepend_country_code_enabled"
    private val _prependCCKey = "prepend_country_code"
    private val _decimalPoints = "decimal_points"
    private val _evaluateResultMode = "eval_result_mode"

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
        _sharedPreferenceManager.getInt(_evaluateResultMode, EvalActivity.EVAL_RESULT_MODE_ASK_NEXT_TIME)

    fun setEvaluateResultMode(mode: Int) {
        _sharedPreferenceManager.edit {
            putInt(_evaluateResultMode, mode)
        }
    }
}