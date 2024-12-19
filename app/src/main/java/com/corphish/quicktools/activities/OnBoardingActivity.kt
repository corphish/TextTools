package com.corphish.quicktools.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.R
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.viewmodels.OnBoardingViewModel

class OnBoardingActivity : ComponentActivity() {
    private val viewModel: OnBoardingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickToolsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    OnBoarding(
                        onAppModeSelected = {
                            viewModel.setAppMode(it)
                        },
                        onFinish = {
                            viewModel.setOnBoardingDone(true)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OnBoarding(
    onAppModeSelected: (AppMode) -> Unit = {},
    onFinish: () -> Unit = {},
) {
    var page by remember { mutableIntStateOf(1) }

    when (page) {
        0 -> InitialPage { page = 1 }
        1 -> ModeSelectionScreen(
            onModeSelected = { onAppModeSelected(it) },
            onFinish = { onFinish() }
        )
    }
}

@Composable
fun InitialPage(
    onNextPressed: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(256.dp)
        )

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = BrandFontFamily
        )

        Text(
            text = stringResource(R.string.app_desc_short),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        IconButton(
            onClick = { onNextPressed() },
            modifier = Modifier
                .padding(vertical = 16.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun ModeSelectionScreen(
    onModeSelected: (AppMode) -> Unit = {},
    onFinish: () -> Unit = {},
) {
    var selectedMode by remember { mutableStateOf(AppMode.SINGLE) }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Icon(
            Icons.Filled.Settings,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = stringResource(R.string.mode_select_title),
            style = MaterialTheme.typography.headlineMedium,
            fontFamily = BrandFontFamily
        )

        Text(
            text = stringResource(R.string.mode_select_desc),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        ModeCard(
            title = R.string.mode_single_title,
            desc = R.string.mode_single_desc,
            isSelected = selectedMode == AppMode.SINGLE,
            onClick = {
                selectedMode = AppMode.SINGLE
                onModeSelected(AppMode.SINGLE)
            }
        )

        ModeCard(
            title = R.string.mode_multi_title,
            desc = R.string.mode_multi_desc,
            isSelected = selectedMode == AppMode.MULTI,
            onClick = {
                selectedMode = AppMode.MULTI
                onModeSelected(AppMode.MULTI)
            }
        )

        Box(modifier = Modifier.weight(1f))

        IconButton(
            onClick = { onFinish() },
            modifier = Modifier
                .padding(vertical = 16.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .align(Alignment.CenterHorizontally)
        ) {
            Icon(
                Icons.Default.Done,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun ModeCard(
    @StringRes title: Int,
    @StringRes desc: Int,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    Card(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        colors = CardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                stringResource(title),
                fontWeight = FontWeight.Bold,
                fontFamily = BrandFontFamily
            )
            Text(
                stringResource(desc),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnBoardingPreview() {
    QuickToolsTheme {
        OnBoarding()
    }
}