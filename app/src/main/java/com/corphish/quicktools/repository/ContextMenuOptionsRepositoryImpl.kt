package com.corphish.quicktools.repository

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.edit
import androidx.preference.PreferenceManager

/**
 * Implementation of the [ContextMenuOptionsRepository].
 * This class is also responsible for enabling/disabling actual components.
 */
class ContextMenuOptionsRepositoryImpl(private val context: Context) :
    ContextMenuOptionsRepository {
    private val _modeKey = "context_menu_mode"
    private val _featuresKeySuffix = "context_menu_features_"
    private val _sharedPreferenceManager = PreferenceManager.getDefaultSharedPreferences(context)
    private val _packageManager = context.packageManager

    // Manifest name mappings
    private val _optionsActivityName = context.packageName + ".activities.OptionsActivity"
    private val _multiFeatureAliasMapping = mapOf(
        FeatureIds.EVAL to context.packageName + ".activities.EvalActivityAlias",
        FeatureIds.WHATSAPP to context.packageName + ".activities.WUPActivityAlias",
        FeatureIds.TRANSFORM to context.packageName + ".activities.TransformActivityAlias",
        FeatureIds.TEXT_COUNT to context.packageName + ".activities.TextCountActivityAlias",
        FeatureIds.FIND_AND_REPLACE to context.packageName + ".activities.FindAndReplaceActivityAlias",
        FeatureIds.SAVE_TEXT to context.packageName + ".activities.SaveTextActivityAlias"
    )

    override fun getCurrentAppMode(): AppMode {
        val mode = _sharedPreferenceManager.getString(_modeKey, AppMode.SINGLE.name)
            ?: return AppMode.SINGLE

        return AppMode.valueOf(mode)
    }

    override fun setCurrentAppMode(mode: AppMode) {
        _sharedPreferenceManager.edit {
            putString(_modeKey, mode.name)
        }

        switchModeTo(mode)
    }

    private fun switchModeTo(mode: AppMode) {
        when (mode) {
            AppMode.SINGLE -> {
                // Enable options activity
                val optionsActivityComp = ComponentName(context.packageName, _optionsActivityName)
                _packageManager.setComponentEnabledSetting(
                    optionsActivityComp,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )

                // Disable all multi feature aliases
                _multiFeatureAliasMapping.forEach {
                    _packageManager.setComponentEnabledSetting(
                        ComponentName(context.packageName, it.value),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
            }

            AppMode.MULTI -> {
                // Disable options activity
                val optionsActivityComp = ComponentName(context.packageName, _optionsActivityName)
                _packageManager.setComponentEnabledSetting(
                    optionsActivityComp,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )

                // Enable all the multi feature aliases
                val currentFeatures = getCurrentlyEnabledFeatures()
                _multiFeatureAliasMapping.forEach {
                    _packageManager.setComponentEnabledSetting(
                        ComponentName(context.packageName, it.value),
                        if (currentFeatures.contains(it.key)) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }
            }
        }
    }

    override fun getCurrentlyEnabledFeatures(): List<FeatureIds> {
        val enabledFeatures = mutableListOf<FeatureIds>()
        _multiFeatureAliasMapping.forEach { (featureIds, _) ->
            if (_sharedPreferenceManager.getBoolean(_featuresKeySuffix + featureIds.name, true)) {
                enabledFeatures.add(featureIds)
            }
        }

        return enabledFeatures
    }

    override fun enableOrDisableFeature(feature: FeatureIds, enabled: Boolean) {
        _sharedPreferenceManager.edit {
            putBoolean(_featuresKeySuffix + feature.name, enabled)
        }

        // Enable or disable the component if we are in multi mode
        // For single no action required
        val mode = getCurrentAppMode()
        if (mode == AppMode.MULTI) {
            val featureAlias = _multiFeatureAliasMapping[feature] ?: return
            val component = ComponentName(context.packageName, featureAlias)
            _packageManager.setComponentEnabledSetting(
                component,
                if (enabled) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}