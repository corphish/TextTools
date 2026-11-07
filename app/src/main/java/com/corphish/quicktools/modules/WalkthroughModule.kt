package com.corphish.quicktools.modules

import com.corphish.quicktools.repository.WalkthroughRepository
import com.corphish.quicktools.repository.WalkthroughRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds the walkthrough repository. Kept apart from [AppModule] so instrumented tests can
 * replace it alone, e.g. to keep walkthroughs from covering the screens under test.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class WalkthroughModule {
    @Binds
    @Singleton
    abstract fun bindWalkthroughRepository(impl: WalkthroughRepositoryImpl): WalkthroughRepository
}
