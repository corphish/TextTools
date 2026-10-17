package com.corphish.quicktools.repository

import android.content.Context
import androidx.core.content.edit
import com.corphish.quicktools.data.Appearance
import com.corphish.quicktools.functions.ThemeFunctions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * [ThemeRepository] backed by the app's SharedPreferences (appearance) and memory (session hue).
 * Bound as a singleton so every activity in the process sees the same state.
 */
class ThemeRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
    private val themeFunctions: ThemeFunctions,
) : ThemeRepository {
    private val _appearanceKey = "appearance"

    private val _sharedPreferenceManager = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)

    private val _appearance = MutableStateFlow(
        Appearance.fromName(_sharedPreferenceManager.getString(_appearanceKey, null))
    )
    private val _sessionHue = MutableStateFlow(themeFunctions.randomHue())

    override val appearance: StateFlow<Appearance> = _appearance
    override val sessionHue: StateFlow<Float> = _sessionHue

    override fun setAppearance(appearance: Appearance) {
        _sharedPreferenceManager.edit {
            putString(_appearanceKey, appearance.name)
        }
        _appearance.value = appearance
    }

    override fun setSessionHue(degrees: Float) {
        _sessionHue.value = degrees
    }

    override fun randomHue() = themeFunctions.randomHue()
}
