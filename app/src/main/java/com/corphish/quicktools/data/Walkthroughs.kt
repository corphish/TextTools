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

    /** Text input on the simulation screen. */
    const val SIMULATION_INPUT = "simulation_input"

    /** Simulate button on the simulation screen. */
    const val SIMULATION_RUN = "simulation_run"

    /** Info action on the simulation screen. */
    const val SIMULATION_INFO = "simulation_info"
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

    /** Tour of the simulation screen: input, Simulate button and info. */
    val SIMULATION = Walkthrough(
        key = "simulation",
        steps = listOf(
            WalkthroughStep(WalkthroughTargets.SIMULATION_INPUT, R.string.walkthrough_simulation_input_title, R.string.walkthrough_simulation_input_body),
            WalkthroughStep(WalkthroughTargets.SIMULATION_RUN, R.string.walkthrough_simulation_run_title, R.string.walkthrough_simulation_run_body),
            WalkthroughStep(WalkthroughTargets.SIMULATION_INFO, R.string.walkthrough_simulation_info_title, R.string.walkthrough_simulation_info_body),
        ),
    )

    /** Every walkthrough of the app; keys must be unique. */
    val ALL = listOf(MAIN, SIMULATION)
}
