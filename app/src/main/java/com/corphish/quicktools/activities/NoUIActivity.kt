package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Base activity class for other feature classes.
 */
abstract class NoUIActivity: AppCompatActivity() {

    /**
     * Main intent handling logic goes here that must be overridden by other feature classes.
     */
    abstract fun handleIntent(intent: Intent)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntentWrapper(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentWrapper(intent)
    }

    private fun handleIntentWrapper(intent: Intent) {
        handleIntent(intent)
        finish()
    }
}