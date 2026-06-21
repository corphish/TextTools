package com.corphish.quicktools.functions

import java.math.BigDecimal
import java.math.BigInteger
import org.junit.Assert.*
import org.junit.Test

class NumberFunctionsTest {

    private val numberFunctions = NumberFunctions()

    @Test
    fun testDetermineBase() {
        // Base 10
        assertEquals(Pair(10, false), numberFunctions.determineBase("123"))
        assertEquals(Pair(10, true), numberFunctions.determineBase("123.45"))
        assertEquals(Pair(10, false), numberFunctions.determineBase("-123"))
        
        // Hex
        assertEquals(Pair(16, false), numberFunctions.determineBase("abc"))
        assertEquals(Pair(16, false), numberFunctions.determineBase("ABC"))
        assertEquals(Pair(16, true), numberFunctions.determineBase("a.f"))
        
        // Base 36
        assertEquals(Pair(36, false), numberFunctions.determineBase("GHI"))
        
        // Base 62
        assertEquals(Pair(62, false), numberFunctions.determineBase("ghi"))
        
        // Invalid
        assertNull(numberFunctions.determineBase(""))
        assertNull(numberFunctions.determineBase("1.2.3"))
        assertNull(numberFunctions.determineBase("!@#"))
    }

    @Test
    fun testGetSupportedBases() {
        val bases = numberFunctions.getSupportedBases("101")
        assertTrue(bases.contains(2))
        assertTrue(bases.contains(10))
        assertTrue(bases.contains(16))
        assertTrue(bases.contains(62))
        
        val hexBases = numberFunctions.getSupportedBases("abc")
        assertFalse(hexBases.contains(10))
        assertTrue(hexBases.contains(16))
        assertTrue(hexBases.contains(36))
        assertTrue(hexBases.contains(62))
    }

    @Test
    fun testBigIntegerConversions() {
        val bi = BigInteger.valueOf(255)
        assertEquals("255", numberFunctions.bigIntegerToString(bi, 10))
        assertEquals("ff", numberFunctions.bigIntegerToString(bi, 16))
        assertEquals("11111111", numberFunctions.bigIntegerToString(bi, 2))
        
        assertEquals(bi, numberFunctions.parseToBigInteger("255", 10))
        assertEquals(bi, numberFunctions.parseToBigInteger("ff", 16))
        assertEquals(bi, numberFunctions.parseToBigInteger("11111111", 2))
        
        // Base 62
        // base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        // 'w' -> 36 + 22 = 58
        // 'Z' -> 35
        // 58 * 62 + 35 = 3596 + 35 = 3631
        val b62Val = "wZ" 
        val bi62 = BigInteger.valueOf(3631)
        assertEquals(b62Val, numberFunctions.bigIntegerToString(bi62, 62))
        assertEquals(bi62, numberFunctions.parseToBigInteger(b62Val, 62))
    }

    @Test
    fun testBigDecimalConversions() {
        val bd = BigDecimal("123.45")
        assertEquals("123.45", numberFunctions.bigDecimalToString(bd, 10))
        
        val parsed = numberFunctions.parseToBigDecimal("123.45", 10)
        assertEquals(0, bd.compareTo(parsed))
        
        // Base 16 conversion check
        val hexStr = numberFunctions.bigDecimalToString(bd, 16, 2)
        val parsedHex = numberFunctions.parseToBigDecimal(hexStr, 16)
        // Since precision is 2, there might be slight difference
        assertTrue(bd.subtract(parsedHex).abs() < BigDecimal("0.1"))
    }

    @Test
    fun testMathTransformations() {
        val bi = BigInteger.valueOf(4)
        assertEquals(BigInteger.valueOf(16), numberFunctions.square(bi))
        assertEquals(BigInteger.valueOf(64), numberFunctions.cube(bi))
        // Note: NumberFunctions uses high-precision BD for roots now even for BI path
        // But for test purposes we can still check properties
    }

    @Test
    fun testDigitDerivations() {
        val bi = BigInteger.valueOf(123)
        assertEquals(BigInteger.valueOf(6), numberFunctions.sumOfDigits(bi))
        assertEquals(BigInteger.valueOf(6), numberFunctions.productOfDigits(bi))
        assertEquals(BigInteger.valueOf(321), numberFunctions.reverseDigits(bi))
        assertEquals(6, numberFunctions.digitalRoot(bi))
        
        val freq = numberFunctions.digitFrequency(bi)
        assertEquals(1, freq['1'] ?: 0)
        assertEquals(1, freq['2'] ?: 0)
        assertEquals(1, freq['3'] ?: 0)
    }

    @Test
    fun testFactors() {
        val bi = BigInteger.valueOf(12)
        val divisors = numberFunctions.getDivisors(bi)
        assertEquals(listOf(1, 2, 3, 4, 6, 12).map { BigInteger.valueOf(it.toLong()) }, divisors)
        
        val primeFactors = numberFunctions.getPrimeFactors(bi)
        assertEquals(listOf(2, 2, 3).map { BigInteger.valueOf(it.toLong()) }, primeFactors)
    }

    @Test
    fun testPrimes() {
        assertTrue(numberFunctions.isPrime(BigInteger.valueOf(7)))
        assertFalse(numberFunctions.isPrime(BigInteger.valueOf(8)))
        assertEquals(BigInteger.valueOf(11), numberFunctions.nextPrime(BigInteger.valueOf(7)))
        assertEquals(BigInteger.valueOf(5), numberFunctions.prevPrime(BigInteger.valueOf(7)))
    }

    @Test
    fun testLanguage() {
        assertEquals("one hundred and twenty-three", numberFunctions.numberToWords(BigInteger.valueOf(123)))
        assertEquals("CXXIII", numberFunctions.numberToRoman(BigInteger.valueOf(123)))
        assertEquals("123rd", numberFunctions.numberToOrdinal(BigInteger.valueOf(123)))
    }
}
