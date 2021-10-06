package com.surovtsev.cool_3d_minesweeper.model_views.settings_screen_view_model.helpers
import com.surovtsev.cool_3d_minesweeper.dagger.app.SettingsListWithIds
import com.surovtsev.cool_3d_minesweeper.dagger.app.SettingsScope
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import javax.inject.Inject

@SettingsScope
class SettingsScreenEvents @Inject constructor(
    val settingsListWithIds: SettingsListWithIds,
)

