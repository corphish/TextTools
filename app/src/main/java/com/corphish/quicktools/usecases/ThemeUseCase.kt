package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.Appearance
import com.corphish.quicktools.repository.ThemeRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Resolves the theme inputs for a screen.
 *
 * Hue rules:
 * - The app's own screens (main, settings, on-boarding, simulation) share one session hue.
 * - Every tool launched with text to process gets its own random hue.
 */
class ThemeUseCase @Inject constructor(
    private val themeRepository: ThemeRepository
) {
    /** The user's appearance choice. */
    val appearance: StateFlow<Appearance>
        get() = themeRepository.appearance

    /** Persists the user's appearance choice. */
    fun setAppearance(appearance: Appearance) {
        themeRepository.setAppearance(appearance)
    }

    /** Hue shared by the app's own screens for this session. */
    val sessionHue: StateFlow<Float>
        get() = themeRepository.sessionHue

    /** Updates the session hue, e.g. after the user shuffles colors. */
    fun setSessionHue(degrees: Float) {
        themeRepository.setSessionHue(degrees)
    }

    /** Returns a fresh random hue for a newly launched tool. */
    fun newToolHue(): Float = themeRepository.randomHue()
}
