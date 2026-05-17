package com.corphish.quicktools.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsRepositoryTest {

    private lateinit var repository: SettingsRepository
    private val context: Context = mockk()
    private val sharedPreferences: SharedPreferences = mockk()
    private val editor: SharedPreferences.Editor = mockk(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(PreferenceManager::class)
        every { PreferenceManager.getDefaultSharedPreferences(context) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        
        repository = SettingsRepository(context)
    }

    @After
    fun tearDown() {
        unmockkStatic(PreferenceManager::class)
    }

    @Test
    fun testPrependCountryCodeEnabled() {
        every { sharedPreferences.getBoolean("prepend_country_code_enabled", false) } returns true
        assertEquals(true, repository.getPrependCountryCodeEnabled())

        repository.setPrependCountryCodeEnabled(true)
        verify { editor.putBoolean("prepend_country_code_enabled", true) }
    }

    @Test
    fun testPrependCountryCode() {
        every { sharedPreferences.getString("prepend_country_code", "") } returns "+91"
        assertEquals("+91", repository.getPrependCountryCode())

        repository.setPrependCountryCode("+1")
        verify { editor.putString("prepend_country_code", "+1") }
    }

    @Test
    fun testDecimalPoints() {
        every { sharedPreferences.getInt("decimal_points", 2) } returns 3
        assertEquals(3, repository.getDecimalPoints())

        repository.setDecimalPoints(4)
        verify { editor.putInt("decimal_points", 4) }
    }

    @Test
    fun testEvaluateResultMode() {
        every { sharedPreferences.getInt("eval_result_mode", 0) } returns 1
        assertEquals(1, repository.getEvaluateResultMode())

        repository.setEvaluateResultMode(2)
        verify { editor.putInt("eval_result_mode", 2) }
    }

    @Test
    fun testOnboardingDone() {
        every { sharedPreferences.getBoolean("onboarding_done", false) } returns true
        assertEquals(true, repository.getOnboardingDone())

        repository.setOnboardingDone(true)
        verify { editor.putBoolean("onboarding_done", true) }
    }
}
