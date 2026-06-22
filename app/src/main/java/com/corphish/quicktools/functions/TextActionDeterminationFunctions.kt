package com.corphish.quicktools.functions

import android.util.Log
import android.view.textclassifier.TextClassifier
import android.view.textclassifier.TextLinks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TextActionDeterminationFunctions @Inject constructor() {
    suspend fun determineTextActions(text: String, textClassifier: TextClassifier): List<ParsedTextAction> {
        return withContext(Dispatchers.Default) {
            val request = TextLinks.Request.Builder(text).build()
            val links = textClassifier.generateLinks(request)
            links.links.mapNotNull { link ->
                Log.d("TextActionDeterminationFunctions", "link=$link")
                val entityType = link.getEntity(0)
                val confidenceScore = link.getConfidenceScore(entityType)

                val actionType = when (entityType) {
                    TextClassifier.TYPE_EMAIL -> IntentAction.EMAIL
                    TextClassifier.TYPE_PHONE -> IntentAction.PHONE
                    TextClassifier.TYPE_URL -> IntentAction.URL
                    TextClassifier.TYPE_ADDRESS -> IntentAction.MAP
                    TextClassifier.TYPE_FLIGHT_NUMBER -> IntentAction.FLIGHT
                    TextClassifier.TYPE_DATE, TextClassifier.TYPE_DATE_TIME -> IntentAction.CALENDAR
                    else -> null
                }

                actionType?.let {
                    if (confidenceScore >= 0.7f) {
                        ParsedTextAction(it, text.substring(link.start, link.end))
                    } else {
                        null
                    }
                }
            }
        }
    }

    fun applyAction(text: String) =
        ParsedTextAction(IntentAction.APPLY, text)

    fun copyToClipboardAction(text: String) =
        ParsedTextAction(IntentAction.COPY_TO_CLIPBOARD, text)
}

data class ParsedTextAction(val type: IntentAction, val parsedText: String)
enum class IntentAction { EMAIL, PHONE, URL, MAP, FLIGHT, CALENDAR, APPLY, COPY_TO_CLIPBOARD }