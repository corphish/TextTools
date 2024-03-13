package com.corphish.quicktools.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.BuildConfig
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.R
import com.corphish.quicktools.features.Feature
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.Typography
import com.corphish.quicktools.ui.theme.TypographyV2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickToolsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = TypographyV2.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = BrandFontFamily,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = stringResource(id = R.string.app_desc),
            style = Typography.bodyMedium
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
            text = stringResource(id = R.string.oss_desc),
            style = Typography.bodyMedium
        )

        Button(
            onClick = { uriHandler.openUri("https://github.com/corphish/TextTools/") },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(painterResource(id = R.drawable.ic_open_in_new), contentDescription = "")
            Text(
                text = stringResource(id = R.string.oss_check),
                modifier = Modifier.padding(start = 16.dp),
                style = TypographyV2.labelMedium,
                fontWeight = FontWeight.W600
            )
        }

        Text(
            text = stringResource(id = R.string.app_info),
            style = TypographyV2.labelSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            fontFamily = BrandFontFamily,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(
                id = R.string.app_version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            ),
            style = Typography.bodyMedium
        )

    }
}

@Composable
fun FeatureItem(feature: Feature) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp)/*.clickable {
            val intent = Intent(context, TryOutActivity::class.java)
            intent.putExtra(TryOutActivity.TRY_OUT_FLOW, feature.flow)
            context.startActivity(intent)
        }*/
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuickToolsTheme {
        Greeting()
    }
}