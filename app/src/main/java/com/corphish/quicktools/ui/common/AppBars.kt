@file:OptIn(ExperimentalMaterial3Api::class)

package com.corphish.quicktools.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.corphish.quicktools.ui.theme.BrandFontFamily
import com.corphish.quicktools.ui.theme.TypographyV2

@Composable
fun CustomTopAppBar(
    @StringRes id: Int,
    hideTitle: Boolean = false,
    onNavigationClick: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
) {
    MediumTopAppBar(
        title = {
            if (!hideTitle) {
                Text(
                    text = stringResource(id = id),
                    style = TypographyV2.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = BrandFontFamily,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { onNavigationClick() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                    contentDescription = "",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}