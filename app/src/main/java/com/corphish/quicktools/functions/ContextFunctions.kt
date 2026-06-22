package com.corphish.quicktools.functions

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.view.textclassifier.TextClassificationManager
import android.view.textclassifier.TextClassifier
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Class to perform functions that involve context.
 * No UI related stuff should be performed here.
 */
class ContextFunctions @Inject constructor(
    @ApplicationContext
    private val context: Context
) {
    fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text_tools_result", text)
        clipboard.setPrimaryClip(clip)
    }

    fun openInWeb(url: String): Boolean {
        // Ensure the URL has a scheme, otherwise it will crash
        val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
            "https://$url"
        } else {
            url
        }

        val browserIntent = Intent(Intent.ACTION_VIEW, formattedUrl.toUri())
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(browserIntent)
            return true
        } catch (_: ActivityNotFoundException) {
            return false
        }
    }

    fun sendEmail(emailAddress: String): Boolean {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:$emailAddress".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
            return true
        } catch (_: ActivityNotFoundException) {
            return false
        }
    }

    fun dialPhone(phoneNumber: String): Boolean {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
            return true
        } catch (_: ActivityNotFoundException) {
            return false
        }
    }

    fun openMap(address: String): Boolean {
        val encodedAddress = Uri.encode(address)
        val intent = Intent(Intent.ACTION_VIEW, "geo:0,0?q=$encodedAddress".toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
            return true
        } catch (_: ActivityNotFoundException) {
            return false
        }
    }

    fun trackFlight(flightNumber: String): Boolean {
        val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
            putExtra(SearchManager.QUERY, "Flight $flightNumber status")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
            return true
        } catch (_: ActivityNotFoundException) {
            // Fallback to a standard browser search if they don't have a dedicated web search handler
            return openInWeb("https://www.google.com/search?q=Flight+$flightNumber+status")
        }
    }

    fun addToCalendar(dateText: String): Boolean {
        // Note: The TextClassifier gives us the raw string (e.g., "Tomorrow at 3pm").
        // We pass this into the TITLE field so the user can see what triggered it,
        // though you'd need a date parsing library if you wanted to auto-fill the exact timestamp.
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, "Event: $dateText")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
            return true
        } catch (_: ActivityNotFoundException) {
            return false
        }
    }

    fun textClassifier(): TextClassifier {
        val tcm = context.getSystemService(TextClassificationManager::class.java)
        return tcm.textClassifier
    }
}