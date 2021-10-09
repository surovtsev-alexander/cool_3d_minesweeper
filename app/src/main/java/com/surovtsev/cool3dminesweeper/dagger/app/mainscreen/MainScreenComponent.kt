package com.surovtsev.cool3dminesweeper.dagger.app.mainscreen

import com.surovtsev.cool3dminesweeper.dagger.app.MainScreenScope
import com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel.MainScreenViewModel
import com.surovtsev.cool3dminesweeper.presentation.Screen
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.ButtonsInfo
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.MainScreenButtonInfo
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.MainScreenButtonType
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@MainScreenScope
@DefineComponent(
    parent = ViewModelComponent::class
)
interface MainScreenComponent {

    @DefineComponent.Builder
    interface Builder {
        fun build(): MainScreenComponent
    }
}

@MainScreenScope
@InstallIn(MainScreenComponent::class)
@EntryPoint
interface MainScreenEntryPoint {
    val buttonsInfo: ButtonsInfo
}

@Module
@InstallIn(MainScreenComponent::class)
object MainScreenModule {
    @[IntoMap StringKey(MainScreenViewModel.NewGame)]
    @MainScreenScope
    @Provides
    fun provideNewGameButtonInfo() =
        MainScreenButtonInfo(
            Screen.GameScreen,
            MainScreenViewModel.NewGame,
            MainScreenButtonType.NewGameButton
        )

    @[IntoMap StringKey(MainScreenViewModel.LoadGame)]
    @MainScreenScope
    @Provides
    fun provideLoadGameButtonInfo() =
        MainScreenButtonInfo(
            Screen.GameScreen,
            MainScreenViewModel.LoadGame,
            MainScreenButtonType.LoadGameButton
        )

    @[IntoMap StringKey(MainScreenViewModel.Ranking)]
    @MainScreenScope
    @Provides
    fun provideRankingButtonInfo() =
        MainScreenButtonInfo(
            Screen.RankingScreen,
            MainScreenViewModel.Ranking
        )

    @[IntoMap StringKey(MainScreenViewModel.Settings)]
    @MainScreenScope
    @Provides
    fun provideSettingsButtonInfo() =
        MainScreenButtonInfo(
            Screen.SettingsScreen,
            MainScreenViewModel.Settings
        )

    @[IntoMap StringKey(MainScreenViewModel.Help)]
    @MainScreenScope
    @Provides
    fun provideHelpButtonInfo() =
        MainScreenButtonInfo(
            Screen.HelpScreen,
            MainScreenViewModel.Help
        )
}
