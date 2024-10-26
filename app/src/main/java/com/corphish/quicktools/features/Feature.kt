package com.corphish.quicktools.features

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.corphish.quicktools.R
import com.corphish.quicktools.activities.TryOutActivity

/**
 * Data class that defines a feature.
 */
data class Feature(
    @DrawableRes val icon: Int,
    @StringRes val featureTitle: Int,
    @StringRes val featureDesc: Int,
    @StringRes val contextMenuText: Int,
    val flow: String,
) {
    companion object {
        val LIST = listOf(
            Feature(
                icon = R.drawable.ic_whatsapp,
                featureTitle = R.string.wup_title,
                featureDesc = R.string.wup_desc,
                contextMenuText = R.string.context_menu_whatsapp,
                flow = TryOutActivity.TRY_OUT_FLOW_WUP,
            ),
            Feature(
                icon = R.drawable.ic_numbers,
                featureTitle = R.string.eval_title,
                featureDesc = R.string.eval_desc,
                contextMenuText = R.string.context_menu_eval,
                flow = TryOutActivity.TRY_OUT_FLOW_EVAL,
            ),
            Feature(
                icon = R.drawable.ic_text_transform,
                featureTitle = R.string.transform_long,
                featureDesc = R.string.transform_desc,
                contextMenuText = R.string.context_menu_transform,
                flow = TryOutActivity.TRY_OUT_FLOW
            ),
            Feature(
                icon = R.drawable.ic_text_count,
                featureTitle = R.string.text_count,
                featureDesc = R.string.text_count_desc,
                contextMenuText = R.string.context_menu_text_count,
                flow = TryOutActivity.TRY_OUT_FLOW
            ),
            Feature(
                icon = R.drawable.ic_save,
                featureTitle = R.string.save_text_title,
                featureDesc = R.string.save_text_desc,
                contextMenuText = R.string.context_menu_save,
                flow = TryOutActivity.TRY_OUT_FLOW
            ),
            Feature(
                icon = R.drawable.ic_find_and_replace,
                featureTitle = R.string.title_activity_find_and_replace,
                featureDesc = R.string.find_and_replace_desc,
                contextMenuText = R.string.context_menu_find_and_replace,
                flow = TryOutActivity.TRY_OUT_FLOW
            )
        )
    }
}
