package com.corphish.quicktools.repository

import com.corphish.quicktools.data.Appearance
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository for the app's theme state: the persisted [Appearance] and the in-memory hue shared
 * by every screen of the current app session.
 */
interface ThemeRepository {
    /** The user's appearance choice, persisted across launches. */
    val appearance: StateFlow<Appearance>

    /** Persists [appearance] and publishes it to [ThemeRepository.appearance]. */
    fun setAppearance(appearance: Appearance)

    /**
     * Hue (in degrees) shared by the app's own screens for this process. It is not persisted,
     * so a fresh app launch starts with a new random hue.
     */
    val sessionHue: StateFlow<Float>

    /** Replaces the [sessionHue] with [degrees]. */
    fun setSessionHue(degrees: Float)

    /** Returns a new random hue in degrees, unrelated to the [sessionHue]. */
    fun randomHue(): Float
}
