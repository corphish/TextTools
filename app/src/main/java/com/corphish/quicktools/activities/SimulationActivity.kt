package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.ui.common.CustomTopAppBar
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.TypographyV2

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
                                setPackage(packageName)
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
    var inputText by rememberSaveable { mutableStateOf("") }
    var showInfoDialog by rememberSaveable { mutableStateOf(false) }
    
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text(stringResource(R.string.done))
                }
            },
            title = { Text(stringResource(R.string.simulate)) },
            text = { Text(stringResource(R.string.simulate_details)) },
            modifier = Modifier.testTag("info_dialog")
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().padding(16.dp)) {
        val isWide = maxWidth > maxHeight
        
        if (isWide) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SimulationInputSection(
                    text = inputText, 
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                
                SimulationActions(
                    onInfoClick = { showInfoDialog = true },
                    onSimulateClick = { onSimulate(inputText) },
                    isVertical = true
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                SimulationInputSection(
                    text = inputText, 
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f)
                )
                
                SimulationActions(
                    onInfoClick = { showInfoDialog = true },
                    onSimulateClick = { onSimulate(inputText) },
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun SimulationInputSection(
    text: String, 
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(id = R.string.input),
            style = TypographyV2.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp).testTag("input_label")
        )
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            TextField(
                value = text,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxSize().testTag("simulation_input"),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                placeholder = { Text(text = stringResource(R.string.input)) }
            )
        }
    }
}

@Composable
fun SimulationActions(
    onInfoClick: () -> Unit,
    onSimulateClick: () -> Unit,
    modifier: Modifier = Modifier,
    isVertical: Boolean = false
) {
    if (isVertical) {
        Column(
            modifier = modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            SimulationActionButtons(onInfoClick, onSimulateClick)
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SimulationActionButtons(onInfoClick, onSimulateClick)
        }
    }
}

@Composable
fun SimulationActionButtons(
    onInfoClick: () -> Unit,
    onSimulateClick: () -> Unit
) {
    IconButton(
        onClick = onInfoClick,
        modifier = Modifier.testTag("info_button")
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_info),
            contentDescription = "Info",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
    }

    IconButton(
        onClick = onSimulateClick,
        modifier = Modifier
            .testTag("simulate_button")
            .clip(CircleShape)
            .size(64.dp)
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            painterResource(R.drawable.ic_simulate),
            contentDescription = stringResource(R.string.simulate),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SimulationPreview() {
    QuickToolsTheme {
        Simulation()
    }
}
