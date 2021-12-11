package com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel.helpers
import com.surovtsev.cool3dminesweeper.dagger.app.SettingsScope
import com.surovtsev.core.settings.SettingsListData
import javax.inject.Inject

@SettingsScope
class SettingsScreenEvents @Inject constructor(
    val settingsListData: SettingsListData,
)

