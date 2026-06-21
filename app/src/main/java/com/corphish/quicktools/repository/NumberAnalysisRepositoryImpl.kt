package com.corphish.quicktools.repository

import com.corphish.quicktools.R
import com.corphish.quicktools.data.NumberAnalysisResult
import com.corphish.quicktools.functions.NumberFunctions
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

import java.util.Locale

class NumberAnalysisRepositoryImpl @Inject constructor(
    private val numberFunctions: NumberFunctions
) : NumberAnalysisRepository {
    override fun analyze(text: String, base: Int?, precision: Int): NumberAnalysisResult? {
        try {
            val determination = numberFunctions.determineBase(text) ?: return null
            val actualBase = base ?: determination.first
            val isFloatingPoint = determination.second

            val supportedBases = numberFunctions.getSupportedBases(text)

            val conversions = supportedBases.map { toBase ->
                val converted = if (isFloatingPoint) {
                    val bd = numberFunctions.parseToBigDecimal(text, actualBase)
                    numberFunctions.bigDecimalToString(bd, toBase, precision)
                } else {
                    val bi = numberFunctions.parseToBigInteger(text, actualBase)
                    numberFunctions.bigIntegerToString(bi, toBase)
                }
                Pair(toBase, converted)
            }

            val mathTransformations = mutableListOf<Pair<Int, String>>()
            val digitDerivations = mutableListOf<Pair<Int, String>>()
            val factors = mutableListOf<Pair<Int, String>>()
            val primeInfo = mutableListOf<Pair<Int, String>>()
            val languageConversions = mutableListOf<Pair<Int, String>>()

            if (isFloatingPoint) {
                val n = numberFunctions.parseToBigDecimal(text, actualBase)

                // Math
                mathTransformations.add(
                    R.string.square to numberFunctions.bigDecimalToString(
                        numberFunctions.square(n),
                        10, precision
                    )
                )
                mathTransformations.add(
                    R.string.cube to numberFunctions.bigDecimalToString(
                        numberFunctions.cube(n),
                        10, precision
                    )
                )
                mathTransformations.add(
                    R.string.square_root to numberFunctions.bigDecimalToString(
                        numberFunctions.sqrtBD(n),
                        10, precision
                    )
                )
                mathTransformations.add(
                    R.string.cube_root to numberFunctions.bigDecimalToString(
                        numberFunctions.cbrtBD(n),
                        10, precision
                    )
                )
                mathTransformations.add(
                    R.string.log to numberFunctions.bigDecimalToString(
                        numberFunctions.log(n),
                        10, precision
                    )
                )
                mathTransformations.add(
                    R.string.ln to numberFunctions.bigDecimalToString(
                        numberFunctions.ln(n),
                        10, precision
                    )
                )
                mathTransformations.add(
                    R.string.exponential to if (n.toDouble() <= 64.0) numberFunctions.bigDecimalToString(
                        numberFunctions.exp(n),
                        10, precision
                    ) else "@string/result_too_large"
                )
                mathTransformations.add(
                    R.string.absolute_value to numberFunctions.bigDecimalToString(
                        numberFunctions.abs(n),
                        10, precision
                    )
                )
                mathTransformations.add(
                    R.string.factorial to if (n.abs() <= BigDecimal.valueOf(32)) String.format(
                        Locale.ENGLISH,
                        "%.${precision}f",
                        numberFunctions.gamma(n.toDouble() + 1.0)
                    ) else "@string/result_too_large"
                )

                // Digit
                val allDigitsStr = text.filter { it.isDigit() }
                val sum = allDigitsStr.sumOf { it.digitToInt() }
                digitDerivations.add(R.string.sum_of_digits to sum.toString())

                var prod = BigInteger.ONE
                var prodNonZero = BigInteger.ONE
                var hasDigits = false
                var hasNonZeroDigits = false
                for (char in allDigitsStr) {
                    val d = char.digitToInt().toLong()
                    prod = prod.multiply(BigInteger.valueOf(d))
                    hasDigits = true
                    if (d != 0L) {
                        prodNonZero = prodNonZero.multiply(BigInteger.valueOf(d))
                        hasNonZeroDigits = true
                    }
                }

                digitDerivations.add(R.string.product_of_digits to (if (hasDigits) prod.toString() else "0"))
                digitDerivations.add(R.string.product_of_digits_except_zero to (if (hasNonZeroDigits) prodNonZero.toString() else "0"))
                digitDerivations.add(R.string.reverse to text.reversed())

                val freq = allDigitsStr.groupingBy { it }.eachCount()
                digitDerivations.add(R.string.digit_frequency to freq.toString())

                val digits = allDigitsStr.map { it.digitToInt() }
                if (digits.isNotEmpty()) {
                    digitDerivations.add(R.string.largest_digit to digits.maxOrNull().toString())
                    digitDerivations.add(R.string.smallest_digit to digits.minOrNull().toString())
                }

                val bi = n.toBigInteger().abs()
                digitDerivations.add(
                    R.string.digital_root to numberFunctions.digitalRoot(bi).toString()
                )

                // Prime
                primeInfo.add(R.string.next_prime to numberFunctions.nextPrime(bi).toString())
                primeInfo.add(R.string.prev_prime to numberFunctions.prevPrime(bi).toString())

                // Language
                languageConversions.add(R.string.number_to_words to numberFunctions.numberToWords(bi))

            } else {
                val n = numberFunctions.parseToBigInteger(text, actualBase)

                // Math
                mathTransformations.add(R.string.square to numberFunctions.square(n).toString())
                mathTransformations.add(R.string.cube to numberFunctions.cube(n).toString())
                mathTransformations.add(
                    R.string.square_root to numberFunctions.bigDecimalToString(
                        numberFunctions.sqrtBD(BigDecimal(n)),
                        10, precision
                    )
                )
                mathTransformations.add(
                    R.string.cube_root to numberFunctions.bigDecimalToString(
                        numberFunctions.cbrtBD(BigDecimal(n)),
                        10, precision
                    )
                )
                mathTransformations.add(
                    R.string.log to numberFunctions.bigDecimalToString(
                        numberFunctions.log(BigDecimal(n)),
                        10, precision
                    )
                )
                mathTransformations.add(
                    R.string.ln to numberFunctions.bigDecimalToString(
                        numberFunctions.ln(BigDecimal(n)),
                        10, precision
                    )
                )
                mathTransformations.add(
                    R.string.exponential to if (n.toDouble() <= 64.0) numberFunctions.bigDecimalToString(
                        numberFunctions.exp(BigDecimal(n)),
                        10, precision
                    ) else "@string/result_too_large"
                )
                mathTransformations.add(
                    R.string.absolute_value to numberFunctions.abs(n).toString()
                )
                mathTransformations.add(
                    R.string.factorial to if (n.abs() <= BigInteger.valueOf(32)) numberFunctions.factorial(
                        n.abs().toInt()
                    ).toString() else "@string/result_too_large"
                )

                // Digit
                digitDerivations.add(
                    R.string.sum_of_digits to numberFunctions.sumOfDigits(n).toString()
                )
                digitDerivations.add(
                    R.string.product_of_digits to numberFunctions.productOfDigits(n).toString()
                )
                digitDerivations.add(
                    R.string.product_of_digits_except_zero to numberFunctions.productOfDigitsExceptZero(
                        n
                    ).toString()
                )
                digitDerivations.add(
                    R.string.reverse to numberFunctions.reverseDigits(n).toString()
                )
                digitDerivations.add(
                    R.string.digit_frequency to numberFunctions.digitFrequency(n).toString()
                )
                val digits = n.abs().toString().filter { it.isDigit() }.map { it.digitToInt() }
                if (digits.isNotEmpty()) {
                    digitDerivations.add(R.string.largest_digit to digits.maxOrNull().toString())
                    digitDerivations.add(R.string.smallest_digit to digits.minOrNull().toString())
                }
                digitDerivations.add(
                    R.string.digital_root to numberFunctions.digitalRoot(n).toString()
                )

                // Factors
                factors.add(
                    R.string.prime_factors to numberFunctions.getPrimeFactors(n).joinToString(", ")
                )
                factors.add(R.string.divisors to numberFunctions.getDivisors(n).joinToString(", "))

                // Prime
                primeInfo.add(R.string.is_prime to (if (numberFunctions.isPrime(n)) "@string/yes" else "@string/no"))
                primeInfo.add(R.string.next_prime to numberFunctions.nextPrime(n).toString())
                primeInfo.add(R.string.prev_prime to numberFunctions.prevPrime(n).toString())

                // Language
                languageConversions.add(R.string.number_to_words to numberFunctions.numberToWords(n))
                languageConversions.add(R.string.ordinal to numberFunctions.numberToOrdinal(n))
                languageConversions.add(R.string.roman_numeral to numberFunctions.numberToRoman(n))

            }

            return NumberAnalysisResult(
                input = text,
                base = actualBase,
                isFloatingPoint = isFloatingPoint,
                supportedBases = supportedBases,
                conversions = conversions,
                mathTransformations = mathTransformations,
                digitDerivations = digitDerivations,
                factors = factors,
                primeInfo = primeInfo,
                languageConversions = languageConversions
            )
        } catch (e: NumberFormatException) {
            return null
        }
    }
}
