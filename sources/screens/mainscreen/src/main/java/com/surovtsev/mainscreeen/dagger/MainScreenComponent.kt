/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


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
