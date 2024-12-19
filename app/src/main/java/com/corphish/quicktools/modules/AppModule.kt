package com.corphish.quicktools.modules

import android.content.Context
import com.corphish.quicktools.repository.ContextMenuOptionsRepository
import com.corphish.quicktools.repository.ContextMenuOptionsRepositoryImpl
import com.corphish.quicktools.repository.SettingsRepository
import com.corphish.quicktools.repository.TextRepository
import com.corphish.quicktools.repository.TextRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context) =
        SettingsRepository(context)

    @Provides
    @Singleton
    fun provideTextRepository(@ApplicationContext context: Context): TextRepository =
        TextRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideContextMenuOptionsRepository(@ApplicationContext context: Context): ContextMenuOptionsRepository =
        ContextMenuOptionsRepositoryImpl(context)
}