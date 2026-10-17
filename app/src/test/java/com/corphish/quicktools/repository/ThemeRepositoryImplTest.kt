package com.corphish.quicktools.repository

import android.content.Context
import android.content.SharedPreferences
import com.corphish.quicktools.data.Appearance
import com.corphish.quicktools.functions.ThemeFunctions
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ThemeRepositoryImplTest {

    private val context: Context = mockk()
    private val sharedPreferences: SharedPreferences = mockk()
    private val editor: SharedPreferences.Editor = mockk(relaxed = true)
    private val themeFunctions: ThemeFunctions = mockk()

    @Before
    fun setUp() {
        every { context.packageName } returns "com.corphish.quicktools"
        every { context.getSharedPreferences("com.corphish.quicktools_preferences", Context.MODE_PRIVATE) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        every { themeFunctions.randomHue(any()) } returnsMany listOf(120f, 300f)
    }

    private fun createRepository(storedAppearance: String?): ThemeRepositoryImpl {
        every { sharedPreferences.getString("appearance", null) } returns storedAppearance
        return ThemeRepositoryImpl(context, themeFunctions)
    }

    @Test
    fun testAppearance_DefaultsToSystemWhenUnset() {
        assertEquals(Appearance.SYSTEM, createRepository(null).appearance.value)
    }

    @Test
    fun testAppearance_ReadsStoredValue() {
        assertEquals(Appearance.DARK, createRepository("DARK").appearance.value)
    }

    @Test
    fun testAppearance_FallsBackOnUnknownValue() {
        assertEquals(Appearance.SYSTEM, createRepository("SEPIA").appearance.value)
    }

    @Test
    fun testSetAppearance_PersistsAndPublishes() {
        val repository = createRepository(null)
        repository.setAppearance(Appearance.LIGHT)

        verify { editor.putString("appearance", "LIGHT") }
        assertEquals(Appearance.LIGHT, repository.appearance.value)
    }

    @Test
    fun testSessionHue_StartsRandom() {
        assertEquals(120f, createRepository(null).sessionHue.value, 0f)
    }

    @Test
    fun testSetSessionHue() {
        val repository = createRepository(null)
        repository.setSessionHue(45f)
        assertEquals(45f, repository.sessionHue.value, 0f)
    }

    @Test
    fun testRandomHue_DoesNotChangeSessionHue() {
        val repository = createRepository(null)
        assertEquals(300f, repository.randomHue(), 0f)
        assertEquals(120f, repository.sessionHue.value, 0f)
    }
}
