package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.Result
import com.corphish.quicktools.repository.EvalRepository
import com.corphish.quicktools.viewmodels.EvalViewModel.Companion.EVAL_RESULT_APPEND
import javax.inject.Inject

/**
 * Use case for evaluating inline mathematical expressions.
 */
class EvalUseCase @Inject constructor(
    private val evalRepository: EvalRepository
) {
    /**
     * Executes the expression evaluation.
     * @return [Result.Success] with the formatted result, or [Result.Error] if the expression is invalid.
     */
    fun execute(expression: String, decimalPoints: Int): Result<String> =
        evalRepository.evaluate(expression, decimalPoints)

    /**
     * Builds the text to write back to the source.
     *
     * @param expression Original selected expression.
     * @param result Evaluated result.
     * @param mode [EVAL_RESULT_APPEND] gives "expression = result"; any other mode gives the result alone.
     */
    fun buildOutput(expression: String, result: String, mode: Int): String =
        if (mode == EVAL_RESULT_APPEND) "$expression = $result" else result
}
