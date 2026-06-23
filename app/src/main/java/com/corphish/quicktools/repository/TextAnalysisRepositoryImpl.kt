package com.corphish.quicktools.repository

import android.view.textclassifier.TextClassifier
import com.corphish.quicktools.data.TextCountResult
import com.corphish.quicktools.functions.ContextFunctions
import com.corphish.quicktools.functions.TextClassifierFunctions
import com.corphish.quicktools.functions.TextFunctions
import javax.inject.Inject

class TextAnalysisRepositoryImpl @Inject constructor(
    private val textFunctions: TextFunctions,
    private val textClassifierFunctions: TextClassifierFunctions,
    private val contextFunctions: ContextFunctions
) : TextAnalysisRepository {

    override suspend fun analyzeText(text: String): TextCountResult {
        val baseResult = textFunctions.countText(text)
        val entities = textClassifierFunctions.extractEntities(text, contextFunctions.textClassifier())

        return baseResult.copy(
            emails = entities[TextClassifier.TYPE_EMAIL] ?: baseResult.emails,
            phoneNumbers = entities[TextClassifier.TYPE_PHONE] ?: baseResult.phoneNumbers,
            urls = entities[TextClassifier.TYPE_URL] ?: baseResult.urls,
            dates = entities[TextClassifier.TYPE_DATE] ?: entities[TextClassifier.TYPE_DATE_TIME] ?: baseResult.dates,
            ipv4Addresses = baseResult.ipv4Addresses, // TextClassifier doesn't explicitly have IP
            ipv6Addresses = baseResult.ipv6Addresses,
            times = entities[TextClassifier.TYPE_DATE_TIME] ?: baseResult.times,
            currencies = baseResult.currencies, // TextClassifier doesn't explicitly have Currency
        )
    }
}
