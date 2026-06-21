package com.corphish.quicktools.functions

import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

@Singleton
class NumberFunctions @Inject constructor() {

    private val base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    fun determineBase(text: String): Pair<Int, Boolean>? {
        if (text.isEmpty()) return null
        
        val dotCount = text.count { it == '.' }
        if (dotCount > 1) return null
        
        val isFloatingPoint = dotCount == 1
        val parts = text.split(".")
        val whole = parts[0]
        val fractional = if (isFloatingPoint) parts[1] else ""
        
        val fullText = (whole + fractional).filter { it != '-' }
        if (fullText.isEmpty()) return null

        // 1. All digits -> Base 10
        if (fullText.all { it.isDigit() }) {
            return Pair(10, isFloatingPoint)
        }

        // 2. Hexadecimal (lowercase/uppercase)
        if (fullText.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }) {
            return Pair(16, isFloatingPoint)
        }

        // 3. Base 36 or 62
        if (fullText.all { it.isDigit() || it in 'A'..'Z' }) {
            return Pair(36, isFloatingPoint)
        }
        
        if (fullText.all { it.isDigit() || it in 'A'..'Z' || it in 'a'..'z' }) {
            return Pair(62, isFloatingPoint)
        }

        return null // Other symbols or invalid
    }

    fun getSupportedBases(text: String): List<Int> {
        val bases = listOf(2, 3, 4, 5, 6, 7, 8, 9, 10, 16, 36, 62)
        val parts = text.split(".")
        val fullText = parts.joinToString("").filter { it != '-' }
        
        return bases.filter { base ->
            fullText.all { char ->
                if (base <= 36) {
                    Character.digit(char, base) != -1
                } else {
                    val index = base62Chars.indexOf(char)
                    index != -1 && index < base
                }
            }
        }
    }

    fun parseToBigInteger(text: String, base: Int): BigInteger {
        val isNegative = text.startsWith("-")
        val cleanText = if (isNegative) text.substring(1) else text
        if (base <= 36) {
            val bi = BigInteger(cleanText, base)
            return if (isNegative) bi.negate() else bi
        }
        // Base 62
        var result = BigInteger.ZERO
        val multiplier = BigInteger.valueOf(62)
        for (char in cleanText) {
            val value = base62Chars.indexOf(char)
            result = result.multiply(multiplier).add(BigInteger.valueOf(value.toLong()))
        }
        return if (isNegative) result.negate() else result
    }

    fun bigIntegerToString(value: BigInteger, base: Int): String {
        val isNegative = value < BigInteger.ZERO
        val absValue = value.abs()
        if (base <= 36) {
            val s = absValue.toString(base).let { if (base == 16 || base == 10) it.lowercase() else it.uppercase() }
            return if (isNegative) "-$s" else s
        }
        // Base 62
        if (absValue == BigInteger.ZERO) return "0"
        var temp = absValue
        val sb = StringBuilder()
        val divisor = BigInteger.valueOf(62)
        while (temp > BigInteger.ZERO) {
            val remainder = temp.remainder(divisor)
            sb.append(base62Chars[remainder.toInt()])
            temp = temp.divide(divisor)
        }
        val s = sb.reverse().toString()
        return if (isNegative) "-$s" else s
    }

    fun parseToBigDecimal(text: String, base: Int): BigDecimal {
        val isNegative = text.startsWith("-")
        val cleanText = if (isNegative) text.substring(1) else text
        
        if (base == 10) return BigDecimal(text)
        
        val parts = cleanText.split(".")
        val wholePart = parseToBigInteger(parts[0], base)
        var result = BigDecimal(wholePart)
        
        if (parts.size > 1) {
            val fractionalPart = parts[1]
            var fracValue = BigDecimal.ZERO
            val baseBD = BigDecimal.valueOf(base.toLong())
            var divisor = baseBD
            for (char in fractionalPart) {
                val digitValue = base62Chars.indexOf(char)
                fracValue = fracValue.add(BigDecimal.valueOf(digitValue.toLong()).divide(divisor, MathContext.DECIMAL128))
                divisor = divisor.multiply(baseBD)
            }
            result = result.add(fracValue)
        }
        return if (isNegative) result.negate() else result
    }

    fun bigDecimalToString(value: BigDecimal, base: Int, precision: Int = 10): String {
        if (base == 10) return value.setScale(precision, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
        
        val isNegative = value.signum() == -1
        val absValue = value.abs()
        
        val wholePart = absValue.toBigInteger()
        var fractionalPart = absValue.subtract(BigDecimal(wholePart))
        
        val sb = StringBuilder(bigIntegerToString(wholePart, base))
        if (fractionalPart.signum() != 0) {
            sb.append(".")
            val baseBD = BigDecimal.valueOf(base.toLong())
            repeat(precision) {
                if (fractionalPart.signum() == 0) return@repeat
                val product = fractionalPart.multiply(baseBD)
                val digit = product.toBigInteger()
                sb.append(base62Chars[digit.toInt()])
                fractionalPart = product.subtract(BigDecimal(digit))
            }
        }
        val s = sb.toString()
        return if (isNegative) "-$s" else s
    }

    // Mathematical Transformations
    fun square(n: BigInteger): BigInteger = n.multiply(n)
    fun cube(n: BigInteger): BigInteger = n.multiply(n).multiply(n)
    fun sqrtBI(n: BigInteger): BigInteger {
        if (n < BigInteger.ZERO) return BigInteger.ZERO
        if (n < BigInteger.valueOf(2)) return n
        var y = n.divide(BigInteger.valueOf(2)).add(BigInteger.valueOf(1))
        var x = n.divide(y).add(y).divide(BigInteger.valueOf(2))
        while (x < y) {
            y = x
            x = n.divide(y).add(y).divide(BigInteger.valueOf(2))
        }
        return y
    }
    fun cbrtBI(n: BigInteger): BigInteger {
        if (n == BigInteger.ZERO) return BigInteger.ZERO
        val isNegative = n < BigInteger.ZERO
        val absN = n.abs()
        var low = BigInteger.ZERO
        var high = absN
        var ans = BigInteger.ZERO
        while (low <= high) {
            val mid = low.add(high).shiftRight(1)
            if (mid == BigInteger.ZERO) {
                low = BigInteger.ONE
                continue
            }
            val midCube = mid.multiply(mid).multiply(mid)
            if (midCube == absN) {
                ans = mid
                break
            }
            if (midCube < absN) {
                ans = mid
                low = mid.add(BigInteger.ONE)
            } else {
                high = mid.subtract(BigInteger.ONE)
            }
        }
        return if (isNegative) ans.negate() else ans
    }
    
    fun factorial(n: Int): BigInteger {
        if (n < 0) return BigInteger.ZERO
        var res = BigInteger.ONE
        for (i in 2..n) res = res.multiply(BigInteger.valueOf(i.toLong()))
        return res
    }

    // Floating point math
    fun square(n: BigDecimal): BigDecimal = n.multiply(n)
    fun cube(n: BigDecimal): BigDecimal = n.multiply(n).multiply(n)
    fun sqrtBD(n: BigDecimal): BigDecimal {
        if (n.signum() == -1) return BigDecimal.ZERO
        return BigDecimal(sqrt(n.toDouble()))
    }
    fun cbrtBD(n: BigDecimal): BigDecimal {
        return BigDecimal(n.toDouble().pow(1.0/3.0))
    }
    fun log(n: BigDecimal): BigDecimal = BigDecimal(log10(n.toDouble()))
    fun ln(n: BigDecimal): BigDecimal = BigDecimal(ln(n.toDouble()))
    fun exp(n: BigDecimal): BigDecimal = BigDecimal(exp(n.toDouble()))
    fun abs(n: BigDecimal): BigDecimal = n.abs()
    fun abs(n: BigInteger): BigInteger = n.abs()

    fun gamma(n: Double): Double {
        // Lanczos approximation
        val g = 7
        val p = doubleArrayOf(
            0.99999999999980993, 676.5203681218851, -1259.1392167224028,
            771.32342877765313, -176.61502916214059, 12.507343278686905,
            -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7
        )
        var z = n
        if (z < 0.5) return PI / (sin(PI * z) * gamma(1.0 - z))
        z -= 1.0
        var x = p[0]
        for (i in 1 until g + 2) x += p[i] / (z + i)
        val t = z + g + 0.5
        return sqrt(2.0 * PI) * t.pow(z + 0.5) * exp(-t) * x
    }

    // Digit based derivations
    fun sumOfDigits(n: BigInteger): BigInteger {
        return n.abs().toString().filter { it.isDigit() }.sumOf { it.digitToInt() }.toBigInteger()
    }
    
    fun productOfDigits(n: BigInteger): BigInteger {
        val s = n.abs().toString().filter { it.isDigit() }
        if (s.isEmpty()) return BigInteger.ZERO
        var res = BigInteger.ONE
        for (c in s) res = res.multiply(BigInteger.valueOf(c.digitToInt().toLong()))
        return res
    }

    fun productOfDigitsExceptZero(n: BigInteger): BigInteger {
        val s = n.abs().toString().filter { it.isDigit() && it != '0' }
        if (s.isEmpty()) return BigInteger.ZERO
        var res = BigInteger.ONE
        for (c in s) res = res.multiply(BigInteger.valueOf(c.digitToInt().toLong()))
        return res
    }

    fun reverseDigits(n: BigInteger): BigInteger {
        val s = n.abs().toString()
        val r = s.reversed()
        return BigInteger(r).let { if (n < BigInteger.ZERO) it.negate() else it }
    }

    fun digitFrequency(n: BigInteger): Map<Char, Int> {
        return n.abs().toString().filter { it.isDigit() }.groupingBy { it }.eachCount()
    }

    fun digitalRoot(n: BigInteger): Int {
        if (n == BigInteger.ZERO) return 0
        val sum = sumOfDigits(n)
        return if (sum < BigInteger.TEN) sum.toInt() else digitalRoot(sum)
    }

    // Factors
    fun getDivisors(n: BigInteger): List<BigInteger> {
        if (n.abs() > BigInteger.valueOf(1000000)) return listOf()
        val limit = n.abs().toLong()
        val divisors = mutableListOf<BigInteger>()
        for (i in 1..sqrt(limit.toDouble()).toLong()) {
            if (limit % i == 0L) {
                divisors.add(BigInteger.valueOf(i))
                if (i * i != limit) {
                    divisors.add(BigInteger.valueOf(limit / i))
                }
            }
        }
        return divisors.sorted()
    }

    fun getPrimeFactors(n: BigInteger): List<BigInteger> {
        val absN = n.abs()
        if (absN <= BigInteger.ONE) return emptyList()
        var temp = absN
        val factors = mutableListOf<BigInteger>()
        var d = BigInteger.valueOf(2)
        while (d.multiply(d) <= temp) {
            while (temp.remainder(d) == BigInteger.ZERO) {
                factors.add(d)
                temp = temp.divide(d)
            }
            d = d.add(BigInteger.ONE)
        }
        if (temp > BigInteger.ONE) factors.add(temp)
        return factors
    }

    // Primes
    fun isPrime(n: BigInteger): Boolean = n.isProbablePrime(10)
    
    fun nextPrime(n: BigInteger): BigInteger = n.nextProbablePrime()
    
    fun prevPrime(n: BigInteger): BigInteger {
        var p = n.subtract(BigInteger.ONE)
        while (p > BigInteger.ONE && !p.isProbablePrime(10)) {
            p = p.subtract(BigInteger.ONE)
        }
        return if (p <= BigInteger.ONE) BigInteger.ZERO else p
    }

    // Language
    fun numberToWords(n: BigInteger): String {
        if (n == BigInteger.ZERO) return "zero"
        val isNegative = n < BigInteger.ZERO
        var temp = n.abs()
        val units = arrayOf("", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen")
        val tens = arrayOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")

        fun convertLessThanThousand(num: Int): String {
            var res = ""
            if (num >= 100) {
                res += units[num / 100] + " hundred "
                val rem = num % 100
                if (rem > 0) res += "and " + convertLessThanThousand(rem)
            } else if (num >= 20) {
                res += tens[num / 10]
                if (num % 10 > 0) res += "-" + units[num % 10]
            } else if (num > 0) {
                res += units[num]
            }
            return res.trim()
        }

        val chunks = mutableListOf<Int>()
        val thousand = BigInteger.valueOf(1000)
        while (temp > BigInteger.ZERO) {
            chunks.add(temp.remainder(thousand).toInt())
            temp = temp.divide(thousand)
        }

        val scales = arrayOf("", "thousand", "million", "billion", "trillion", "quadrillion", "quintillion")
        var result = ""
        for (i in chunks.indices.reversed()) {
            if (chunks[i] > 0) {
                result += convertLessThanThousand(chunks[i]) + " " + scales[i] + " "
            }
        }
        val finalStr = result.trim()
        return if (isNegative) "minus $finalStr" else finalStr
    }

    fun numberToRoman(n: BigInteger): String {
        if (n < BigInteger.ONE || n > BigInteger.valueOf(3999)) return "N/A"
        var num = n.toInt()
        val values = intArrayOf(1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1)
        val romanLetters = arrayOf("M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
        val roman = StringBuilder()
        for (i in values.indices) {
            while (num >= values[i]) {
                num -= values[i]
                roman.append(romanLetters[i])
            }
        }
        return roman.toString()
    }

    fun numberToOrdinal(n: BigInteger): String {
        val s = n.toString()
        if (s.endsWith("11") || s.endsWith("12") || s.endsWith("13")) return s + "th"
        return when (s.last()) {
            '1' -> s + "st"
            '2' -> s + "nd"
            '3' -> s + "rd"
            else -> s + "th"
        }
    }
}
