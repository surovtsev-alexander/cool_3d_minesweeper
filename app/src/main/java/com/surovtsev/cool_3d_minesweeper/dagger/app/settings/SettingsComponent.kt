package com.surovtsev.cool_3d_minesweeper.dagger.app.settings

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool_3d_minesweeper.dagger.app.SettingsScope
import com.surovtsev.cool_3d_minesweeper.model_views.settings_activity_view_model.*
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsDataFactory
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import com.surovtsev.cool_3d_minesweeper.utils.minesweeper.database.Borders
import com.surovtsev.cool_3d_minesweeper.utils.minesweeper.database.SettingsDataHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import javax.inject.Named

@DefineComponent(
    parent = ViewModelComponent::class
)
@SettingsScope
interface SettingsComponent {


    @DefineComponent.Builder
    interface Builder {
        fun build(): SettingsComponent
    }
}

@InstallIn(SettingsComponent::class)
@EntryPoint
@SettingsScope
interface SettingsComponentEntryPoint {
    val slidersWithNames: @JvmSuppressWildcards SlidersWithNames

    val settingsActivityControls: SettingsActivityControls
    val settingsDBQueries: SettingsDBQueries
    val saveController: SaveController
    val settingsActivityEvents: SettingsActivityEvents
    val settingsDataFactory: SettingsDataFactory
}

//@SettingsScope
//@Subcomponent(
//    modules = [
//        SettingsModule::class,
//        SettingsDataHelperModule::class
//    ]
//)
//interface SettingsComponent {
//    val settingsActivityViewModel: SettingsActivityViewModel
//
//    val slidersWithNames: @JvmSuppressWildcards SlidersWithNames
//}
//

@Module
@InstallIn(SettingsComponent::class)
object SettingsModule {

    @SettingsScope
    @Named(SettingsActivityControls.SelectedSettingsIdName)
    @Provides
    fun provideSelectedSettingsId(): SelectedSettingsId {
        return SelectedSettingsId(-1)
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
    ): SettingsDataFactory {
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
@InstallIn(SettingsComponent::class)
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
