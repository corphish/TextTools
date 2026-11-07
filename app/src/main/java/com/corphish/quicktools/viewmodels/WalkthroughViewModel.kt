package com.corphish.quicktools.viewmodels

import androidx.lifecycle.ViewModel
import com.corphish.quicktools.data.Walkthrough
import com.corphish.quicktools.data.WalkthroughStep
import com.corphish.quicktools.usecases.WalkthroughUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * The walkthrough step currently on screen.
 *
 * @property walkthrough Walkthrough being shown.
 * @property stepIndex Index of the current step in [Walkthrough.steps].
 * @property hasPrevious Whether an earlier step with an available target exists.
 * @property hasNext Whether a later step with an available target exists; otherwise "Next" finishes.
 */
data class WalkthroughUiState(
    val walkthrough: Walkthrough,
    val stepIndex: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
) {
    /** The current step. */
    val step: WalkthroughStep get() = walkthrough.steps[stepIndex]
}

/**
 * Runs the walkthroughs of one screen, one at a time.
 *
 * A walkthrough starts once every one of its targets is on screen, and when either
 * - it was started manually with [start], or
 * - it has [Walkthrough.autoStart] set and is still pending.
 * Manually started ones go first; otherwise the screen's order is kept. Next and back skip
 * steps whose target is not on screen. Finishing and skipping both mark the walkthrough as
 * completed. Scoped to the activity, so the current step survives configuration changes.
 */
@HiltViewModel
class WalkthroughViewModel @Inject constructor(
    private val walkthroughUseCase: WalkthroughUseCase,
) : ViewModel() {
    private var walkthroughs: List<Walkthrough> = emptyList()
    private var availableTargets: Set<String> = emptySet()
    private val requestedKeys = linkedSetOf<String>()

    private val _state = MutableStateFlow<WalkthroughUiState?>(null)

    /** The step on screen, or null when no walkthrough is shown. */
    val state: StateFlow<WalkthroughUiState?> = _state

    /**
     * Sets the walkthroughs of the screen and starts the first one that is due.
     *
     * @param walkthroughs Walkthroughs in the order they should be shown.
     */
    fun setWalkthroughs(walkthroughs: List<Walkthrough>) {
        this.walkthroughs = walkthroughs
        tryStart()
    }

    /**
     * Updates which targets are on screen, then starts a due walkthrough if none is shown.
     *
     * @param targetIds Ids of the targets currently laid out and visible.
     */
    fun onTargetsChanged(targetIds: Set<String>) {
        availableTargets = targetIds
        _state.value?.let { show(it.walkthrough, it.stepIndex) }
        tryStart()
    }

    /**
     * Requests the walkthrough with [key], whether or not it was completed before. It starts
     * once its targets are on screen and no other walkthrough is shown.
     *
     * @return false when the screen has no walkthrough with [key].
     */
    fun start(key: String): Boolean {
        if (walkthroughs.none { it.key == key }) return false
        if (_state.value?.walkthrough?.key != key) {
            requestedKeys += key
            tryStart()
        }
        return true
    }

    /** Moves to the next step with an available target, or finishes the walkthrough. */
    fun next() {
        val current = _state.value ?: return
        val nextIndex = availableStepIndex(current.walkthrough, current.stepIndex + 1, forward = true)
        if (nextIndex == null) complete(current.walkthrough) else show(current.walkthrough, nextIndex)
    }

    /** Moves to the previous step with an available target, if any. */
    fun back() {
        val current = _state.value ?: return
        availableStepIndex(current.walkthrough, current.stepIndex - 1, forward = false)
            ?.let { show(current.walkthrough, it) }
    }

    /** Ends the walkthrough; it is not shown again, just like a finished one. */
    fun skip() {
        _state.value?.let { complete(it.walkthrough) }
    }

    private fun complete(walkthrough: Walkthrough) {
        walkthroughUseCase.markCompleted(walkthrough)
        requestedKeys -= walkthrough.key
        _state.value = null
        tryStart()
    }

    private fun tryStart() {
        if (_state.value != null) return
        val due = walkthroughs
            .sortedBy { it.key !in requestedKeys }
            .firstOrNull { isDue(it) && availableTargets.containsAll(it.targetIds) }
            ?: return
        requestedKeys -= due.key
        show(due, 0)
    }

    private fun isDue(walkthrough: Walkthrough) =
        walkthrough.key in requestedKeys || (walkthrough.autoStart && walkthroughUseCase.isPending(walkthrough))

    private fun show(walkthrough: Walkthrough, stepIndex: Int) {
        _state.value = WalkthroughUiState(
            walkthrough = walkthrough,
            stepIndex = stepIndex,
            hasPrevious = availableStepIndex(walkthrough, stepIndex - 1, forward = false) != null,
            hasNext = availableStepIndex(walkthrough, stepIndex + 1, forward = true) != null,
        )
    }

    /**
     * Finds the first step from [from] (inclusive) in the given direction whose target is on
     * screen.
     *
     * @return The step's index, or null when there is none.
     */
    private fun availableStepIndex(walkthrough: Walkthrough, from: Int, forward: Boolean): Int? {
        val indices = if (forward) from..walkthrough.steps.lastIndex else from downTo 0
        return indices.firstOrNull { walkthrough.steps[it].targetId in availableTargets }
    }
}
