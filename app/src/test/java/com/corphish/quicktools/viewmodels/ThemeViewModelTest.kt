package com.corphish.quicktools.viewmodels

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import com.corphish.quicktools.data.Appearance
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.usecases.ThemeUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class ThemeViewModelTest {

    private val themeUseCase: ThemeUseCase = mockk(relaxed = true)
    private val appearance = MutableStateFlow(Appearance.LIGHT)
    private val sessionHue = MutableStateFlow(90f)

    @Before
    fun setUp() {
        every { themeUseCase.appearance } returns appearance
        every { themeUseCase.sessionHue } returns sessionHue
        every { themeUseCase.newToolHue() } returns 270f
    }

    private fun appLaunch() = ThemeViewModel(SavedStateHandle(), themeUseCase)

    private fun toolLaunch() =
        ThemeViewModel(SavedStateHandle(mapOf(Intent.EXTRA_PROCESS_TEXT to "text")), themeUseCase)

    @Test
    fun testAppearance() {
        assertSame(appearance, appLaunch().appearance)
    }

    @Test
    fun testAppLaunch_UsesSessionHue() {
        val viewModel = appLaunch()
        assertSame(sessionHue, viewModel.hue)
        verify(exactly = 0) { themeUseCase.newToolHue() }
    }

    @Test
    fun testAppLaunch_HueChangeUpdatesSession() {
        appLaunch().onHueChanged(10f)
        verify { themeUseCase.setSessionHue(10f) }
    }

    @Test
    fun testToolLaunch_UsesNewRandomHue() {
        assertEquals(270f, toolLaunch().hue.value, 0f)
    }

    @Test
    fun testToolLaunch_HueChangeStaysLocal() {
        val viewModel = toolLaunch()
        viewModel.onHueChanged(10f)

        assertEquals(10f, viewModel.hue.value, 0f)
        verify(exactly = 0) { themeUseCase.setSessionHue(any()) }
    }

    @Test
    fun testInheritedHue_UsedForAppLaunchedTool() {
        val viewModel = ThemeViewModel(
            SavedStateHandle(mapOf(Intent.EXTRA_PROCESS_TEXT to "text", Constants.INTENT_HUE to 42f)),
            themeUseCase
        )

        assertEquals(42f, viewModel.hue.value, 0f)
        verify(exactly = 0) { themeUseCase.newToolHue() }
    }

    @Test
    fun testInheritedHue_ChangeStaysLocal() {
        val viewModel = ThemeViewModel(SavedStateHandle(mapOf(Constants.INTENT_HUE to 42f)), themeUseCase)
        viewModel.onHueChanged(10f)

        assertEquals(10f, viewModel.hue.value, 0f)
        verify(exactly = 0) { themeUseCase.setSessionHue(any()) }
    }

    @Test
    fun testInvalidInheritedHue_FallsBackToRandomForTool() {
        val viewModel = ThemeViewModel(
            SavedStateHandle(mapOf(Intent.EXTRA_PROCESS_TEXT to "text", Constants.INTENT_HUE to "red")),
            themeUseCase
        )

        assertEquals(270f, viewModel.hue.value, 0f)
    }

    @Test
    fun testNonFiniteInheritedHue_Ignored() {
        val viewModel = ThemeViewModel(SavedStateHandle(mapOf(Constants.INTENT_HUE to Float.NaN)), themeUseCase)
        assertSame(sessionHue, viewModel.hue)
    }
}
