package com.surovtsev.cool_3d_minesweeper.model_views.settings_activity_view_model
import com.surovtsev.cool_3d_minesweeper.dagger.app.SettingsScope
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import javax.inject.Inject

typealias SettingsList = MyLiveData<List<DataWithId<SettingsData>>>

@SettingsScope
class SettingsActivityEvents @Inject constructor(
    val settingsList: SettingsList,
)

