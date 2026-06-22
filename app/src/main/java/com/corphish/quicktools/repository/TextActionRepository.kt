package com.corphish.quicktools.repository

import com.corphish.quicktools.functions.ParsedTextAction

interface TextActionRepository {
    /**
     * Builds terminal actions that can be performed on the text by the android system.
     * The text passed is usually a result of some actions performed by the app which is to
     * be passed back to Android system for the desired action.
     *
     * @param text Processed text.
     * @param canBeApplied Whether this text result can be applied as part of PROCESS_TEXT intent.
     * @return List of applicable text actions.
     */
    suspend fun buildTextActions(
        text: String,
        canBeApplied: Boolean
    ): List<ParsedTextAction>

    /**
     * Performs a given text action.
     * This forwards the result to Android system based on the action.
     * @return True if action was successful, false otherwise.
     */
    fun performTextAction(action: ParsedTextAction): Boolean
}