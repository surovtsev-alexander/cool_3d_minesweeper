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


package com.surovtsev.cool3dminesweeper.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.savedstate.SavedStateRegistryOwner
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.surovtsev.cool3dminesweeper.app.appComponent
import com.surovtsev.core.presentation.Screen
import com.surovtsev.gamescreen.presentation.GameScreen
import com.surovtsev.gamescreen.viewmodel.GameScreenViewModel
import com.surovtsev.gamescreen.viewmodel.LoadGameParameterName
import com.surovtsev.mainscreeen.presentation.MainScreen
import com.surovtsev.mainscreeen.viewmodel.MainScreenViewModel
import com.surovtsev.rankingscreen.presentation.RankingScreen
import com.surovtsev.rankingscreen.rankinscreenviewmodel.RankingScreenViewModel
import com.surovtsev.settingsscreen.presentation.SettingsScreen
import com.surovtsev.settingsscreen.viewmodel.SettingsScreenViewModel
import com.surovtsev.utils.compose.components.scrollbar.UIHelper
import com.surovtsev.utils.compose.navigationanimationhelper.SimpleNavigationAnimationHelper
import com.surovtsev.utils.dagger.savedstateviewmodelfactory.SavedStateViewModelFactory
import com.surovtsev.videotutorialscreen.presentation.VideoTutorialScreen
import com.surovtsev.videotutorialscreen.viewmodel.VideoTutorialScreenViewModel

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        initUI()
    }

    @OptIn(ExperimentalAnimationApi::class)
    private fun initUI() {
        val savedStateRegistryOwner: SavedStateRegistryOwner = this
        val context = applicationContext
        val appComponent = appComponent

        val metrics = applicationContext.resources.displayMetrics
        val activityWidth = metrics.widthPixels
        val activityHeight = metrics.heightPixels

        val dipCoefficient = UIHelper.calculateDIPCoefficient(resources.displayMetrics)

        setContent {
            val navAnimHelper = SimpleNavigationAnimationHelper(
                offsetX = activityWidth,
                offsetY = activityHeight
            )

            val navController = rememberAnimatedNavController()
            AnimatedNavHost(
                navController = navController,
                startDestination = Screen.MainScreen.route
            ) {
                composable(
                    route = Screen.MainScreen.route,
                    exitTransition = {
                        fadeOut(
                            animationSpec = navAnimHelper.fadingTween
                        )
                    },
                    enterTransition = {
                        fadeIn(
                            animationSpec = navAnimHelper.fadingTween
                        )
                    }
                ) { entry ->
                    val viewModel: MainScreenViewModel by viewModels {
                        SavedStateViewModelFactory(savedStateRegistryOwner) { stateHandler ->
                            appComponent.mainScreenViewModelFactory.build(
                                stateHandler, context, appComponent
                            )
                        }

                    }
                    entry.lifecycle.addObserver(viewModel)
                    MainScreen(
                        viewModel,
                        navController
                    )
                }
                composable(
                    route = Screen.GameScreen.route + "/{$LoadGameParameterName}",
                    arguments = listOf(
                        navArgument(LoadGameParameterName) {
                            type = NavType.StringType
                            defaultValue = "false"
                            nullable = false
                        }
                    ),
                    enterTransition = navAnimHelper.concreteEnterSliding.fromTop,
                    exitTransition = navAnimHelper.concreteExitSliding.toTop,
                ) { entry ->
                    val viewModel: GameScreenViewModel by viewModels {
                        SavedStateViewModelFactory(savedStateRegistryOwner) { stateHandler ->
                            appComponent.gameScreenViewModelFactory.build(
                                stateHandler, context, appComponent
                            )
                        }
                    }
                    entry.lifecycle.addObserver(viewModel)
                    val loadGame = entry.arguments!![LoadGameParameterName].toString().toBoolean()
                    GameScreen(
                        viewModel,
                        this@MainActivity,
                        navController,
                        context,
                        loadGame
                    )
                }
                composable(
                    route = Screen.RankingScreen.route,
                    enterTransition = navAnimHelper.concreteEnterSliding.fromLeft,
                    exitTransition = navAnimHelper.concreteExitSliding.toLeft
                ) { entry ->
                    val viewModel: RankingScreenViewModel by viewModels {
                        SavedStateViewModelFactory(savedStateRegistryOwner) { stateHandler ->
                            appComponent.rankingScreenViewModelFactory.build(
                                stateHandler, context, appComponent
                            )
                        }
                    }
                    entry.lifecycle.addObserver(viewModel)
                    RankingScreen(
                        viewModel,
                        navController,
                        dipCoefficient,
                    )
                }
                composable(
                    route = Screen.SettingsScreen.route,
                    enterTransition = navAnimHelper.concreteEnterSliding.fromRight,
                    exitTransition = navAnimHelper.concreteExitSliding.toRight,
                ) { entry ->
                    val viewModel: SettingsScreenViewModel by viewModels {
                        SavedStateViewModelFactory(savedStateRegistryOwner) { stateHandler ->
                            appComponent.settingsScreenViewModelFactory.build(
                                stateHandler, context, appComponent
                            )
                        }
                    }
                    entry.lifecycle.addObserver(viewModel)
                    SettingsScreen(
                        viewModel,
                        navController,
                        dipCoefficient,
                    )
                }
                composable(
                    route = Screen.VideoTutorialScreen.route,
                    enterTransition = navAnimHelper.concreteEnterSliding.fromBottom,
                    exitTransition = navAnimHelper.concreteExitSliding.toBottom,
                ) { entry ->
                    val viewModel = VideoTutorialScreenViewModel()
                    entry.lifecycle.addObserver(viewModel)
                    VideoTutorialScreen(viewModel)
                }
            }
        }
    }
}
