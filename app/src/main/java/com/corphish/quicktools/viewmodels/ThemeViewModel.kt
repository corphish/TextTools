package com.corphish.quicktools.viewmodels

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.corphish.quicktools.data.Appearance
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.usecases.ThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Supplies the theme inputs (appearance and hue) for the activity it is scoped to.
 *
 * Activity-scoped ViewModels receive the intent extras through [SavedStateHandle]. The hue is
 * picked from them in this order:
 * 1. [Constants.INTENT_HUE]: the screen was opened from another screen of the app (simulation,
 *    the single-option picker, or a tool continuing to the next step), so it keeps that hue.
 * 2. [Intent.EXTRA_PROCESS_TEXT] without a hue: a tool opened from another app's text selection
 *    menu, which gets a new random hue.
 * 3. Otherwise: the app's own screens, which share the session hue.
 * Hues from 1 and 2 are kept for the activity's lifetime, including configuration changes.
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val themeUseCase: ThemeUseCase,
) : ViewModel() {
    // Other apps can send any extras, so only accept a finite Float as an inherited hue
    private val inheritedHue = (savedStateHandle.get<Any?>(Constants.INTENT_HUE) as? Float)?.takeIf { it.isFinite() }

    private val _toolHue: MutableStateFlow<Float>? = when {
        inheritedHue != null -> MutableStateFlow(inheritedHue)
        savedStateHandle.contains(Intent.EXTRA_PROCESS_TEXT) -> MutableStateFlow(themeUseCase.newToolHue())
        else -> null
    }

    /** The user's appearance choice. */
    val appearance: StateFlow<Appearance> = themeUseCase.appearance

    /** Hue in degrees for this activity. */
    val hue: StateFlow<Float> = _toolHue ?: themeUseCase.sessionHue

    /**
     * Records a hue change made by the UI (e.g. "Shuffle colors"), so it is kept for this tool
     * launch or shared with the rest of the session.
     */
    fun onHueChanged(degrees: Float) {
        _toolHue?.let { it.value = degrees } ?: themeUseCase.setSessionHue(degrees)
    }
}
