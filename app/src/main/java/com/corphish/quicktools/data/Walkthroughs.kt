package com.corphish.quicktools.data

import com.corphish.quicktools.R

/** Ids of the composables walkthroughs point at, used with `Modifier.walkthroughTarget`. */
object WalkthroughTargets {
    /** First feature tile on the main screen. */
    const val MAIN_FEATURE_TILE = "main_feature_tile"

    /** Edit action on the main screen. */
    const val MAIN_EDIT = "main_edit"

    /** Settings action on the main screen. */
    const val MAIN_SETTINGS = "main_settings"

    /** Simulate button on the main screen. */
    const val MAIN_SIMULATE = "main_simulate"
}

/** All walkthroughs of the app. */
object Walkthroughs {
    /** Tour of the main screen: feature tiles, edit mode, simulation and settings. */
    val MAIN = Walkthrough(
        key = "main",
        steps = listOf(
            WalkthroughStep(WalkthroughTargets.MAIN_FEATURE_TILE, R.string.walkthrough_main_tile_title, R.string.walkthrough_main_tile_body),
            WalkthroughStep(WalkthroughTargets.MAIN_EDIT, R.string.walkthrough_main_edit_title, R.string.walkthrough_main_edit_body),
            WalkthroughStep(WalkthroughTargets.MAIN_SIMULATE, R.string.walkthrough_main_simulate_title, R.string.walkthrough_main_simulate_body),
            WalkthroughStep(WalkthroughTargets.MAIN_SETTINGS, R.string.walkthrough_main_settings_title, R.string.walkthrough_main_settings_body),
        ),
    )
}
