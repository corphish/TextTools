package com.corphish.quicktools.settings

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class SettingsHelper(
    context: Context
) {
    private val _prependCCEnabledKey = "prepend_country_code_enabled"
    private val _prependCCKey = "prepend_country_code"
    private val _decimalPoints = "decimal_points"

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
}