package com.surovtsev.cool_3d_minesweeper.views.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.daggerComponentsHolder
import com.surovtsev.cool_3d_minesweeper.presentation.Screen
import com.surovtsev.cool_3d_minesweeper.presentation.game_screen.GameScreen
import com.surovtsev.cool_3d_minesweeper.presentation.game_screen.LoadGameParameterName
import com.surovtsev.cool_3d_minesweeper.presentation.main_screen.MainScreen
import com.surovtsev.cool_3d_minesweeper.presentation.ranking_screen.RankingScreen
import com.surovtsev.cool_3d_minesweeper.presentation.settings_screen.SettingsScreen

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val daggerComponentsHolder = daggerComponentsHolder

            NavHost(
                navController = navController,
                startDestination = Screen.MainScreen.route
            ) {
                composable(
                    route = Screen.MainScreen.route
                ) { entry ->
                    val appComponent = daggerComponentsHolder.appComponent
                    MainScreen(
                        appComponent,
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
                    val loadGame = entry.arguments?.getString(LoadGameParameterName).toBoolean()
                    if (daggerComponentsHolder.createGameComponentIfNeeded(loadGame, entry)) {
                        val gameComponent = daggerComponentsHolder.gameComponentHolder.component!!
                        val viewModel = gameComponent.gameActivityViewModel
                        entry.lifecycle.addObserver(viewModel)
                    }
                    val gameComponent = daggerComponentsHolder.gameComponentHolder.component!!
                    GameScreen(
                        gameComponent,
                        this@MainActivity
                    )
                }
                composable(
                    route = Screen.RankingScreen.route
                ) { entry ->
                    daggerComponentsHolder.createRankingComponentIfNeeded(entry)
                    val rankingComponent = daggerComponentsHolder.rankingComponentHolder.component!!
                    RankingScreen(
                        rankingComponent
                    )
                }
                composable(
                    route = Screen.SettingsScreen.route
                ) { entry ->
                    daggerComponentsHolder.createSettingsComponentIfNeeded(entry)
                    val settingsComponent = daggerComponentsHolder.settingComponentHolder.component!!
                    SettingsScreen(
                        settingsComponent,
                        navController
                    )
                }
            }
        }
    }
}