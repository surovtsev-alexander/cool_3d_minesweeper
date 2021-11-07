package com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel.helpers
import com.surovtsev.cool3dminesweeper.dagger.app.SettingsListData
import com.surovtsev.cool3dminesweeper.dagger.app.SettingsScope
import javax.inject.Inject

@SettingsScope
class SettingsScreenEvents @Inject constructor(
    val settingsListData: SettingsListData,
)

