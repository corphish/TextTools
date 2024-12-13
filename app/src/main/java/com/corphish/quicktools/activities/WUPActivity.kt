package com.corphish.quicktools.activities

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.corphish.quicktools.R
import com.corphish.quicktools.viewmodels.WUPViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * WUP (WhatsApp Unknown Phone number) activity handles messaging to
 * unknown phone numbers in whatsapp without saving them as a contact by
 * opening it in wa.me/<phone_number>.
 */
@AndroidEntryPoint
class WUPActivity : NoUIActivity() {
    private val wupViewModel: WUPViewModel by viewModels()

    override fun handleIntent(intent: Intent): Boolean {
        if (intent.hasExtra(Intent.EXTRA_PROCESS_TEXT)) {
            val text = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT)?.toString() ?: ""

            lifecycleScope.launch {
                wupViewModel.processedPhoneNumber.collect {
                    if (it != null) {
                        if (it != WUPViewModel.INITIAL_VALUE) {
                            openInWeb(phoneNumber = it)
                        } else {
                            // Do nothing as it is initial response
                        }
                    } else {
                        Toast.makeText(this@WUPActivity, R.string.invalid_phone_number, Toast.LENGTH_LONG).show()
                    }
                }
            }

            wupViewModel.determinePhoneNumber(text)
        }

        return true
    }

    private fun openInWeb(phoneNumber: String) {
        val url = "https://wa.me/$phoneNumber"
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.data = Uri.parse(url)
        startActivity(browserIntent)
    }
}