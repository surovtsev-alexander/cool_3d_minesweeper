package com.surovtsev.cool_3d_minesweeper.model_views.settings_activity_model_view

import com.surovtsev.cool_3d_minesweeper.dagger.app.SettingsScope
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import javax.inject.Inject
import javax.inject.Named


typealias SettingsSlider = MyLiveData<Float>
typealias SlidersWithNames = Map<String, SettingsSlider>

@SettingsScope
class SettingsActivityControls @Inject constructor(
    @Named(SelectedSettingsIdName)
    val selectedSettingsId: MyLiveData<Int>,
    @Named(XCountSliderValueName)
    val xCountSliderValue: MyLiveData<Float>,
    @Named(YCountSliderValueName)
    val yCountSliderValue: MyLiveData<Float>,
    @Named(ZCountSliderValueName)
    val zCountSliderValue: MyLiveData<Float>,
    @Named(BombsPercentageSliderValueName)
    val bombsPercentageSliderValue: SettingsSlider,
    val slidersWithNames: @JvmSuppressWildcards SlidersWithNames,
    val slidersInfo: @JvmSuppressWildcards List<Pair<String, Pair<IntRange, MyLiveData<Float>>>>
) {
    companion object {
        const val SelectedSettingsIdName = "selectedSettingsId"

        const val XCountSliderValueName = "xCountSliderValue"
        const val YCountSliderValueName = "yCountSliderValue"
        const val ZCountSliderValueName = "zCountSliderValue"
        const val BombsPercentageSliderValueName = "bombsPercentageSliderValue"
    }
}
