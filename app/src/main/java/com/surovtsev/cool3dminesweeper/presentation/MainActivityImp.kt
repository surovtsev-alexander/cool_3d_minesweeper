package com.surovtsev.cool3dminesweeper.presentation

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.savedstate.SavedStateRegistryOwner
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.surovtsev.cool3dminesweeper.controllers.applicationcontroller.appComponent
import com.surovtsev.cool3dminesweeper.presentation.helpscreen.HelpScreen
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.MainScreen
import com.surovtsev.cool3dminesweeper.viewmodels.helpscreenviewmodel.HelpScreenViewModel
import com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel.MainScreenViewModel
import com.surovtsev.cool3dminesweeper.unused.test.MyViewModel
import com.surovtsev.core.mainactivity.MainActivity
import com.surovtsev.core.mainactivity.requestpermissionsresultreceiver.RequestPermissionsResult
import com.surovtsev.game.presentation.GameScreen
import com.surovtsev.game.viewmodel.GameScreenViewModel
import com.surovtsev.game.viewmodel.LoadGameParameterName
import com.surovtsev.ranking.presentation.RankingScreen
import com.surovtsev.ranking.rankinscreenviewmodel.RankingScreenViewModel
import com.surovtsev.settings.presentation.SettingsScreen
import com.surovtsev.settings.viewmodel.SettingsScreenViewModel
import com.surovtsev.utils.compose.navigationanimationhelper.SimpleNavigationAnimationHelper
import com.surovtsev.utils.dagger.savedstateviewmodelfactory.SavedStateViewModelFactory
import logcat.logcat

class MainActivityImp: MainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    exitTransition = navAnimHelper.concreteExitException.toTop,
                ) { entry ->
                    val viewModel: GameScreenViewModel by viewModels {
                        SavedStateViewModelFactory(savedStateRegistryOwner) { stateHadler ->
                            appComponent.gameScreenViewModelFactory.build(
                                stateHadler, context, appComponent
                            )
                        }
                    }
                    entry.lifecycle.addObserver(viewModel)
                    val loadGame = entry.arguments!![LoadGameParameterName].toString().toBoolean()
                    GameScreen(
                        viewModel,
                        this@MainActivityImp,
                        navController,
                        context,
                        loadGame
                    )
                }
                composable(
                    route = Screen.RankingScreen.route,
                    enterTransition = navAnimHelper.concreteEnterSliding.fromLeft,
                    exitTransition = navAnimHelper.concreteExitException.toLeft
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
                        navController
                    )
                }
                composable(
                    route = Screen.SettingsScreen.route,
                    enterTransition = navAnimHelper.concreteEnterSliding.fromRight,
                    exitTransition = navAnimHelper.concreteExitException.toRight,
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
                        navController
                    )
                }
                composable(
                    route = Screen.HelpScreen.route,
                    enterTransition = navAnimHelper.concreteEnterSliding.fromBottom,
                    exitTransition = navAnimHelper.concreteExitException.toBottom,
                ) { entry ->
                    val viewModel: HelpScreenViewModel = HelpScreenViewModel()
                    entry.lifecycle.addObserver(viewModel)
                    HelpScreen(viewModel)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        /* TODO: refactor obsolete request permissions code */
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        requestPermissionsResultReceiver?.handleRequestPermissionsResult(
            RequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        )

        logcat { "onRequestPermissionsResult. requestCode: $requestCode, permissions: $permissions, grantResults: $grantResults" }
    }
}
