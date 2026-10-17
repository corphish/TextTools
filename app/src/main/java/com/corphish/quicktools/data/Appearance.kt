package com.corphish.quicktools.data

/**
 * User-selectable light/dark appearance of the app.
 *
 * @property isDark Whether this choice forces dark mode, or null when it follows the system.
 */
enum class Appearance(val isDark: Boolean?) {
    /** Follow the system light/dark setting. */
    SYSTEM(null),

    /** Always use the light scheme. */
    LIGHT(false),

    /** Always use the dark scheme. */
    DARK(true);

    companion object {
        /** Default appearance used when nothing has been chosen yet. */
        val DEFAULT = SYSTEM

        /**
         * Returns the appearance stored under [name], or [DEFAULT] when [name] is null or unknown.
         */
        fun fromName(name: String?): Appearance = entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}
