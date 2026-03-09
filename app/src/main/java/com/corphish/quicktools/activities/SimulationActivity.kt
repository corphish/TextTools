package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.ui.common.CustomTopAppBar
import com.corphish.quicktools.ui.theme.QuickToolsTheme

class SimulationActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickToolsTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CustomTopAppBar(
                            id = R.string.simulate,
                            onNavigationClick = { finish() })
                    }
                ) { innerPadding ->
                    Simulation(
                        modifier = Modifier.padding(innerPadding),
                        onSimulate = {
                            val intent = Intent(Intent.ACTION_PROCESS_TEXT).apply {
                                type = "text/plain"
                                setClass(this@SimulationActivity, OptionsActivity::class.java)
                                putExtra(Intent.EXTRA_PROCESS_TEXT, it)
                                putExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false)
                                putExtra(Constants.INTENT_FORCE_COPY, true)
                            }

                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Simulation(modifier: Modifier = Modifier, onSimulate: (String) -> Unit = {}) {
    var inputText by remember { mutableStateOf("") }
    ConstraintLayout(
        modifier = modifier.fillMaxSize()
    ) {
        val (userInput, infoText, launchSimulation) = createRefs()

        OutlinedTextField(
            value = inputText,
            onValueChange = {
                inputText = it
            },
            modifier = Modifier.constrainAs(userInput) {
                top.linkTo(parent.top, margin = 16.dp)
                bottom.linkTo(infoText.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
                height = Dimension.fillToConstraints
                width = Dimension.fillToConstraints
            },
            label = { Text(text = stringResource(id = R.string.input)) },
        )

        Text(
            stringResource(id = R.string.simulate_details),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.constrainAs(infoText) {
                top.linkTo(userInput.bottom, margin = 8.dp)
                bottom.linkTo(launchSimulation.top, margin = 8.dp)
                start.linkTo(parent.start, margin = 8.dp)
                end.linkTo(parent.end, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        )

        IconButton(
            onClick = { onSimulate(inputText) },
            modifier = Modifier
                .constrainAs(launchSimulation) {
                    top.linkTo(infoText.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    width = Dimension.wrapContent
                    height = Dimension.wrapContent
                }
                .clip(CircleShape)
                .size(64.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    QuickToolsTheme {
        Simulation()
    }
}