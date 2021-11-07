package com.surovtsev.cool3dminesweeper.dagger.app.settings

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.dagger.app.SettingsScope
import com.surovtsev.cool3dminesweeper.models.room.dao.SettingsDao
import com.surovtsev.cool3dminesweeper.models.room.entities.Settings
import com.surovtsev.cool3dminesweeper.models.room.entities.SettingsDataFactory
import com.surovtsev.cool3dminesweeper.utils.minesweeper.database.Borders
import com.surovtsev.cool3dminesweeper.utils.minesweeper.database.SettingsDataHelper
import com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel.helpers.*
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
    val settingsDao: SettingsDao
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

    private fun createMyLiveDataForSlider(defValue: Int) = SettingsSlider(defValue)

    @[IntoMap StringKey(Settings.SettingsData.Dimensions.ColumnNames.xCount)]
    @Provides
    @SettingsScope
    fun provideXCountSliderValue() = createMyLiveDataForSlider(
        Settings.SettingsData.Dimensions.DefaultValues.xCount
    )

    @[IntoMap StringKey(Settings.SettingsData.Dimensions.ColumnNames.yCount)]
    @Provides
    @SettingsScope
    fun provideYCountSliderValue() = createMyLiveDataForSlider(
        Settings.SettingsData.Dimensions.DefaultValues.yCount
    )

    @[IntoMap StringKey(Settings.SettingsData.Dimensions.ColumnNames.zCount)]
    @Provides
    @SettingsScope
    fun provideZCountSliderValue() = createMyLiveDataForSlider(
        Settings.SettingsData.Dimensions.DefaultValues.zCount
    )

    @[IntoMap StringKey(Settings.SettingsData.ColumnNames.bombsPercentage)]
    @Provides
    @SettingsScope
    fun provideBombsPercentageSliderValue() = createMyLiveDataForSlider(
        Settings.SettingsData.DefaultValues.bombsPercentage
    )

    @Provides
    @SettingsScope
    fun provideSettingsDataFactory(
        slidersWithNames: @JvmSuppressWildcards SlidersWithNames
    ): SettingsDataFactory {
        return {
            Settings.SettingsData(
                Settings.SettingsData.Dimensions(
                    slidersWithNames[Settings.SettingsData.Dimensions.ColumnNames.xCount]!!.valueOrDefault,
                    slidersWithNames[Settings.SettingsData.Dimensions.ColumnNames.yCount]!!.valueOrDefault,
                    slidersWithNames[Settings.SettingsData.Dimensions.ColumnNames.zCount]!!.valueOrDefault,
                ),
                slidersWithNames[Settings.SettingsData.ColumnNames.bombsPercentage]!!.valueOrDefault
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
    fun provideBombsPercentageBorders() = 10..40

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
