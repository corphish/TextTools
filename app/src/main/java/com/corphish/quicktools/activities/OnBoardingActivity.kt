package com.corphish.quicktools.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Done
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.R
import com.corphish.quicktools.repository.AppMode
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.viewmodels.OnBoardingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : ComponentActivity() {
    private val viewModel: OnBoardingViewModel by viewModels()

    private fun switchToMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickToolsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    OnBoarding(
                        paddingValues = innerPadding,
                        onAppModeSelected = {
                            viewModel.setAppMode(it)
                        },
                        onFinish = {
                            viewModel.setOnBoardingDone(true)
                            switchToMain()
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
    paddingValues: PaddingValues = PaddingValues(),
    onAppModeSelected: (AppMode) -> Unit = {},
    onFinish: () -> Unit = {},
) {
    var page by rememberSaveable { mutableIntStateOf(0) }

    when (page) {
        0 -> InitialPage { page = 1 }
        1 -> ModeSelectionScreen(
            paddingValues = paddingValues,
            onModeSelected = { onAppModeSelected(it) },
            onFinish = { onFinish() }
        )
    }
}

@Composable
fun InitialPage(
    onNextPressed: () -> Unit = {}
) {
    val config = LocalConfiguration.current

    if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
        InitialPagePortrait(onNextPressed)
    } else {
        InitialPageLandscape(onNextPressed)
    }
}

@Composable
fun InitialPagePortrait(
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
            fontFamily = BrandFontFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
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
                .padding(vertical = 32.dp)
                .clip(CircleShape)
                .size(64.dp)
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
fun InitialPageLandscape(
    onNextPressed: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(256.dp)
                .weight(1f)
                .padding(start = 32.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(all = 32.dp)
                .weight(2f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = BrandFontFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = stringResource(R.string.app_desc_short),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = { onNextPressed() },
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .clip(CircleShape)
                    .size(64.dp)
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
}

@Composable
fun ModeSelectionScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onModeSelected: (AppMode) -> Unit = {},
    onFinish: () -> Unit = {},
) {
    val config = LocalConfiguration.current

    if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
        ModeSelectionScreenPortrait(paddingValues, onModeSelected, onFinish)
    } else {
        ModeSelectionScreenLandscape(paddingValues, onModeSelected, onFinish)
    }
}

@Composable
fun ModeSelectionScreenPortrait(
    paddingValues: PaddingValues = PaddingValues(),
    onModeSelected: (AppMode) -> Unit = {},
    onFinish: () -> Unit = {},
) {
    var selectedMode by rememberSaveable { mutableStateOf(AppMode.SINGLE) }
    Column(
        modifier = Modifier
            .padding(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr).plus(16.dp),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr).plus(16.dp),
                top = paddingValues.calculateTopPadding().plus(16.dp),
                bottom = paddingValues.calculateBottomPadding().plus(16.dp)
            )
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
            fontFamily = BrandFontFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
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
                .size(64.dp)
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
fun ModeSelectionScreenLandscape(
    paddingValues: PaddingValues = PaddingValues(),
    onModeSelected: (AppMode) -> Unit = {},
    onFinish: () -> Unit = {},
) {
    var selectedMode by rememberSaveable { mutableStateOf(AppMode.SINGLE) }
    Row(
        modifier = Modifier
            .padding(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr).plus(32.dp),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr).plus(32.dp),
                top = paddingValues.calculateTopPadding().plus(16.dp),
                bottom = paddingValues.calculateBottomPadding().plus(16.dp)
            )
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(end = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
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
                fontFamily = BrandFontFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            Text(
                text = stringResource(R.string.mode_select_desc),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().weight(2f).padding(horizontal = 16.dp).verticalScroll(
                rememberScrollState()
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
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

            Spacer(modifier = Modifier.weight(1f))

            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { onFinish() },
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .clip(CircleShape)
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        Icons.Default.Done,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
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