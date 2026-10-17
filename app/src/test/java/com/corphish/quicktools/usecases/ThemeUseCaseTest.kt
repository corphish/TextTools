package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.Appearance
import com.corphish.quicktools.repository.ThemeRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class ThemeUseCaseTest {

    private val themeRepository: ThemeRepository = mockk(relaxed = true)
    private val appearance = MutableStateFlow(Appearance.DARK)
    private val sessionHue = MutableStateFlow(90f)
    private lateinit var themeUseCase: ThemeUseCase

    @Before
    fun setUp() {
        every { themeRepository.appearance } returns appearance
        every { themeRepository.sessionHue } returns sessionHue
        themeUseCase = ThemeUseCase(themeRepository)
    }

    @Test
    fun testAppearance() {
        assertSame(appearance, themeUseCase.appearance)
    }

    @Test
    fun testSetAppearance() {
        themeUseCase.setAppearance(Appearance.LIGHT)
        verify { themeRepository.setAppearance(Appearance.LIGHT) }
    }

    @Test
    fun testSessionHue() {
        assertSame(sessionHue, themeUseCase.sessionHue)
    }

    @Test
    fun testSetSessionHue() {
        themeUseCase.setSessionHue(200f)
        verify { themeRepository.setSessionHue(200f) }
    }

    @Test
    fun testNewToolHue() {
        every { themeRepository.randomHue() } returns 15f
        assertEquals(15f, themeUseCase.newToolHue(), 0f)
    }
}
