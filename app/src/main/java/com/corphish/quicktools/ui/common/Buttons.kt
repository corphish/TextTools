package com.corphish.quicktools.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

/** Size of icons shown inside buttons and icon actions. */
private val ButtonIconSize = 20.dp

/**
 * Icon sized for a button's leading/trailing slot or an `IconAction`.
 *
 * @param icon Drawable resource of the icon.
 * @param contentDescription Accessibility label; null when the button already has a label.
 * @param tint Icon color; defaults to the surrounding content color.
 */
@Composable
fun ButtonIcon(
    @DrawableRes icon: Int,
    contentDescription: String? = null,
    tint: Color = LocalContentColor.current,
) {
    Icon(
        painterResource(icon),
        contentDescription = contentDescription,
        modifier = Modifier.size(ButtonIconSize),
        tint = tint
    )
}
