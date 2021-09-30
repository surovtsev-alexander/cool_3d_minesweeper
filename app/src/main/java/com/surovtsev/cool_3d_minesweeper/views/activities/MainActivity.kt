package com.surovtsev.cool_3d_minesweeper.views.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.surovtsev.cool_3d_minesweeper.presentation.Screen
import com.surovtsev.cool_3d_minesweeper.presentation.game_screen.GameScreen
import com.surovtsev.cool_3d_minesweeper.presentation.main_screen.MainScreen
import com.surovtsev.cool_3d_minesweeper.presentation.ranking_screen.RankingScreen
import com.surovtsev.cool_3d_minesweeper.presentation.settings_screen.SettingsScreen

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
                    MainScreen(navController = navController)
                }
                composable(
                    route = Screen.GameScreen.route
                ) {
                    GameScreen()
                }
                composable(
                    route = Screen.RankingScreen.route
                ) {
                    RankingScreen()
                }
                composable(
                    route = Screen.SettingsScreen.route
                ) {
                    SettingsScreen()
                }
            }
//            Navigation()
        }
    }
}