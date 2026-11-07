package com.corphish.quicktools.data

import androidx.annotation.StringRes

/**
 * One step of a [Walkthrough]: highlights the composable tagged with [targetId] and explains it.
 *
 * @property targetId Id given to the target's `Modifier.walkthroughTarget`.
 * @property title Short title; its last word is emphasised.
 * @property body Explanation shown under the title.
 */
data class WalkthroughStep(
    val targetId: String,
    @StringRes val title: Int,
    @StringRes val body: Int,
)

/**
 * Guided tour of a screen, shown as a spotlight over each step's target in turn.
 *
 * Finishing or skipping it saves [version] under [key], so it is not shown again until
 * [version] is raised (e.g. after the screen is redesigned).
 *
 * @property key Persisted id of the walkthrough; must be unique within the app.
 * @property steps Steps in the order they are shown.
 * @property version Version saved on completion; raise it to show the walkthrough again.
 * @property autoStart Whether it starts by itself while pending, or only when started manually.
 */
data class Walkthrough(
    val key: String,
    val steps: List<WalkthroughStep>,
    val version: Int = 1,
    val autoStart: Boolean = true,
) {
    init {
        require(steps.isNotEmpty()) { "Walkthrough $key needs at least one step" }
        require(version > 0) { "Walkthrough $key needs a positive version" }
    }

    /** Ids of every target the steps point at. */
    val targetIds: Set<String> get() = steps.mapTo(mutableSetOf()) { it.targetId }
}
