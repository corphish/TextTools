package com.corphish.quicktools.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.BuildConfig
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.R
import com.corphish.quicktools.data.Constants
import com.corphish.quicktools.features.Feature
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.Typography
import com.corphish.quicktools.ui.theme.TypographyV2
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickToolsTheme {
                Greeting()
            }
        }
    }
}

@Composable
fun Greeting() {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val mainScrollState = rememberScrollState()
    val contributorScrollState = rememberScrollState()

    Scaffold(
        floatingActionButton = {
        FloatingActionButton(
            onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            },
        ) {
            Icon(Icons.Filled.Settings, "Settings")
        }
    }) {
        Column(
            modifier = Modifier
                .padding(
                    top = it
                        .calculateTopPadding()
                        .plus(16.dp),
                    bottom = it
                        .calculateBottomPadding()
                        .plus(16.dp),
                    start = it
                        .calculateStartPadding(LayoutDirection.Ltr)
                        .plus(16.dp),
                    end = it
                        .calculateEndPadding(LayoutDirection.Ltr)
                        .plus(16.dp)
                )
                .verticalScroll(mainScrollState)
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = TypographyV2.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = BrandFontFamily,
                modifier = Modifier.padding(bottom = 8.dp)
            )


            Text(
                text = stringResource(id = R.string.app_desc), style = Typography.bodyMedium
            )

            Text(
                text = stringResource(id = R.string.features),
                style = TypographyV2.labelSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                fontFamily = BrandFontFamily,
                color = MaterialTheme.colorScheme.primary
            )

            for (feature in Feature.LIST) {
                FeatureItem(feature = feature)
            }

            Text(
                text = stringResource(id = R.string.oss_info),
                style = TypographyV2.labelSmall,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                fontFamily = BrandFontFamily,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stringResource(id = R.string.oss_desc), style = Typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .horizontalScroll(contributorScrollState)
            ) {
                Button(
                    onClick = { uriHandler.openUri("https://github.com/corphish/TextTools/") },
                ) {
                    Icon(painterResource(id = R.drawable.ic_open_in_new), contentDescription = "")
                    Text(
                        text = stringResource(id = R.string.oss_check),
                        modifier = Modifier.padding(start = 16.dp),
                        style = TypographyV2.labelMedium,
                        fontWeight = FontWeight.W600
                    )
                }

                Button(
                    onClick = { uriHandler.openUri("https://github.com/corphish/TextTools/blob/main/CONTRIBUTORS.md") },
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    Icon(painterResource(id = R.drawable.ic_open_in_new), contentDescription = "")
                    Text(
                        text = stringResource(id = R.string.contributors),
                        modifier = Modifier.padding(start = 16.dp),
                        style = TypographyV2.labelMedium,
                        fontWeight = FontWeight.W600
                    )
                }
            }

            // Placeholder so that FAB does not overlap the contents
            Box(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun FeatureItem(feature: Feature) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp)/*.clickable {
            val intent = Intent(context, TryOutActivity::class.java)
            intent.putExtra(TryOutActivity.TRY_OUT_FLOW, feature.flow)
            context.startActivity(intent)
        }*/
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary), contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(id = feature.icon),
                contentDescription = "",
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimary)
            )
        }


        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = stringResource(id = feature.featureTitle),
                style = TypographyV2.labelMedium,
                fontFamily = BrandFontFamily,
                fontWeight = FontWeight.W600
            )
            Text(text = stringResource(id = feature.featureDesc), style = Typography.bodyMedium)

            // Don't show context menu option in single option flavor
            if (BuildConfig.FLAVOR == Constants.FLAVOR_MULTIPLE_OPTIONS) {
                Row(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.context_menu_option),
                        style = Typography.labelMedium,
                    )
                    Text(
                        text = stringResource(id = feature.contextMenuText),
                        style = Typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuickToolsTheme {
        Greeting()
    }
}