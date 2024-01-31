package com.corphish.quicktools.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.corphish.quicktools.data.Constants

/**
 * WUP (WhatsApp Unknown Phone number) activity handles messaging to
 * unknown phone numbers in whatsapp without saving them as a contact by
 * opening it in wa.me/<phone_number>.
 */
class WUPActivity : NoUIActivity() {
    override fun handleIntent(intent: Intent) {
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""
            val modified = specialCharactersRemovedFrom(text)
            val regex = Regex(Constants.PHONE_NUMBER_REGEX)
            if (regex.matches(modified)) {
                openInWeb(phoneNumber = modified)
            }
        }

        finish()
    }

    private fun openInWeb(phoneNumber: String) {
        val url = "https://wa.me/$phoneNumber"
        Log.d("WUP", "https://wa.me/${specialCharactersRemovedFrom(phoneNumber)}")
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = Uri.parse(url)
        startActivity(browserIntent)
    }

    private fun specialCharactersRemovedFrom(phoneNumber: String) : String {
        var res = phoneNumber
        for (char in Constants.PHONE_NUMBER_SPECIAL_CHARACTERS) {
            res = res.replace(char, "")
        }

        return res
    }
}