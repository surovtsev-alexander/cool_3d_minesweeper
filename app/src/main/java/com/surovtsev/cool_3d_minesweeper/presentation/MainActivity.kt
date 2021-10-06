package com.surovtsev.cool_3d_minesweeper.presentation

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
import com.surovtsev.cool_3d_minesweeper.model_views.game_activity_view_model.GameActivityViewModel
import com.surovtsev.cool_3d_minesweeper.model_views.main_activity_view_model.MainActivityViewModel
import com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_view_model.RankingActivityViewModel
import com.surovtsev.cool_3d_minesweeper.model_views.settings_activity_view_model.SettingsActivityViewModel
import com.surovtsev.cool_3d_minesweeper.presentation.game_screen.GameScreen
import com.surovtsev.cool_3d_minesweeper.presentation.game_screen.LoadGameParameterName
import com.surovtsev.cool_3d_minesweeper.presentation.main_screen.MainScreen
import com.surovtsev.cool_3d_minesweeper.presentation.ranking_screen.RankingScreen
import com.surovtsev.cool_3d_minesweeper.presentation.settings_screen.SettingsScreen
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
                    val viewModel: MainActivityViewModel by viewModels()
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
                    val viewModel: GameActivityViewModel = hiltViewModel()
                    entry.lifecycle.addObserver(viewModel)
                    GameScreen(
                        viewModel,
                        this@MainActivity
                    )
                }
                composable(
                    route = Screen.RankingScreen.route
                ) { entry ->
                    val viewModel: RankingActivityViewModel = hiltViewModel()
                    entry.lifecycle.addObserver(viewModel)
                    RankingScreen(
                        viewModel
                    )
                }
                composable(
                    route = Screen.SettingsScreen.route
                ) { entry ->
                    val viewModel: SettingsActivityViewModel = hiltViewModel()
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