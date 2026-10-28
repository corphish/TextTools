package com.corphish.quicktools.activities

import android.content.Context
import android.content.Intent
import com.corphish.quicktools.data.Constants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class FindAndReplaceActivityIntentTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun testTextActionsIntent_ForwardsResult() {
        val intent = FindAndReplaceActivity.textActionsIntent(context, "result", readonly = false, forceCopy = false)

        assertTrue(intent.flags and Intent.FLAG_ACTIVITY_FORWARD_RESULT != 0)
    }

    @Test
    fun testTextActionsIntent_TargetsTextActions() {
        val intent = FindAndReplaceActivity.textActionsIntent(context, "result", readonly = false, forceCopy = false)

        assertEquals(TextActionActivity::class.java.name, intent.component?.className)
    }

    @Test
    fun testTextActionsIntent_CarriesExtras() {
        val intent = FindAndReplaceActivity.textActionsIntent(context, "result", readonly = true, forceCopy = false)

        assertEquals("result", intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT))
        assertTrue(intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false))
        assertFalse(intent.getBooleanExtra(Constants.INTENT_FORCE_COPY, true))
    }
}
