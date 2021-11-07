package com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel.helpers

import com.surovtsev.cool3dminesweeper.dagger.app.SettingsScope
import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData
import javax.inject.Inject
import javax.inject.Named


typealias SelectedSettingsId = MyLiveData<Long>
typealias SettingsSlider = MyLiveData<Int>
typealias SlidersWithNames = Map<String, SettingsSlider>
typealias SlidersInfo = List<Pair<String, Pair<IntRange, SettingsSlider>>>

@SettingsScope
class SettingsScreenControls @Inject constructor(
    @Named(SelectedSettingsIdName)
    val selectedSettingsId: SelectedSettingsId,
    val slidersWithNames: @JvmSuppressWildcards SlidersWithNames,
    val slidersInfo: @JvmSuppressWildcards SlidersInfo
) {
    companion object {
        const val SelectedSettingsIdName = "selectedSettingsId"
    }
}
