package com.surovtsev.settings.viewmodel.helpers
import com.surovtsev.core.settings.SettingsListData
import com.surovtsev.settings.dagger.SettingsScope
import javax.inject.Inject

@SettingsScope
class SettingsScreenEvents @Inject constructor(
    val settingsListData: SettingsListData,
)

