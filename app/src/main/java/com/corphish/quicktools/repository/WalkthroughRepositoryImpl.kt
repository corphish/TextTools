package com.corphish.quicktools.repository

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * [WalkthroughRepository] backed by the app's SharedPreferences. Each walkthrough key is stored
 * as an Int under [KEY_PREFIX] + key, which keeps them apart from other settings and lets
 * [resetAll] remove only walkthrough entries.
 */
class WalkthroughRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
) : WalkthroughRepository {
    private val _sharedPreferenceManager = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)

    private val _completedVersions = MutableStateFlow(readCompletedVersions())

    override val completedVersions: StateFlow<Map<String, Int>> = _completedVersions

    override fun markCompleted(key: String, version: Int) {
        _sharedPreferenceManager.edit { putInt(KEY_PREFIX + key, version) }
        _completedVersions.value += key to version
    }

    override fun resetAll() {
        val keys = _completedVersions.value.keys
        _sharedPreferenceManager.edit { keys.forEach { remove(KEY_PREFIX + it) } }
        _completedVersions.value = emptyMap()
    }

    /** Reads every stored walkthrough entry, ignoring values that are not Ints. */
    private fun readCompletedVersions(): Map<String, Int> =
        _sharedPreferenceManager.all.orEmpty()
            .filterKeys { it.startsWith(KEY_PREFIX) }
            .mapNotNull { (key, value) -> (value as? Int)?.let { key.removePrefix(KEY_PREFIX) to it } }
            .toMap()

    private companion object {
        /** Prefix of the preference keys holding completed walkthrough versions. */
        const val KEY_PREFIX = "walkthrough_"
    }
}
