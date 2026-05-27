package com.corphish.quicktools.activities

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.corphish.quicktools.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<SettingsActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testSettingsTitleIsDisplayed() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.title_activity_settings))
            .assertIsDisplayed()
    }

    @Test
    fun testAppSettingsSection() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.app))
            .performScrollTo()
            .assertIsDisplayed()
        
        // Find the title text specifically (ambiguous because of OutlinedTextField label)
        composeTestRule.onAllNodesWithText(composeTestRule.activity.getString(R.string.mode_select_title))
            .onFirst()
            .assertIsDisplayed()

        // Test opening the dropdown - click the one that has a click action (the dropdown field)
        composeTestRule.onAllNodesWithText(composeTestRule.activity.getString(R.string.mode_select_title))
            .filterToOne(hasClickAction())
            .performClick()
        
        composeTestRule.onAllNodesWithText(composeTestRule.activity.getString(R.string.mode_single_title))
            .onFirst()
            .assertIsDisplayed()
        composeTestRule.onAllNodesWithText(composeTestRule.activity.getString(R.string.mode_multi_title))
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun testMessagingSettingsSection() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.messaging))
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.prepend_country_code_title))
            .assertIsDisplayed()
        
        // Test toggling the switch area
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.prepend_country_code_title))
            .performClick()
    }

    @Test
    fun testEvaluateSettingsSection() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.eval_title_small))
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.decimal_points_title))
            .assertIsDisplayed()
    }

    @Test
    fun testAppInfoSettingsSection() {
        // Scroll to App Info section
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.app_info))
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.version))
            .performScrollTo()
            .assertIsDisplayed()
        
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.releases))
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun testDonateSettingsSection() {
        // Scroll to the donate section which is at the bottom
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.donate_msg))
            .performScrollTo()
            .assertIsDisplayed()

        // Handle multiple "Donate" strings if present
        composeTestRule.onAllNodesWithText(composeTestRule.activity.getString(R.string.donate))
            .onFirst()
            .performScrollTo()
            .assertIsDisplayed()
    }
}
