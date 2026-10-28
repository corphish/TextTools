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
class TextActionActivityIntentTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun testHandOffIntent_ForwardsResult() {
        val intent = TextActionActivity.handOffIntent(context, "result", readonly = false, forceCopy = false)

        assertTrue(intent.flags and Intent.FLAG_ACTIVITY_FORWARD_RESULT != 0)
    }

    @Test
    fun testHandOffIntent_TargetsTextActions() {
        val intent = TextActionActivity.handOffIntent(context, "result", readonly = false, forceCopy = false)

        assertEquals(TextActionActivity::class.java.name, intent.component?.className)
    }

    @Test
    fun testHandOffIntent_CarriesExtras() {
        val intent = TextActionActivity.handOffIntent(context, "result", readonly = true, forceCopy = false)

        assertEquals("result", intent.getStringExtra(Intent.EXTRA_PROCESS_TEXT))
        assertTrue(intent.getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false))
        assertFalse(intent.getBooleanExtra(Constants.INTENT_FORCE_COPY, true))
    }
}
