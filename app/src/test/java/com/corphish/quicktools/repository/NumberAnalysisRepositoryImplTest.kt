package com.corphish.quicktools.repository

import com.corphish.quicktools.functions.NumberFunctions
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.math.BigInteger
import java.math.BigDecimal

class NumberAnalysisRepositoryImplTest {

    private lateinit var repository: NumberAnalysisRepositoryImpl
    private val numberFunctions: NumberFunctions = mockk()

    @Before
    fun setUp() {
        repository = NumberAnalysisRepositoryImpl(numberFunctions)
    }

    @Test
    fun testAnalyze_Basic() {
        val input = "10"
        every { numberFunctions.determineBase(input) } returns Pair(10, false)
        every { numberFunctions.getSupportedBases(input) } returns listOf(2, 8, 10, 16)
        every { numberFunctions.parseToBigInteger(input, 10) } returns BigInteger.TEN
        every { numberFunctions.bigIntegerToString(BigInteger.TEN, 2) } returns "1010"
        every { numberFunctions.bigIntegerToString(BigInteger.TEN, 8) } returns "12"
        every { numberFunctions.bigIntegerToString(BigInteger.TEN, 10) } returns "10"
        every { numberFunctions.bigIntegerToString(BigInteger.TEN, 16) } returns "a"
        
        // Mock math transformations
        every { numberFunctions.square(any<BigInteger>()) } returns BigInteger.valueOf(100)
        every { numberFunctions.cube(any<BigInteger>()) } returns BigInteger.valueOf(1000)
        every { numberFunctions.sqrtBI(any<BigInteger>()) } returns BigInteger.valueOf(3)
        every { numberFunctions.cbrtBI(any<BigInteger>()) } returns BigInteger.valueOf(2)
        every { numberFunctions.abs(any<BigInteger>()) } returns BigInteger.TEN
        every { numberFunctions.factorial(any<Int>()) } returns BigInteger.valueOf(3628800)
        every { numberFunctions.log(any<BigDecimal>()) } returns BigDecimal.ONE
        every { numberFunctions.ln(any<BigDecimal>()) } returns BigDecimal.ONE
        every { numberFunctions.exp(any<BigDecimal>()) } returns BigDecimal.ONE

        // Mock digit derivations
        every { numberFunctions.sumOfDigits(any<BigInteger>()) } returns BigInteger.ONE
        every { numberFunctions.productOfDigits(any<BigInteger>()) } returns BigInteger.ZERO
        every { numberFunctions.productOfDigitsExceptZero(any<BigInteger>()) } returns BigInteger.ONE
        every { numberFunctions.reverseDigits(any<BigInteger>()) } returns BigInteger.ONE
        every { numberFunctions.digitFrequency(any<BigInteger>()) } returns mapOf('1' to 1, '0' to 1)
        every { numberFunctions.digitalRoot(any<BigInteger>()) } returns 1
        
        // Mock factors
        every { numberFunctions.getPrimeFactors(any<BigInteger>()) } returns listOf(BigInteger.valueOf(2), BigInteger.valueOf(5))
        every { numberFunctions.getDivisors(any<BigInteger>()) } returns listOf(BigInteger.ONE, BigInteger.valueOf(2), BigInteger.valueOf(5), BigInteger.TEN)
        
        // Mock primes
        every { numberFunctions.isPrime(any<BigInteger>()) } returns false
        every { numberFunctions.nextPrime(any<BigInteger>()) } returns BigInteger.valueOf(11)
        every { numberFunctions.prevPrime(any<BigInteger>()) } returns BigInteger.valueOf(7)
        
        // Mock language
        every { numberFunctions.numberToWords(any<BigInteger>()) } returns "ten"
        every { numberFunctions.numberToOrdinal(any<BigInteger>()) } returns "10th"
        every { numberFunctions.numberToRoman(any<BigInteger>()) } returns "X"

        val result = repository.analyze(input, null, 10)
        
        assertNotNull(result)
        assertEquals(input, result!!.input)
        assertEquals(10, result.base)
        assertFalse(result.isFloatingPoint)
        assertEquals(listOf(2, 8, 10, 16), result.supportedBases)
    }

    @Test
    fun testAnalyze_Invalid() {
        every { numberFunctions.determineBase(any()) } returns null
        val result = repository.analyze("invalid", null, 10)
        assertNull(result)
    }
}
