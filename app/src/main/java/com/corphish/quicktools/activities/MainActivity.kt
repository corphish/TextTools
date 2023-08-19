package com.corphish.quicktools.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
            style = Typography.titleLarge
        )
        Text(
            text = stringResource(id = R.string.app_desc),
            style = Typography.bodySmall
        )
        Text(
            text = stringResource(id = R.string.features),
            style = Typography.labelSmall,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )
        
        for (feature in Feature.LIST) {
            FeatureItem(feature = feature)
        }
    }
}

@Composable
fun FeatureItem(feature: Feature) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Image(
            painterResource(id = feature.icon),
            contentDescription = "",
            modifier = Modifier.size(48.dp)
        )

        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(text = stringResource(id = feature.featureTitle), style = Typography.labelMedium)
            Text(text = stringResource(id = feature.featureDesc), style = Typography.bodySmall)
            Row {
                Text(text = stringResource(id = R.string.context_menu_option), style = Typography.labelSmall)
                Text(text = stringResource(id = feature.contextMenuText), style = Typography.bodySmall)
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