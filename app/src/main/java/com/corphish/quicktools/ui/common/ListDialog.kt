package com.corphish.quicktools.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.TypographyV2

/**
 * A list of selectable options shown inside a dialog.
 */
@Composable
fun <T> ListDialog(
    title: String,
    message: String,
    list: List<T>,
    supportBack: Boolean = false,
    onItemSelected: (Int) -> Unit,
    stringSelector: @Composable (T) -> String,
    iconSelector: @Composable (T) -> Int,
    onBackPressed: () -> Unit = {},
    additionalContent: @Composable ColumnScope.() -> Unit = {},
    dismissible: Boolean = true,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = dismissible,
            dismissOnClickOutside = dismissible
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    if (supportBack) {
                        IconButton(
                            onClick = { onBackPressed() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(48.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                                contentDescription = "",
                                modifier = Modifier.size(32.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Text(
                        text = title,
                        style = TypographyV2.headlineMedium,
                        fontFamily = BrandFontFamily,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(
                            start = if (supportBack) 16.dp else 0.dp,
                            bottom = if (message.isEmpty()) 16.dp else 0.dp
                        )
                    )
                }

                if (!message.isEmpty()) {
                    Text(
                        text = message,
                        style = TypographyV2.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    for ((index, item) in list.withIndex()) {
                        ListDialogItem(text = stringSelector(item), icon = iconSelector(item)) {
                            onItemSelected(index)
                        }
                    }
                }

                additionalContent()
            }
        }
    }
}

@Composable
fun ListDialogItem(text: String, @DrawableRes icon: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painterResource(id = icon),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimary)
                )
            }

            Text(
                text = text,
                style = TypographyV2.labelMedium,
                fontFamily = BrandFontFamily,
                fontWeight = FontWeight.W400,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}