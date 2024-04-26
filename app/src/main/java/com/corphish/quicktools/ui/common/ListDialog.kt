package com.corphish.quicktools.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    stringResourceSelector: (T) -> Int,
    onBackPressed: () -> Unit = {},
    additionalContent: @Composable ColumnScope.() -> Unit = {},
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
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
                        fontWeight = FontWeight.W200,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = if (supportBack) 16.dp else 0.dp)
                    )
                }

                Text(
                    text = message,
                    style = TypographyV2.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                for ((index, item) in list.withIndex()) {
                    Button(
                        onClick = {
                            onItemSelected(index)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = stringResourceSelector(item)), fontFamily = BrandFontFamily,)
                    }
                }

                additionalContent()
            }
        }
    }
}