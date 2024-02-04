package com.corphish.quicktools.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.ui.theme.QuickToolsTheme
import com.corphish.quicktools.R
import com.corphish.quicktools.features.Feature
import com.corphish.quicktools.ui.theme.Typography

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
            text = stringResource(id = R.string.app_desc),
            style = Typography.bodyMedium
        )
        Text(
            text = stringResource(id = R.string.features),
            style = Typography.labelMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.primary
        )

        for (feature in Feature.LIST) {
            FeatureItem(feature = feature)
        }
    }
}

@Composable
fun FeatureItem(feature: Feature) {
    val context = LocalContext.current
    Row(
        modifier = Modifier.padding(vertical = 4.dp).clickable {
            val intent = Intent(context, TryOutActivity::class.java)
            intent.putExtra(TryOutActivity.TRY_OUT_FLOW, feature.flow)
            context.startActivity(intent)
        }
    ) {
        Image(
            painterResource(id = feature.icon),
            contentDescription = "",
            modifier = Modifier.size(48.dp),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
        )

        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(text = stringResource(id = feature.featureTitle), style = Typography.labelLarge)
            Text(text = stringResource(id = feature.featureDesc), style = Typography.bodyMedium)
            Row {
                Text(
                    text = stringResource(id = R.string.context_menu_option),
                    style = Typography.labelMedium
                )
                Text(
                    text = stringResource(id = feature.contextMenuText),
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
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