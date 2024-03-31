package com.corphish.quicktools.activities

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.settings.SettingsHelper

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
        val settingsHelper = SettingsHelper(this)
        return if (settingsHelper.getPrependCountryCodeEnabled()) {
            val code = settingsHelper.getPrependCountryCode()
            "$code$phoneNumber"
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