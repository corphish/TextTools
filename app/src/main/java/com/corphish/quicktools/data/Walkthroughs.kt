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

    /** Text card on the transform screen. */
    const val TRANSFORM_TEXT = "transform_text"

    /** Transform options button on the transform screen. */
    const val TRANSFORM_OPTIONS = "transform_options"

    /** Preview toggle on the transform screen. */
    const val TRANSFORM_PREVIEW = "transform_preview"

    /** Next button on the transform screen. */
    const val TRANSFORM_NEXT = "transform_next"

    /** Text input on the text analysis screen. */
    const val TEXT_ANALYSIS_INPUT = "text_analysis_input"

    /** Statistics on the text analysis screen. */
    const val TEXT_ANALYSIS_STATS = "text_analysis_stats"

    /** Number input on the number analysis screen. */
    const val NUMBER_ANALYSIS_INPUT = "number_analysis_input"

    /** Base pills on the number analysis screen. */
    const val NUMBER_ANALYSIS_BASES = "number_analysis_bases"

    /** First result section (conversions) on the number analysis screen. */
    const val NUMBER_ANALYSIS_RESULTS = "number_analysis_results"
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

    /** Tour of the transform screen: text, transform options, preview and Next. */
    val TRANSFORM = Walkthrough(
        key = "transform",
        steps = listOf(
            WalkthroughStep(WalkthroughTargets.TRANSFORM_TEXT, R.string.walkthrough_transform_text_title, R.string.walkthrough_transform_text_body),
            WalkthroughStep(WalkthroughTargets.TRANSFORM_OPTIONS, R.string.walkthrough_transform_options_title, R.string.walkthrough_transform_options_body),
            WalkthroughStep(WalkthroughTargets.TRANSFORM_PREVIEW, R.string.walkthrough_transform_preview_title, R.string.walkthrough_transform_preview_body),
            WalkthroughStep(WalkthroughTargets.TRANSFORM_NEXT, R.string.walkthrough_transform_next_title, R.string.walkthrough_transform_next_body),
        ),
    )

    /**
     * Tour of the text analysis screen. Only the input and the statistics are on screen when it
     * opens, so the last step points to the sections further down.
     */
    val TEXT_ANALYSIS = Walkthrough(
        key = "text_analysis",
        steps = listOf(
            WalkthroughStep(WalkthroughTargets.TEXT_ANALYSIS_INPUT, R.string.walkthrough_text_analysis_input_title, R.string.walkthrough_text_analysis_input_body),
            WalkthroughStep(WalkthroughTargets.TEXT_ANALYSIS_STATS, R.string.walkthrough_text_analysis_stats_title, R.string.walkthrough_text_analysis_stats_body),
        ),
    )

    /**
     * Tour of the number analysis screen: input, base and results. It starts once the input is
     * a valid number, as the bases and results only show then.
     */
    val NUMBER_ANALYSIS = Walkthrough(
        key = "number_analysis",
        steps = listOf(
            WalkthroughStep(WalkthroughTargets.NUMBER_ANALYSIS_INPUT, R.string.walkthrough_number_analysis_input_title, R.string.walkthrough_number_analysis_input_body),
            WalkthroughStep(WalkthroughTargets.NUMBER_ANALYSIS_BASES, R.string.walkthrough_number_analysis_bases_title, R.string.walkthrough_number_analysis_bases_body),
            WalkthroughStep(WalkthroughTargets.NUMBER_ANALYSIS_RESULTS, R.string.walkthrough_number_analysis_results_title, R.string.walkthrough_number_analysis_results_body),
        ),
    )

    /** Every walkthrough of the app; keys must be unique. */
    val ALL = listOf(MAIN, SIMULATION, TRANSFORM, TEXT_ANALYSIS, NUMBER_ANALYSIS)
}
