package com.corphish.quicktools.repository

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ContextMenuOptionsRepositoryImplTest {

    private lateinit var repository: ContextMenuOptionsRepositoryImpl
    private val context: Context = mockk()
    private val sharedPreferences: SharedPreferences = mockk()
    private val editor: SharedPreferences.Editor = mockk(relaxed = true)
    private val packageManager: PackageManager = mockk(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(PreferenceManager::class)
        every { context.packageName } returns "com.corphish.quicktools"
        every { context.packageManager } returns packageManager
        every { PreferenceManager.getDefaultSharedPreferences(context) } returns sharedPreferences
        every { sharedPreferences.edit() } returns editor
        
        repository = ContextMenuOptionsRepositoryImpl(context)
    }

    @After
    fun tearDown() {
        unmockkStatic(PreferenceManager::class)
    }

    @Test
    fun testGetCurrentAppMode() {
        every { sharedPreferences.getString("context_menu_mode", AppMode.SINGLE.name) } returns AppMode.MULTI.name
        assertEquals(AppMode.MULTI, repository.getCurrentAppMode())
    }

    @Test
    fun testSetCurrentAppMode_Single() {
        repository.setCurrentAppMode(AppMode.SINGLE)
        
        verify { editor.putString("context_menu_mode", AppMode.SINGLE.name) }
        // Verify OptionsActivity enabled
        verify { 
            packageManager.setComponentEnabledSetting(
                any(), 
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 
                PackageManager.DONT_KILL_APP
            ) 
        }
    }

    @Test
    fun testSetCurrentAppMode_Multi() {
        // To test MULTI, we also need currently enabled features
        every { sharedPreferences.getBoolean(any(), true) } returns true
        
        repository.setCurrentAppMode(AppMode.MULTI)
        
        verify { editor.putString("context_menu_mode", AppMode.MULTI.name) }
        // Verify OptionsActivity disabled
        verify { 
            packageManager.setComponentEnabledSetting(
                any(), 
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
                PackageManager.DONT_KILL_APP
            ) 
        }
    }

    @Test
    fun testGetCurrentlyEnabledFeatures() {
        every { sharedPreferences.getBoolean("context_menu_features_EVAL", true) } returns true
        every { sharedPreferences.getBoolean("context_menu_features_WHATSAPP", true) } returns false
        every { sharedPreferences.getBoolean("context_menu_features_TRANSFORM", true) } returns true
        every { sharedPreferences.getBoolean("context_menu_features_TEXT_COUNT", true) } returns true
        every { sharedPreferences.getBoolean("context_menu_features_FIND_AND_REPLACE", true) } returns true
        every { sharedPreferences.getBoolean("context_menu_features_SAVE_TEXT", true) } returns true
        every { sharedPreferences.getBoolean("context_menu_features_NUMBER_ANALYSIS", true) } returns true
        every { sharedPreferences.getBoolean("context_menu_features_TEXT_TEMPLATE", true) } returns true

        val features = repository.getCurrentlyEnabledFeatures()
        assertTrue(features.contains(FeatureIds.EVAL))
        assertTrue(!features.contains(FeatureIds.WHATSAPP))
        assertTrue(features.contains(FeatureIds.NUMBER_ANALYSIS))
        assertTrue(features.contains(FeatureIds.TEXT_TEMPLATE))
    }

    @Test
    fun testEnableOrDisableFeature_MultiMode() {
        every { sharedPreferences.getString("context_menu_mode", AppMode.SINGLE.name) } returns AppMode.MULTI.name
        
        repository.enableOrDisableFeature(FeatureIds.EVAL, false)
        
        verify { editor.putBoolean("context_menu_features_EVAL", false) }
        verify { 
            packageManager.setComponentEnabledSetting(
                any(), 
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
                PackageManager.DONT_KILL_APP
            ) 
        }
    }
}
