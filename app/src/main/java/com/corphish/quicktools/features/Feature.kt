package com.corphish.quicktools.features

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.corphish.quicktools.R

/**
 * Data class that defines a feature.
 */
data class Feature(
    @DrawableRes val icon: Int,
    @StringRes val featureTitle: Int,
    @StringRes val featureDesc: Int,
    @StringRes val contextMenuText: Int,
) {
    companion object {
        val LIST = listOf(
            Feature(
                icon = R.drawable.ic_whatsapp,
                featureTitle = R.string.wup_title,
                featureDesc = R.string.wup_desc,
                contextMenuText = R.string.context_menu_whatsapp,
            ),
            Feature(
                icon = R.drawable.ic_numbers,
                featureTitle = R.string.eval_title,
                featureDesc = R.string.eval_desc,
                contextMenuText = R.string.context_menu_eval,
            )
        )
    }
}
