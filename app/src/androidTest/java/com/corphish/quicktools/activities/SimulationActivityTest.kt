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
class SimulationActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<SimulationActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testActivityTitleIsDisplayed() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.simulate))
            .assertIsDisplayed()
    }

    @Test
    fun testInputAndButtonsVisibility() {
        // Label for input
        composeTestRule.onNodeWithTag("input_label")
            .assertIsDisplayed()

        // Info button
        composeTestRule.onNodeWithTag("info_button")
            .assertIsDisplayed()
            .assertHasClickAction()

        // Simulation button
        composeTestRule.onNodeWithTag("simulate_button")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun testInfoDialog() {
        // Click info button
        composeTestRule.onNodeWithTag("info_button")
            .performClick()

        // Verify dialog is displayed
        composeTestRule.onNodeWithTag("info_dialog")
            .assertIsDisplayed()

        // Verify info text is in dialog
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.simulate_details))
            .assertIsDisplayed()

        // Close dialog
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.done))
            .performClick()

        // Verify dialog is gone
        composeTestRule.onNodeWithTag("info_dialog")
            .assertDoesNotExist()
    }

    @Test
    fun testInputInteraction() {
        val testText = "Hello Simulation"
        
        // Find input and type
        composeTestRule.onNodeWithTag("simulation_input")
            .performTextInput(testText)

        // Verify text is present
        composeTestRule.onNodeWithText(testText)
            .assertIsDisplayed()
    }

    @Test
    fun testSimulationButtonClick() {
        val testText = "Test Simulation Click"
        
        composeTestRule.onNodeWithTag("simulation_input")
            .performTextInput(testText)

        // Click the simulate button
        composeTestRule.onNodeWithTag("simulate_button")
            .performClick()
        
        // No crash check. Assertion after click is omitted as it launches a system/targeted intent
        // which can lead to flakiness in visibility checks.
    }
}
