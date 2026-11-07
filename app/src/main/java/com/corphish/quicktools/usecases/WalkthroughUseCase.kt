package com.corphish.quicktools.usecases

import com.corphish.quicktools.data.Walkthrough
import com.corphish.quicktools.repository.WalkthroughRepository
import javax.inject.Inject

/**
 * Decides which walkthroughs still need to be shown and records the ones the user finished
 * or skipped. Both count as completed.
 */
class WalkthroughUseCase @Inject constructor(
    private val walkthroughRepository: WalkthroughRepository,
) {
    /**
     * Whether [walkthrough] has not been completed at its current version.
     *
     * @return true when its saved version is missing or lower than [Walkthrough.version].
     */
    fun isPending(walkthrough: Walkthrough): Boolean =
        (walkthroughRepository.completedVersions.value[walkthrough.key] ?: 0) < walkthrough.version

    /** Saves [walkthrough] as completed at its current version. */
    fun markCompleted(walkthrough: Walkthrough) {
        walkthroughRepository.markCompleted(walkthrough.key, walkthrough.version)
    }

    /** Makes every walkthrough pending again. */
    fun resetAll() {
        walkthroughRepository.resetAll()
    }
}
