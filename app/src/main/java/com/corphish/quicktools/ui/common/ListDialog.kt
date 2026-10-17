package com.corphish.quicktools.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.corphish.quicktools.R
import com.tabotabo.ui.button.IconAction
import com.tabotabo.ui.button.PrimaryButton
import com.tabotabo.ui.button.SecondaryButton
import com.tabotabo.ui.surface.glowOutline
import com.tabotabo.ui.text.EmphasisText
import com.tabotabo.ui.theme.TabotaboTheme
import com.tabotabo.ui.typography.EmphasisWeights
import com.tabotabo.ui.typography.body

/** Size of the icon shown in each list item. */
private val ItemIconSize = 24.dp

/**
 * A list of selectable options (plain rows) shown inside an outlined dialog, styled like
 * `TabotaboDialog` but without confirm/dismiss buttons: picking an item is the action.
 *
 * @param title Dialog title; its last word is emphasised.
 * @param message Optional message under the title; hidden when empty.
 * @param list Items to show.
 * @param supportBack Whether to show a back arrow before the title.
 * @param onItemSelected Called with the index of the picked item.
 * @param stringSelector Label of an item.
 * @param iconSelector Drawable resource of an item's icon.
 * @param onBackPressed Called when the back arrow is tapped.
 * @param additionalContent Content shown below the list.
 * @param dismissible Whether back press and outside taps dismiss the dialog.
 * @param onDismissRequest Called when the dialog is dismissed.
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
    val colors = TabotaboTheme.colors
    val spacing = TabotaboTheme.dimensions.spacing

    DialogContainer(onDismissRequest = onDismissRequest, dismissible = dismissible) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (supportBack) {
                IconAction(onClick = onBackPressed, contentDescription = stringResource(R.string.back)) {
                    Icon(painterResource(R.drawable.ic_arrow_left), contentDescription = null)
                }
                Spacer(modifier = Modifier.width(spacing.xs))
            }

            DialogTitle(title)
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(spacing.s))
            Text(text = message, style = TabotaboTheme.typography.body, color = colors.muted)
        }

        Spacer(modifier = Modifier.height(spacing.l))

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
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

/**
 * Dialog with a form: a title, custom [content] and confirm/dismiss buttons, where the confirm
 * button can be disabled until the form is valid.
 *
 * @param title Dialog title; its last word is emphasised.
 * @param confirmText Label of the confirm button; it is hidden when null. With no buttons the
 *   dialog closes by tapping outside it or going back.
 * @param onConfirm Called when the confirm button is tapped.
 * @param onDismissRequest Called when the dialog is dismissed or the dismiss button is tapped.
 * @param modifier Modifier for the dialog container.
 * @param confirmEnabled Whether the confirm button is enabled.
 * @param dismissText Label of the dismiss button; it is hidden when null.
 * @param content Form content between the title and the buttons.
 */
@Composable
fun FormDialog(
    title: String,
    confirmText: String?,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    confirmEnabled: Boolean = true,
    dismissText: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val spacing = TabotaboTheme.dimensions.spacing

    DialogContainer(onDismissRequest = onDismissRequest, modifier = modifier) {
        DialogTitle(title)
        Spacer(modifier = Modifier.height(spacing.l))

        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
            content = content,
        )

        if (confirmText != null || dismissText != null) {
            Spacer(modifier = Modifier.height(spacing.xl))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.s, Alignment.End),
            ) {
                if (dismissText != null) {
                    SecondaryButton(text = dismissText, onClick = onDismissRequest)
                }
                if (confirmText != null) {
                    PrimaryButton(text = confirmText, onClick = onConfirm, enabled = confirmEnabled)
                }
            }
        }
    }
}

/**
 * Outlined dialog surface shared by the app's dialogs.
 *
 * @param onDismissRequest Called when the dialog is dismissed.
 * @param modifier Modifier for the surface.
 * @param dismissible Whether back press and outside taps dismiss the dialog.
 * @param content Dialog content.
 */
@Composable
private fun DialogContainer(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissible: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = TabotaboTheme.colors
    val dimensions = TabotaboTheme.dimensions
    val shape = RoundedCornerShape(dimensions.cardCorner)

    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            dismissOnBackPress = dismissible,
            dismissOnClickOutside = dismissible
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(colors.surface, shape)
                .glowOutline(colors.outline, dimensions.outlineWidth, shape)
                .padding(dimensions.spacing.xl),
            content = content,
        )
    }
}

/**
 * Dialog title with its last word emphasised.
 *
 * @param title Title text.
 */
@Composable
private fun DialogTitle(title: String) {
    EmphasisText(
        style = TabotaboTheme.typography.headlineSmall,
        weights = EmphasisWeights.Title,
    ) {
        FocusedLastWord(title)
    }
}

/**
 * One option of a [ListDialog]: a plain tappable row with an icon and a label, aligned with
 * the dialog title.
 *
 * @param text Label of the option.
 * @param icon Drawable resource of the option's icon.
 * @param onClick Called when the option is tapped.
 */
@Composable
fun ListDialogItem(text: String, @DrawableRes icon: Int, onClick: () -> Unit) {
    val spacing = TabotaboTheme.dimensions.spacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TabotaboTheme.dimensions.cardCorner))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(vertical = spacing.m),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(ItemIconSize),
            tint = TabotaboTheme.colors.primary
        )

        Text(
            text = text,
            style = TabotaboTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = TabotaboTheme.colors.accent,
            modifier = Modifier.padding(start = spacing.l)
        )
    }
}
