package com.corphish.quicktools.activities

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.repository.SettingsRepository

/**
 * WUP (WhatsApp Unknown Phone number) activity handles messaging to
 * unknown phone numbers in whatsapp without saving them as a contact by
 * opening it in wa.me/<phone_number>.
 */
class WUPActivity : NoUIActivity() {
    override fun handleIntent(intent: Intent): Boolean {
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""
            val modified = specialCharactersRemovedFrom(text)
            val regex = Regex(Constants.PHONE_NUMBER_REGEX)
            if (regex.matches(modified)) {
                openInWeb(phoneNumber = modified)
            } else {
                Toast.makeText(this, R.string.invalid_phone_number, Toast.LENGTH_LONG).show()
            }
        }

        return true
    }

    private fun openInWeb(phoneNumber: String) {
        val url = "https://wa.me/${countryCodedNumber(phoneNumber)}"
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = Uri.parse(url)
        startActivity(browserIntent)
    }

    private fun countryCodedNumber(phoneNumber: String): String {
        val settingsRepository = SettingsRepository(this)
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