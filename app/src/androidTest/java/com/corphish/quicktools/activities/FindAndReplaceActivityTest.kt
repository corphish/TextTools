package com.corphish.quicktools.activities

import android.content.Intent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.v2.createEmptyComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class FindAndReplaceActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createEmptyComposeRule()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testFindAndReplaceInteraction() {
        val initialText = "The quick brown fox jumps over the lazy dog"
        val intent = Intent(ApplicationProvider.getApplicationContext(), FindAndReplaceActivity::class.java).apply {
            putExtra(Intent.EXTRA_PROCESS_TEXT, initialText)
        }
        
        ActivityScenario.launch<FindAndReplaceActivity>(intent).use {
            // Verify initial text is displayed in main input
            composeTestRule.onNodeWithTag("main_input")
                .assertTextContains(initialText)

            // 1. Find "fox"
            composeTestRule.onNodeWithTag("find_input")
                .performTextInput("fox")
            
            // Verify counter shows (1/1)
            composeTestRule.onNodeWithText("(1/1)")
                .assertIsDisplayed()

            // 2. Replace with "cat"
            composeTestRule.onNodeWithTag("replace_input")
                .performTextInput("cat")
            
            // Click replace button
            composeTestRule.onNodeWithTag("replace_button")
                .performClick()

            // Verify main text updated
            composeTestRule.onNodeWithTag("main_input")
                .assertTextContains("The quick brown cat jumps over the lazy dog")
                
            // 3. Find "the" (case insensitive by default usually depends on VM, but let's check)
            // Clear find and replace inputs first
            composeTestRule.onNodeWithTag("find_input")
                .performTextClearance()
            composeTestRule.onNodeWithTag("find_input")
                .performTextInput("the")
                
            // Check counter (there are two "the"s if case insensitive, one "the" and one "The" if case sensitive)
            // Let's toggle ignore case to be sure
            composeTestRule.onNodeWithTag("ignore_case_button")
                .performClick()
                
            // Now find "the" should find "The" and "the"
            composeTestRule.onNodeWithText("(1/2)")
                .assertIsDisplayed()
                
            // Test next button
            composeTestRule.onNodeWithTag("find_next_button")
                .performClick()
            composeTestRule.onNodeWithText("(2/2)")
                .assertIsDisplayed()

            // 4. Replace all "the" with "a"
            composeTestRule.onNodeWithTag("replace_input")
                .performTextClearance()
            composeTestRule.onNodeWithTag("replace_input")
                .performTextInput("a")
                
            composeTestRule.onNodeWithTag("replace_all_button")
                .performClick()
                
            // Verify main text updated (both "The" and "the" replaced)
            composeTestRule.onNodeWithTag("main_input")
                .assertTextContains("a quick brown cat jumps over a lazy dog")
                
            // 5. Test Undo
            composeTestRule.onNodeWithTag("undo_button")
                .performClick()
            composeTestRule.onNodeWithTag("main_input")
                .assertTextContains("The quick brown cat jumps over the lazy dog")
                
            // 6. Test Redo
            composeTestRule.onNodeWithTag("redo_button")
                .performClick()
            composeTestRule.onNodeWithTag("main_input")
                .assertTextContains("a quick brown cat jumps over a lazy dog")
                
            // 7. Test Reset
            composeTestRule.onNodeWithTag("reset_button")
                .performClick()
            composeTestRule.onNodeWithTag("main_input")
                .assertTextContains(initialText)
        }
    }

    @Test
    fun testReadOnlyErrorFinishesActivity() {
        val intent = Intent(ApplicationProvider.getApplicationContext(), FindAndReplaceActivity::class.java).apply {
            putExtra(Intent.EXTRA_PROCESS_TEXT, "Readonly text")
            putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, true)
        }
        
        ActivityScenario.launch<FindAndReplaceActivity>(intent).use { scenario ->
            // In Android, we can check activity state
            // But ActivityScenario.state is more reliable
            // We expect it to finish, so state should be DESTROYED soon
            // But Toast is shown.
            
            // Testing finish() is hard with just compose rules.
            // But we can check if it stays CREATED or goes to DESTROYED.
            // Give it some time to finish
            Thread.sleep(500)
            assert(scenario.state == Lifecycle.State.DESTROYED)
        }
    }
}
