package com.corphish.quicktools.usecases

import com.corphish.quicktools.functions.ContextFunctions
import com.corphish.quicktools.repository.SettingsRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class WhatsappUseCaseTest {

    private lateinit var whatsappUseCase: WhatsappUseCase
    private val contextFunctions: ContextFunctions = mockk(relaxed = true)
    private val settingsRepository: SettingsRepository = mockk()

    @Before
    fun setUp() {
        whatsappUseCase = WhatsappUseCase(contextFunctions, settingsRepository)
    }

    @Test
    fun testOpenInWeb() {
        val phoneNumber = "1234567890"
        whatsappUseCase.openInWeb(phoneNumber)
        verify { contextFunctions.openInWeb("https://wa.me/1234567890") }
    }

    @Test
    fun testDeterminePhoneNumber_Valid() {
        every { settingsRepository.getPrependCountryCodeEnabled() } returns false
        
        // Basic number
        assertEquals("1234567890", whatsappUseCase.determinePhoneNumber("1234567890"))
        
        // With special characters
        assertEquals("1234567890", whatsappUseCase.determinePhoneNumber("123-456-7890"))
        assertEquals("1234567890", whatsappUseCase.determinePhoneNumber("(123) 456 7890"))
    }

    @Test
    fun testDeterminePhoneNumber_Invalid() {
        assertNull(whatsappUseCase.determinePhoneNumber(null))
        assertNull(whatsappUseCase.determinePhoneNumber("not a number"))
        assertNull(whatsappUseCase.determinePhoneNumber("12345")) // too short
    }

    @Test
    fun testCountryCodedNumber_Enabled() {
        every { settingsRepository.getPrependCountryCodeEnabled() } returns true
        every { settingsRepository.getPrependCountryCode() } returns "+91"
        
        // Should prepend if missing
        assertEquals("+911234567890", whatsappUseCase.determinePhoneNumber("1234567890"))
        
        // Should not prepend if already present
        assertEquals("+911234567890", whatsappUseCase.determinePhoneNumber("+911234567890"))
        
        // Should not prepend if another country code is present
        assertEquals("+11234567890", whatsappUseCase.determinePhoneNumber("+11234567890"))
    }

    @Test
    fun testCountryCodedNumber_Disabled() {
        every { settingsRepository.getPrependCountryCodeEnabled() } returns false
        assertEquals("1234567890", whatsappUseCase.determinePhoneNumber("1234567890"))
    }
}
