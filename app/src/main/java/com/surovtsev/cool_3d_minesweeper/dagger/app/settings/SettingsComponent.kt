package com.surovtsev.cool_3d_minesweeper.dagger.app.settings

import com.surovtsev.cool_3d_minesweeper.dagger.app.SettingsScope
import com.surovtsev.cool_3d_minesweeper.model_views.settings_activity_view_model.*
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import com.surovtsev.cool_3d_minesweeper.utils.minesweeper.database.Borders
import com.surovtsev.cool_3d_minesweeper.utils.minesweeper.database.SettingsDataHelper
import com.surovtsev.cool_3d_minesweeper.views.activities.SettingsActivity
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Named

@SettingsScope
@Subcomponent(
    modules = [
        SettingsModule::class,
        SettingsDataHelperModule::class
    ]
)
interface SettingsComponent {
    val settingsActivityViewModel: SettingsActivityViewModel

    val slidersWithNames: @JvmSuppressWildcards SlidersWithNames

    fun inject(settingsActivity: SettingsActivity)
}

@Module
object SettingsModule {

    @SettingsScope
    @Named(SettingsActivityControls.SelectedSettingsIdName)
    @Provides
    fun provideSelectedSettingsId(): SelectedSettingsId {
        return SelectedSettingsId(-1)
    }

    @SettingsScope
    @Provides
    fun provideSettingsList(): SettingsList {
        return SettingsList(listOf())
    }

    private fun createMyLiveDataForSlider(defValue: Int) = MyLiveData(defValue.toFloat())

    @[IntoMap StringKey(SettingsData.xCountName)]
    @Provides
    @SettingsScope
    fun provideXCountSliderValue() = createMyLiveDataForSlider(
        SettingsData.xCountDefaultValue
    )

    @[IntoMap StringKey(SettingsData.yCountName)]
    @Provides
    @SettingsScope
    fun provideYCountSliderValue() = createMyLiveDataForSlider(
        SettingsData.yCountDefaultValue
    )

    @[IntoMap StringKey(SettingsData.zCountName)]
    @Provides
    @SettingsScope
    fun provideZCountSliderValue() = createMyLiveDataForSlider(
        SettingsData.zCountDefaultValue
    )

    @[IntoMap StringKey(SettingsData.bombsPercentageName)]
    @Provides
    @SettingsScope
    fun provideBombsPercentageSliderValue() = createMyLiveDataForSlider(
        SettingsData.bombsPercentageDefaultValue
    )

    @Provides
    @SettingsScope
    fun provideSettingsDataFactory(
        slidersWithNames: @JvmSuppressWildcards SlidersWithNames
    ): () -> SettingsData {
        return {
            SettingsData(
                slidersWithNames[SettingsData.xCountName]!!.valueOrDefault.toInt(),
                slidersWithNames[SettingsData.yCountName]!!.valueOrDefault.toInt(),
                slidersWithNames[SettingsData.zCountName]!!.valueOrDefault.toInt(),
                slidersWithNames[SettingsData.bombsPercentageName]!!.valueOrDefault.toInt()
            )
        }
    }

    @Provides
    @SettingsScope
    fun provideSlidersInfo(
        slidersWithNames: @JvmSuppressWildcards SlidersWithNames,
        borders: Borders
    ): @JvmSuppressWildcards SlidersInfo {
        return slidersWithNames.map { (name, value) ->
            val border = borders[name]!!
            name to (border to value)
        }
    }

}

@Module
object SettingsDataHelperModule {
    @Provides
    @SettingsScope
    @Named(SettingsDataHelper.DimParamsCount)
    fun provideDimParamsCount() = 3

    @Provides
    @SettingsScope
    @Named(SettingsDataHelper.DimBorders)
    fun provideDimBorders() = 3..25

    @Provides
    @SettingsScope
    @Named(SettingsDataHelper.BombsPercentageBorders)
    fun provideBombsPercentageBorders() = 10..99

    @Provides
    @SettingsScope
    fun provideBorders(
        @Named(SettingsDataHelper.DimParamsCount)
        dimParamsCount: Int,
        @Named(SettingsDataHelper.DimBorders)
        dimBorders: IntRange,
        @Named(SettingsDataHelper.BombsPercentageBorders)
        bombsPercentageBorders: IntRange
    ): Borders {
        return (SettingsDataHelper.paramNames.take(dimParamsCount).map {
                    it to dimBorders
                } + SettingsDataHelper.paramNames.drop(dimParamsCount).map {
                    it to bombsPercentageBorders
                }).toMap()

    }
}
