package com.corphish.quicktools.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.corphish.quicktools.data.Constants

/**
 * WUP (WhatsApp Unknown Phone number) activity handles messaging to
 * unknown phone numbers in whatsapp without saving them as a contact by
 * opening it in wa.me/<phone_number>.
 */
class WUPActivity : NoUIActivity() {
    override fun handleIntent(intent: Intent) {
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)
            val regex = Regex(Constants.PHONE_NUMBER_REGEX)
            if (text != null && regex.matches(text)) {
                openInWeb(phoneNumber = text)
            }
        }

        finish()
    }

    private fun openInWeb(phoneNumber: CharSequence) {
        val url = "https://wa.me/$phoneNumber"
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = Uri.parse(url)
        startActivity(browserIntent)
    }
}