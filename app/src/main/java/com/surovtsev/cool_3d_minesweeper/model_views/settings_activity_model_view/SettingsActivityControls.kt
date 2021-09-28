package com.surovtsev.cool_3d_minesweeper.model_views.settings_activity_model_view

import com.surovtsev.cool_3d_minesweeper.dagger.app.SettingsScope
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import javax.inject.Inject
import javax.inject.Named


typealias SelectedSettingsId = MyLiveData<Int>
typealias SettingsSlider = MyLiveData<Float>
typealias SlidersWithNames = Map<String, SettingsSlider>
typealias SlidersInfo = List<Pair<String, Pair<IntRange, MyLiveData<Float>>>>

@SettingsScope
class SettingsActivityControls @Inject constructor(
    @Named(SelectedSettingsIdName)
    val selectedSettingsId: SelectedSettingsId,
    val slidersWithNames: @JvmSuppressWildcards SlidersWithNames,
    val slidersInfo: @JvmSuppressWildcards SlidersInfo
) {
    companion object {
        const val SelectedSettingsIdName = "selectedSettingsId"
    }
}
