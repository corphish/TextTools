package com.corphish.quicktools.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.ui.theme.BrandFontFamily

@Composable
fun CircularButtonWithText(
    modifier: Modifier = Modifier,
    imageVector: ImageVector? = null,
    painterResource: Painter? = null,
    enabled: Boolean = true,
    text: String = "",
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.clickable {
            if (enabled) {
                onClick()
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { onClick() },
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = Color.Gray
            ),
            modifier = Modifier
                .clip(CircleShape)
                .size(48.dp)
        ) {
            if (imageVector != null) {
                Icon(
                    imageVector = imageVector,
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )
            } else if (painterResource != null) {
                Icon(
                    painter = painterResource,
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Text(text = text, color = if (enabled) MaterialTheme.colorScheme.onBackground else Color.Gray, fontFamily = BrandFontFamily)
    }
}