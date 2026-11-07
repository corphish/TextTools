package com.corphish.quicktools.modules

import com.corphish.quicktools.repository.ContextMenuOptionsRepository
import com.corphish.quicktools.repository.ContextMenuOptionsRepositoryImpl
import com.corphish.quicktools.repository.EvalRepository
import com.corphish.quicktools.repository.EvalRepositoryImpl
import com.corphish.quicktools.repository.NumberAnalysisRepository
import com.corphish.quicktools.repository.NumberAnalysisRepositoryImpl
import com.corphish.quicktools.repository.SettingsRepository
import com.corphish.quicktools.repository.SettingsRepositoryImpl
import com.corphish.quicktools.repository.TextActionRepository
import com.corphish.quicktools.repository.TextActionRepositoryImpl
import com.corphish.quicktools.repository.TextAnalysisRepository
import com.corphish.quicktools.repository.TextAnalysisRepositoryImpl
import com.corphish.quicktools.repository.TextReplacementRepository
import com.corphish.quicktools.repository.TextReplacementRepositoryImpl
import com.corphish.quicktools.repository.TextRepository
import com.corphish.quicktools.repository.TextRepositoryImpl
import com.corphish.quicktools.repository.TextTemplateRepository
import com.corphish.quicktools.repository.TextTemplateRepositoryImpl
import com.corphish.quicktools.repository.TextTransformRepository
import com.corphish.quicktools.repository.TextTransformRepositoryImpl
import com.corphish.quicktools.repository.ThemeRepository
import com.corphish.quicktools.repository.ThemeRepositoryImpl
import com.corphish.quicktools.repository.WalkthroughRepository
import com.corphish.quicktools.repository.WalkthroughRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Binds each repository interface to its implementation.
 * All implementations here have an `@Inject` constructor, so a plain `@Binds` is
 * sufficient (and preferred over `@Provides`) since no extra construction logic is needed.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindTextRepository(impl: TextRepositoryImpl): TextRepository

    @Binds
    @Singleton
    abstract fun bindTextReplacementRepository(impl: TextReplacementRepositoryImpl): TextReplacementRepository

    @Binds
    @Singleton
    abstract fun bindTextTransformRepository(impl: TextTransformRepositoryImpl): TextTransformRepository

    @Binds
    @Singleton
    abstract fun bindNumberAnalysisRepository(impl: NumberAnalysisRepositoryImpl): NumberAnalysisRepository

    @Binds
    @Singleton
    abstract fun bindContextMenuOptionsRepository(impl: ContextMenuOptionsRepositoryImpl): ContextMenuOptionsRepository

    @Binds
    @Singleton
    abstract fun bindTextActionRepository(impl: TextActionRepositoryImpl): TextActionRepository

    @Binds
    @Singleton
    abstract fun bindTextAnalysisRepository(impl: TextAnalysisRepositoryImpl): TextAnalysisRepository

    @Binds
    @Singleton
    abstract fun bindTextTemplateRepository(impl: TextTemplateRepositoryImpl): TextTemplateRepository

    @Binds
    @Singleton
    abstract fun bindEvalRepository(impl: EvalRepositoryImpl): EvalRepository

    @Binds
    @Singleton
    abstract fun bindThemeRepository(impl: ThemeRepositoryImpl): ThemeRepository

    @Binds
    @Singleton
    abstract fun bindWalkthroughRepository(impl: WalkthroughRepositoryImpl): WalkthroughRepository
}
