package com.surovtsev.cool3dminesweeper.dagger.app.mainscreen

import com.surovtsev.cool3dminesweeper.dagger.app.AppComponent
import com.surovtsev.cool3dminesweeper.dagger.app.MainScreenScope
import com.surovtsev.cool3dminesweeper.presentation.Screen
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.ButtonsInfo
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.MainScreenButtonInfo
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.MainScreenButtonType
import com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel.MainScreenViewModel
import com.surovtsev.core.savecontroller.SaveController
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@MainScreenScope
@Component(
    dependencies = [
        AppComponent::class,
    ],
    modules = [
        MainScreenModule::class,
    ]
)
interface MainScreenComponent {
    val buttonInfo: ButtonsInfo
    val saveController: SaveController
}

@Module
object MainScreenModule {
    @[IntoMap StringKey(MainScreenViewModel.ButtonNames.NewGame)]
    @MainScreenScope
    @Provides
    fun provideNewGameButtonInfo() =
        MainScreenButtonInfo(
            Screen.GameScreen,
            MainScreenButtonType.NewGameButton
        )

    @[IntoMap StringKey(MainScreenViewModel.ButtonNames.LoadGame)]
    @MainScreenScope
    @Provides
    fun provideLoadGameButtonInfo() =
        MainScreenButtonInfo(
            Screen.GameScreen,
            MainScreenButtonType.LoadGameButton
        )

    @[IntoMap StringKey(MainScreenViewModel.ButtonNames.Ranking)]
    @MainScreenScope
    @Provides
    fun provideRankingButtonInfo() =
        MainScreenButtonInfo(
            Screen.RankingScreen
        )

    @[IntoMap StringKey(MainScreenViewModel.ButtonNames.Settings)]
    @MainScreenScope
    @Provides
    fun provideSettingsButtonInfo() =
        MainScreenButtonInfo(
            Screen.SettingsScreen
        )

    @[IntoMap StringKey(MainScreenViewModel.ButtonNames.Help)]
    @MainScreenScope
    @Provides
    fun provideHelpButtonInfo() =
        MainScreenButtonInfo(
            Screen.HelpScreen
        )
}
