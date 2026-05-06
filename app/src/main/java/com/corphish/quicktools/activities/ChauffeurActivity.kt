package com.corphish.quicktools.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2
import com.corphish.quicktools.viewmodels.ChauffeurViewModel
import com.corphish.quicktools.viewmodels.GenerativeModelStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChauffeurActivity : ComponentActivity() {
    private val viewModel by viewModels<ChauffeurViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickToolsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChauffeurScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ChauffeurScreen(
    viewModel: ChauffeurViewModel,
    modifier: Modifier = Modifier,
) {
    val modelStatus by viewModel.modelDownloadFlow.collectAsState()

    when (modelStatus) {
        GenerativeModelStatus.Available -> {

        }

        GenerativeModelStatus.DownloadCompleted -> {

        }

        is GenerativeModelStatus.DownloadFailed -> {

        }

        GenerativeModelStatus.DownloadStarting -> {

        }

        GenerativeModelStatus.Downloadable -> {
            ModelDownloadInitialScreen(
                isAvailable = true,
                modifier = modifier,
                onDownloadButtonClicked = {
                    // viewModel.downloadModel()
                }
            )
        }

        is GenerativeModelStatus.Downloading -> {

        }

        GenerativeModelStatus.Initial -> {
            InitialScreen()
        }

        GenerativeModelStatus.NotAvailable -> {
            ModelDownloadInitialScreen(
                isAvailable = false,
                modifier = modifier,
                onDownloadButtonClicked = {}
            )
        }
    }
}

@Composable
fun InitialScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ModelDownloadInitialScreen(
    isAvailable: Boolean,
    onDownloadButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.CoPresent,
                modifier = Modifier.size(128.dp).padding(bottom = 32.dp),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text = "Welcome to Chauffeur Services", style = TypographyV2.titleLarge)
            Text(
                text = "You can talk to the chauffeur to get things done",
                style = TypographyV2.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (isAvailable) {
                Button(onClick = onDownloadButtonClicked) {
                    Text(text = "Download Model")
                }
            } else {
                Text(
                    text = "Model not available. Function cannot be used in this device.",
                    style = TypographyV2.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}