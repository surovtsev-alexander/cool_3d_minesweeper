package com.surovtsev.mainscreeen.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.presentation.Screen
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.mainscreeen.presentation.ButtonsInfo
import com.surovtsev.mainscreeen.presentation.MainScreenButtonInfo
import com.surovtsev.mainscreeen.presentation.MainScreenButtonType
import com.surovtsev.mainscreeen.viewmodel.MainScreenViewModel
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey

@MainScreenScope
@Component(
    dependencies = [
        AppComponentEntryPoint::class,
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
