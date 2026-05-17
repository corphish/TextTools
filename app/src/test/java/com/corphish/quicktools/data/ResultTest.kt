package com.corphish.quicktools.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ResultTest {

    @Test
    fun testResultTypes() {
        val success = Result.Success("data")
        assertTrue(success is Result.Success)
        assertEquals("data", success.value)

        val error = Result.Error
        assertTrue(error is Result.Error)

        val initial = Result.Initial
        assertTrue(initial is Result.Initial)
    }
}
