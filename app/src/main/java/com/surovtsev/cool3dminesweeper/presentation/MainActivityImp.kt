package com.surovtsev.cool3dminesweeper.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.surovtsev.cool3dminesweeper.presentation.gamescreen.GameScreen
import com.surovtsev.cool3dminesweeper.presentation.gamescreen.LoadGameParameterName
import com.surovtsev.cool3dminesweeper.presentation.helpscreen.HelpScreen
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.MainScreen
import com.surovtsev.cool3dminesweeper.presentation.rankingscreen.RankingScreen
import com.surovtsev.cool3dminesweeper.presentation.settingsscreen.SettingsScreen
import com.surovtsev.core.mainactivity.requestpermissionsresultreceiver.RequestPermissionsResult
import com.surovtsev.utils.compose.navigationanimationhelper.SimpleNavigationAnimationHelper
import com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.GameScreenViewModel
import com.surovtsev.cool3dminesweeper.viewmodels.helpscreenviewmodel.HelpScreenViewModel
import com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel.MainScreenViewModel
import com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel.SettingsScreenViewModel
import com.surovtsev.core.mainactivity.MainActivity
import com.surovtsev.ranking.rankinscreenviewmodel.RankingScreenViewModel
import dagger.hilt.android.AndroidEntryPoint
import logcat.logcat


@AndroidEntryPoint
class MainActivityImp: MainActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUI()
    }

    @OptIn(ExperimentalAnimationApi::class)
    private fun initUI() {
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
                ) {
                    val viewModel: MainScreenViewModel by viewModels()
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
                    val viewModel: GameScreenViewModel = hiltViewModel()
                    entry.lifecycle.addObserver(viewModel)
                    GameScreen(
                        viewModel,
                        this@MainActivityImp
                    )
                }
                composable(
                    route = Screen.RankingScreen.route,
                    enterTransition = navAnimHelper.concreteEnterSliding.fromLeft,
                    exitTransition = navAnimHelper.concreteExitException.toLeft
                ) { entry ->
                    val viewModel: RankingScreenViewModel = hiltViewModel()
                    entry.lifecycle.addObserver(viewModel)
                    RankingScreen(
                        this@MainActivityImp,
                        viewModel
                    )
                }
                composable(
                    route = Screen.SettingsScreen.route,
                    enterTransition = navAnimHelper.concreteEnterSliding.fromRight,
                    exitTransition = navAnimHelper.concreteExitException.toRight,
                ) { entry ->
                    val viewModel: SettingsScreenViewModel = hiltViewModel()
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
                    val viewModel: HelpScreenViewModel = hiltViewModel()
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