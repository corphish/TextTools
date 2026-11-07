package com.corphish.quicktools.repository

import kotlinx.coroutines.flow.StateFlow

/** Stores which version of each walkthrough the user has finished or skipped. */
interface WalkthroughRepository {
    /** Completed version per walkthrough key; keys never completed are absent. */
    val completedVersions: StateFlow<Map<String, Int>>

    /** Saves [version] as the completed version of the walkthrough with [key]. */
    fun markCompleted(key: String, version: Int)

    /** Forgets every completed walkthrough, so all of them are shown again. */
    fun resetAll()
}
