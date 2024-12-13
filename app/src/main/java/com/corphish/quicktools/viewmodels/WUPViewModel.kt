package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WUPViewModel @Inject constructor(private val settingsRepository: SettingsRepository): ViewModel() {
    // Nullable type to denote when phone number is invalid
    private val _processedPhoneNumber = MutableStateFlow<String?>(INITIAL_VALUE)
    val processedPhoneNumber: StateFlow<String?> = _processedPhoneNumber

    fun determinePhoneNumber(data: String?) {
        viewModelScope.launch {
            if (data == null) {
                _processedPhoneNumber.value = null
            } else {
                val modified = specialCharactersRemovedFrom(data)
                val regex = Regex(Constants.PHONE_NUMBER_REGEX)
                if (regex.matches(modified)) {
                    _processedPhoneNumber.value = countryCodedNumber(modified)
                } else {
                    _processedPhoneNumber.value = null
                }
            }
        }
    }

    private fun countryCodedNumber(phoneNumber: String): String {
        return if (settingsRepository.getPrependCountryCodeEnabled()) {
            val code = settingsRepository.getPrependCountryCode()
            if (code == null) {
                phoneNumber
            } else if (phoneNumber.startsWith(code)) {
                // If the number already starts with country code, no need to append.
                phoneNumber
            } else if (phoneNumber.startsWith("+")) {
                // If the number starts with some country code that is not the user specified
                // country code, it must be considered.
                phoneNumber
            } else {
                "$code$phoneNumber"
            }
        } else {
            phoneNumber
        }
    }

    private fun specialCharactersRemovedFrom(phoneNumber: String) : String {
        var res = phoneNumber
        for (char in Constants.PHONE_NUMBER_SPECIAL_CHARACTERS) {
            res = res.replace(char, "")
        }

        return res
    }

    companion object {
        const val INITIAL_VALUE = "some initial value"
    }
}