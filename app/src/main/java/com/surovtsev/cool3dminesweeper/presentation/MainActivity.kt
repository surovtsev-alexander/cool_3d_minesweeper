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
import com.surovtsev.cool3dminesweeper.model_views.game_screen_view_model.GameScreenViewModel
import com.surovtsev.cool3dminesweeper.model_views.main_screen_view_model.MainScreenViewModel
import com.surovtsev.cool3dminesweeper.model_views.ranking_activity_view_model.RankingScreenViewModel
import com.surovtsev.cool3dminesweeper.model_views.settings_screen_view_model.SettingsScreenViewModel
import com.surovtsev.cool3dminesweeper.presentation.game_screen.GameScreen
import com.surovtsev.cool3dminesweeper.presentation.game_screen.LoadGameParameterName
import com.surovtsev.cool3dminesweeper.presentation.main_screen.MainScreen
import com.surovtsev.cool3dminesweeper.presentation.ranking_screen.RankingScreen
import com.surovtsev.cool3dminesweeper.presentation.settings_screen.SettingsScreen
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