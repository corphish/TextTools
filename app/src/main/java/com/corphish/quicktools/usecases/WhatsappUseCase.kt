package com.corphish.quicktools.usecases

import android.app.Activity
import android.content.Intent
import androidx.core.net.toUri
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.repository.SettingsRepository
import javax.inject.Inject

class WhatsappUseCase @Inject constructor(
    private val activity: Activity,
    private val settingsRepository: SettingsRepository,
) {
    fun openInWeb(phoneNumber: String) {
        val url = "${Constants.WHATSAPP_API_LINK}$phoneNumber"
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = url.toUri()
        activity.startActivity(browserIntent)
    }

    fun determinePhoneNumber(data: String?): String? {
        if (data == null) {
            return null
        } else {
            val modified = specialCharactersRemovedFrom(data)
            val regex = Regex(Constants.PHONE_NUMBER_REGEX)
            return if (regex.matches(modified)) {
                countryCodedNumber(modified)
            } else {
                null
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
}