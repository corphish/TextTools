package com.corphish.quicktools.modules

import com.corphish.quicktools.data.Walkthroughs
import com.corphish.quicktools.repository.WalkthroughRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Singleton

/**
 * Replaces [WalkthroughModule] in instrumented tests with a repository where every walkthrough
 * is already completed, so no walkthrough covers the screens under test.
 */
@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [WalkthroughModule::class])
object FakeWalkthroughModule {
    @Provides
    @Singleton
    fun provideWalkthroughRepository(): WalkthroughRepository = CompletedWalkthroughRepository()
}

/** In-memory [WalkthroughRepository] that starts with every app walkthrough completed. */
class CompletedWalkthroughRepository : WalkthroughRepository {
    private val allCompleted = Walkthroughs.ALL.associate { it.key to it.version }
    private val _completedVersions = MutableStateFlow(allCompleted)

    override val completedVersions: StateFlow<Map<String, Int>> = _completedVersions

    override fun markCompleted(key: String, version: Int) {
        _completedVersions.value += key to version
    }

    override fun resetAll() {
        _completedVersions.value = emptyMap()
    }
}
