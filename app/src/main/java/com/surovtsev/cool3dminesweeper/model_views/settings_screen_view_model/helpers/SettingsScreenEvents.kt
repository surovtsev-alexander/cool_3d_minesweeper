package com.surovtsev.cool3dminesweeper.model_views.settings_screen_view_model.helpers
import com.surovtsev.cool3dminesweeper.dagger.app.SettingsListWithIds
import com.surovtsev.cool3dminesweeper.dagger.app.SettingsScope
import javax.inject.Inject

@SettingsScope
class SettingsScreenEvents @Inject constructor(
    val settingsListWithIds: SettingsListWithIds,
)

