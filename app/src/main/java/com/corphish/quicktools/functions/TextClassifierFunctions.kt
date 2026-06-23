package com.corphish.quicktools.functions

import android.view.textclassifier.TextClassifier
import android.view.textclassifier.TextLinks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextClassifierFunctions @Inject constructor() {

    suspend fun extractEntities(text: String, textClassifier: TextClassifier): Map<String, List<String>> {
        return withContext(Dispatchers.Default) {
            val request = TextLinks.Request.Builder(text).build()
            val links = textClassifier.generateLinks(request)
            val result = mutableMapOf<String, MutableList<String>>()

            links.links.forEach { link ->
                val entityType = link.getEntity(0)
                val confidenceScore = link.getConfidenceScore(entityType)

                if (confidenceScore >= 0.7f) {
                    val extractedText = text.substring(link.start, link.end)
                    if (!result.containsKey(entityType)) {
                        result[entityType] = mutableListOf()
                    }
                    result[entityType]?.add(extractedText)
                }
            }
            result
        }
    }
}
