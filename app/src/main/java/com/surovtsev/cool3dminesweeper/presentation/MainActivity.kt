package com.surovtsev.cool3dminesweeper.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.GameScreenViewModel
import com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel.MainScreenViewModel
import com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.RankingScreenViewModel
import com.surovtsev.cool3dminesweeper.viewmodels.settingsscreenviewmodel.SettingsScreenViewModel
import com.surovtsev.cool3dminesweeper.presentation.gamescreen.GameScreen
import com.surovtsev.cool3dminesweeper.presentation.gamescreen.LoadGameParameterName
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.MainScreen
import com.surovtsev.cool3dminesweeper.presentation.rankingscreen.RankingScreen
import com.surovtsev.cool3dminesweeper.presentation.settingsscreen.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Screen.MainScreen.route
            ) {
                composable(
                    route = Screen.MainScreen.route
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
                    )
                ) { entry ->
                    val viewModel: GameScreenViewModel = hiltViewModel()
                    entry.lifecycle.addObserver(viewModel)
                    GameScreen(
                        viewModel,
                        this@MainActivity
                    )
                }
                composable(
                    route = Screen.RankingScreen.route
                ) { entry ->
                    val viewModel: RankingScreenViewModel = hiltViewModel()
                    entry.lifecycle.addObserver(viewModel)
                    RankingScreen(
                        viewModel
                    )
                }
                composable(
                    route = Screen.SettingsScreen.route
                ) { entry ->
                    val viewModel: SettingsScreenViewModel = hiltViewModel()
                    entry.lifecycle.addObserver(viewModel)
                    SettingsScreen(
                        viewModel,
                        navController
                    )
                }
            }
        }
    }
}