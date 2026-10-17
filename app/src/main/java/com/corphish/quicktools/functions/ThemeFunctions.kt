package com.corphish.quicktools.functions

import com.tabotabo.ui.color.Hue
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Theme-related helpers.
 */
@Singleton
class ThemeFunctions @Inject constructor() {

    /**
     * Returns a random hue in degrees, in `[0, 360)`.
     *
     * @param random Source of randomness; overridable for deterministic tests.
     */
    fun randomHue(random: Random = Random.Default): Float = Hue.random(random).degrees
}
