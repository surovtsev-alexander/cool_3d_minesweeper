package com.surovtsev.cool3dminesweeper.dagger.app.settings

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool3dminesweeper.dagger.app.SettingsScope
import com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel.helpers.*
import com.surovtsev.cool3dminesweeper.models.game.database.SettingsData
import com.surovtsev.cool3dminesweeper.models.game.database.SettingsDataFactory
import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData
import com.surovtsev.cool3dminesweeper.utils.minesweeper.database.Borders
import com.surovtsev.cool3dminesweeper.utils.minesweeper.database.SettingsDataHelper
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

    val settingsScreenComponent: SettingsScreenControls
    val settingsDBQueries: SettingsDBQueries
    val saveController: SaveController
    val settingsScreenEvents: SettingsScreenEvents
    val settingsDataFactory: SettingsDataFactory
}

@Module
@InstallIn(SettingsComponent::class)
object SettingsModule {

    @SettingsScope
    @Named(SettingsScreenControls.SelectedSettingsIdName)
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
