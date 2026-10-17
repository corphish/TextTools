package com.corphish.quicktools.viewmodels

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.corphish.quicktools.data.Appearance
import com.corphish.quicktools.usecases.ThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Supplies the theme inputs (appearance and hue) for the activity it is scoped to.
 *
 * An activity counts as a tool launch when its intent carries [Intent.EXTRA_PROCESS_TEXT]
 * (text selection menu, the single-option picker and simulation all send it). Activity-scoped
 * ViewModels receive the intent extras through [SavedStateHandle].
 * Tool launches get a random hue that is kept for the activity's lifetime (including
 * configuration changes); other screens share the session hue.
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val themeUseCase: ThemeUseCase,
) : ViewModel() {
    private val _toolHue: MutableStateFlow<Float>? =
        if (savedStateHandle.contains(Intent.EXTRA_PROCESS_TEXT)) MutableStateFlow(themeUseCase.newToolHue()) else null

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
