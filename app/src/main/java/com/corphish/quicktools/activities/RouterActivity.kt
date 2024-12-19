package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.corphish.quicktools.viewmodels.OnBoardingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RouterActivity : AppCompatActivity() {
    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.onBoardingDone.collect { onBoardingDone ->
                val intent = if (onBoardingDone) {
                    Intent(this@RouterActivity, MainActivity::class.java)
                } else {
                    Intent(this@RouterActivity, OnBoardingActivity::class.java)
                }

                startActivity(intent)
                finish()
            }
        }
    }
}