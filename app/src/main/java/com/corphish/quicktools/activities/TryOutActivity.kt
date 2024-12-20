package com.corphish.quicktools.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.ui.theme.Typography

/**
 * Try outs are disabled as of now because it looks like there is a known issue with
 * Jetpack Compose textfields which does not show context menu.
 */
class TryOutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mode = intent.getStringExtra(TRY_OUT_FLOW) ?: TRY_OUT_FLOW_WUP
        setContent {
            QuickToolsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TryOut(mode)
                }
            }
        }
    }

    companion object {
        // Flows
        const val TRY_OUT_FLOW_WUP = "wup"
        const val TRY_OUT_FLOW_EVAL = "eval"
        const val TRY_OUT_FLOW = "flow"
    }
}

@Composable
fun TryOut(flow: String) {
    val openSuccessDialog = remember { mutableStateOf(false) }
    val openErrorDialog = remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val activity = (LocalContext.current as? Activity)
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = Typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(
                id = when (flow) {
                    TryOutActivity.TRY_OUT_FLOW_WUP -> R.string.try_wup
                    TryOutActivity.TRY_OUT_FLOW_EVAL -> R.string.try_eval
                    else -> R.string.trial_error
                }
            ),
            style = Typography.bodyMedium
        )
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Enter text here") }
                )

                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FilledTonalButton(
                        onClick = { openErrorDialog.value = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(end = 4.dp)
                    ) { Text(text = stringResource(id = R.string.trial_error)) }
                    Button(
                        onClick = { openSuccessDialog.value = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(start = 4.dp)
                    ) { Text(text = stringResource(id = R.string.trial_success)) }
                }
            }
        }
    }

    when {
        openSuccessDialog.value -> {
            MessageDialog(
                hasDismissButton = false,
                onDismissRequest = { /* Not needed */ },
                onConfirmation = {
                    openSuccessDialog.value = false
                    activity?.finish()
                },
                dialogTitle = stringResource(id = R.string.trial_success),
                dialogText = stringResource(id = R.string.trial_success_msg),
                icon = Icons.Default.CheckCircle
            )
        }

        openErrorDialog.value -> {
            MessageDialog(
                hasDismissButton = true,
                onDismissRequest = {
                    openErrorDialog.value = false
                    activity?.finish()
                },
                onConfirmation = {
                    openErrorDialog.value = false
                    activity?.finish()
                    uriHandler.openUri(Constants.ISSUES_PAGE_LINK)
                },
                dialogTitle = stringResource(id = R.string.trial_error),
                dialogText = stringResource(id = R.string.trial_error_msg),
                icon = Icons.Default.Warning
            )
        }
    }
}

@Composable
fun MessageDialog(
    hasDismissButton: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            if (hasDismissButton) {
                TextButton(
                    onClick = {
                        onDismissRequest()
                    }
                ) {
                    Text(stringResource(id = android.R.string.cancel))
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    QuickToolsTheme {
        TryOut(TryOutActivity.TRY_OUT_FLOW_WUP)
    }
}