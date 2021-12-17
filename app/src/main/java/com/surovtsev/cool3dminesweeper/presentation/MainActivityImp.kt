package com.surovtsev.cool3dminesweeper.presentation

import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.surovtsev.cool3dminesweeper.controllers.applicationcontroller.appComponent
import com.surovtsev.cool3dminesweeper.presentation.helpscreen.HelpScreen
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.MainScreen
import com.surovtsev.cool3dminesweeper.viewmodels.helpscreenviewmodel.HelpScreenViewModel
import com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel.MainScreenViewModel
import com.surovtsev.core.mainactivity.MainActivity
import com.surovtsev.core.mainactivity.requestpermissionsresultreceiver.RequestPermissionsResult
import com.surovtsev.utils.compose.navigationanimationhelper.SimpleNavigationAnimationHelper
import logcat.logcat

//class Factory<T: ViewModel>(
//    savedStateRegistryOwner: SavedStateRegistryOwner,
//    private val create: (stateHandle: SavedStateHandle) -> T
//) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, null) {
//
//    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
//        return create.invoke(handle) as T
//    }
//}
//
//inline fun <reified T : ViewModel> Fragment.lazyViewModel(
//    noinline create: (stateHandle: SavedStateHandle) -> T
//) = viewModels<T> {
//    Factory(this, create)
//}

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
                    val viewModel by viewModels<MainScreenViewModel> {  appComponent.viewModelFactory() }
                    MainScreen(
                        viewModel,
                        navController
                    )
                }
//                composable(
//                    route = Screen.GameScreen.route + "/{$LoadGameParameterName}",
//                    arguments = listOf(
//                        navArgument(LoadGameParameterName) {
//                            type = NavType.StringType
//                            defaultValue = "false"
//                            nullable = false
//                        }
//                    ),
//                    enterTransition = navAnimHelper.concreteEnterSliding.fromTop,
//                    exitTransition = navAnimHelper.concreteExitException.toTop,
//                ) { entry ->
//                    val viewModel: GameScreenViewModel = hiltViewModel()
//                    entry.lifecycle.addObserver(viewModel)
//                    GameScreen(
//                        viewModel,
//                        this@MainActivityImp,
//                        navController,
//                    )
//                }
//                composable(
//                    route = Screen.RankingScreen.route,
//                    enterTransition = navAnimHelper.concreteEnterSliding.fromLeft,
//                    exitTransition = navAnimHelper.concreteExitException.toLeft
//                ) { entry ->
//                    val viewModel: RankingScreenViewModel = hiltViewModel()
//                    entry.lifecycle.addObserver(viewModel)
//                    RankingScreen(
//                        viewModel,
//                        navController
//                    )
//                }
//                composable(
//                    route = Screen.SettingsScreen.route,
//                    enterTransition = navAnimHelper.concreteEnterSliding.fromRight,
//                    exitTransition = navAnimHelper.concreteExitException.toRight,
//                ) { entry ->
//                    val viewModel: SettingsScreenViewModel = hiltViewModel()
//                    entry.lifecycle.addObserver(viewModel)
//                    SettingsScreen(
//                        viewModel,
//                        navController
//                    )
//                }
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
