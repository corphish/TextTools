package com.corphish.quicktools.usecases

import com.corphish.quicktools.functions.ParsedTextAction
import com.corphish.quicktools.repository.TextActionRepository
import javax.inject.Inject

class TextActionDeterminationUseCase @Inject constructor(
    private val textActionRepository: TextActionRepository
) {
    suspend fun buildTextActions(
        text: String,
        canBeApplied: Boolean = true
    ) = textActionRepository.buildTextActions(text, canBeApplied)

    fun performAction(action: ParsedTextAction) =
        textActionRepository.performTextAction(action)
}