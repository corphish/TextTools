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
class TextCountActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<TextCountActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testActivityTitleIsDisplayed() {
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.text_count))
            .assertIsDisplayed()
    }

    @Test
    fun testInputUpdatesStatistics() {
        // Use text that doesn't contain the numbers we are looking for
        val testText = "abc def"
        
        // Find the input field. It has the placeholder string R.string.input
        composeTestRule.onAllNodesWithText(composeTestRule.activity.getString(R.string.input))
            .filterToOne(hasSetTextAction())
            .performTextInput(testText)

        // Statistics should be updated
        // 7 characters (abc def)
        assertStatDisplayed(R.string.characters, "7")
        
        // 2 words (abc, def)
        assertStatDisplayed(R.string.words, "2")

        // 6 letters (abc, def)
        assertStatDisplayed(R.string.letters, "6")
    }

    @Test
    fun testSectionsVisibility() {
        // Statistics section header
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.statistics))
            .assertIsDisplayed()

        // Visualizations section header
        scrollToNodeWithText(R.string.visualizations)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.visualizations))
            .assertIsDisplayed()

        // Advanced Features section header
        scrollToNodeWithText(R.string.advanced_features)
        composeTestRule.onNodeWithText(composeTestRule.activity.getString(R.string.advanced_features))
            .assertIsDisplayed()
    }

    @Test
    fun testWordFrequencyVisibility() {
        val testText = "apple banana apple"
        
        composeTestRule.onAllNodesWithText(composeTestRule.activity.getString(R.string.input))
            .filterToOne(hasSetTextAction())
            .performTextInput(testText)

        // Word frequency section should appear
        scrollToNodeWithText(R.string.word_frequency)
        
        // Check for word chips. Chips have label text. 
        // We filter out the input node by checking for hasSetTextAction()
        composeTestRule.onAllNodesWithText("apple", substring = true)
            .filter(!hasSetTextAction())
            .onFirst()
            .assertIsDisplayed()
            
        composeTestRule.onAllNodesWithText("banana", substring = true)
            .filter(!hasSetTextAction())
            .onFirst()
            .assertIsDisplayed()
    }

    @Test
    fun testAdvancedFeaturesContent() {
        val testText = "noon support@example.com 9876543210"
        
        composeTestRule.onAllNodesWithText(composeTestRule.activity.getString(R.string.input))
            .filterToOne(hasSetTextAction())
            .performTextInput(testText)

        // Scroll to advanced features
        scrollToNodeWithText(R.string.advanced_features)

        // Check for detected items (excluding the input field)
        composeTestRule.onAllNodesWithText("noon")
            .filter(!hasSetTextAction())
            .onFirst()
            .assertIsDisplayed()
            
        composeTestRule.onAllNodesWithText("support@example.com")
            .filter(!hasSetTextAction())
            .onFirst()
            .assertIsDisplayed()
            
        composeTestRule.onAllNodesWithText("9876543210")
            .filter(!hasSetTextAction())
            .onFirst()
            .assertIsDisplayed()
    }

    private fun assertStatDisplayed(labelRes: Int, expectedValue: String) {
        val label = composeTestRule.activity.getString(labelRes)
        // Value is in a sibling node (or same parent column) as the label
        composeTestRule.onNode(
            hasText(expectedValue) and hasAnySibling(hasText(label))
        ).assertIsDisplayed()
    }

    private fun scrollToNodeWithText(resId: Int) {
        val text = composeTestRule.activity.getString(resId)
        composeTestRule.onNodeWithTag("stats_list")
            .performScrollToNode(hasText(text))
    }
}
