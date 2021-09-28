package com.surovtsev.cool_3d_minesweeper.dagger.app.settings

import com.surovtsev.cool_3d_minesweeper.dagger.app.SettingsScope
import com.surovtsev.cool_3d_minesweeper.model_views.settings_activity_model_view.*
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import com.surovtsev.cool_3d_minesweeper.utils.minesweeper.database.SettingsDataHelper
import com.surovtsev.cool_3d_minesweeper.views.activities.SettingsActivity
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Named

@SettingsScope
@Subcomponent(
    modules = [
        SettingsModule::class
    ])
interface SettingsComponent {
    fun inject(settingsActivity: SettingsActivity)
}

@Module
object SettingsModule {

    @SettingsScope
    @Named(SettingsActivityControls.SelectedSettingsIdName)
    @Provides
    fun provideSelectedSettingsId(): MyLiveData<Int> {
        return MyLiveData(-1)
    }

    @SettingsScope
    @Provides
    fun provideSettingsList(): SettingsList {
        return SettingsList(listOf())
    }

    private fun createMyLiveDataForSlider(defValue: Int) = MyLiveData(defValue.toFloat())

//    @[IntoMap StringKey(SettingsData.xCountName)]
    @Provides
    @SettingsScope
    @Named(SettingsActivityControls.XCountSliderValueName)
    fun provideXCountSliderValue() = createMyLiveDataForSlider(
        SettingsData.xCountDefaultValue
    )

    @Provides
    @SettingsScope
    @Named(SettingsActivityControls.YCountSliderValueName)
    fun provideYCountSliderValue() = createMyLiveDataForSlider(
        SettingsData.yCountDefaultValue
    )

    @Provides
    @SettingsScope
    @Named(SettingsActivityControls.ZCountSliderValueName)
    fun provideZCountSliderValue() = createMyLiveDataForSlider(
        SettingsData.zCountDefaultValue
    )

    @Provides
    @SettingsScope
    @Named(SettingsActivityControls.BombsPercentageSliderValueName)
    fun provideBombsPercentageSliderValue() = createMyLiveDataForSlider(
        SettingsData.bombsPercentageDefaultValue
    )

    @Provides
    @SettingsScope
    fun provideSlidersWithNames(
        @Named(SettingsActivityControls.XCountSliderValueName)
        xCountSliderValue: SettingsSlider,
        @Named(SettingsActivityControls.YCountSliderValueName)
        yCountSliderValue: SettingsSlider,
        @Named(SettingsActivityControls.ZCountSliderValueName)
        zCountSliderValue: SettingsSlider,
        @Named(SettingsActivityControls.BombsPercentageSliderValueName)
        bombsPercentageSliderValue: SettingsSlider
    ): @JvmSuppressWildcards SlidersWithNames {
        return mapOf(
            SettingsData.xCountName to xCountSliderValue,
            SettingsData.yCountName to yCountSliderValue,
            SettingsData.zCountName to zCountSliderValue,
            SettingsData.bombsPercentageName to bombsPercentageSliderValue
        )
    }

    @Provides
    @SettingsScope
    fun provideSettingsDataFactory(
        settingsActivityControls: SettingsActivityControls
    ): () -> SettingsData {
        return {
            SettingsData(
                settingsActivityControls.xCountSliderValue.valueOrDefault.toInt(),
                settingsActivityControls.yCountSliderValue.valueOrDefault.toInt(),
                settingsActivityControls.zCountSliderValue.valueOrDefault.toInt(),
                settingsActivityControls.bombsPercentageSliderValue.valueOrDefault.toInt()
            )
        }
    }

    @Provides
    @SettingsScope
    fun provideSlidersInfo(
        slidersWithNames: @JvmSuppressWildcards SlidersWithNames
    ): @JvmSuppressWildcards List<Pair<String, Pair<IntRange, MyLiveData<Float>>>> {
        return slidersWithNames.map { (name, value) ->
            val border = SettingsDataHelper.borders[name]!!
            name to (border to value)
        }
    }
}
