package com.corphish.quicktools.ui.common

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tabotabo.ui.settings.SettingsSection
import com.tabotabo.ui.surface.GlowCard

/**
 * Outlined card with a title, a muted description and optional content (usually buttons).
 *
 * @param title Title of the card.
 * @param description Muted explanation under the title.
 * @param modifier Modifier for the card.
 * @param highlighted Whether the card glows to draw attention.
 * @param content Content below the description.
 */
@Composable
fun InfoCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    GlowCard(modifier = modifier.fillMaxWidth(), highlighted = highlighted) {
        SettingsSection(title = title, description = description, content = content)
    }
}
