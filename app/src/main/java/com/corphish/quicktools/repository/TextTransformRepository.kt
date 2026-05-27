package com.corphish.quicktools.repository

/**
 * Repository interface for text transformations.
 */
interface TextTransformRepository {
    /**
     * Transforms the given text based on the primary and secondary indices.
     * @param text Original text.
     * @param primaryIndex Primary transformation index.
     * @param secondaryIndex Secondary transformation index.
     * @param secondaryText Optional secondary text for transformation.
     * @return Transformed text.
     */
    fun transform(
        text: String,
        primaryIndex: Int,
        secondaryIndex: Int,
        secondaryText: String
    ): String
}
