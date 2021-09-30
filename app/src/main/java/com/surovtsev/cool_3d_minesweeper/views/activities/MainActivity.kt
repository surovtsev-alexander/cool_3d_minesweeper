package com.surovtsev.cool_3d_minesweeper.views.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.daggerComponentsHolder
import com.surovtsev.cool_3d_minesweeper.dagger.app.settings.SettingsComponent
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
                    Log.d("TEST+++", "MainActivity MainScreen entry ${System.identityHashCode(entry)}")
                    Log.d("TEST+++", "MainActivity start MainScreen")
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
                    Log.d("TEST+++", "MainActivity GameScreen entry ${System.identityHashCode(entry)}")
                    Log.d("TEST+++", "MainActivity start GameScreen")
                    val loadGame = entry.arguments?.getString(LoadGameParameterName).toBoolean()
                    val gameComponent = daggerComponentsHolder.getGameComponent(
                        loadGame,
                        entry
                    )
                    GameScreen(
                        gameComponent,
                        this@MainActivity
                    )
                }
                composable(
                    route = Screen.RankingScreen.route
                ) {
                    Log.d("TEST+++", "MainActivity start RankingScreen")
                    val rankingComponent = daggerComponentsHolder
                        .createAndGetRankingComponent()
                    RankingScreen(
                        rankingComponent
                    )
                }
                composable(
                    route = Screen.SettingsScreen.route
                ) {
                    Log.d("TEST+++", "MainActivity start SettingsScreen")
                    val settingsComponent: SettingsComponent = daggerComponentsHolder
                        .createAndGetSettingComponent()
                    SettingsScreen(
                        settingsComponent,
                        navController
                    )
                }
            }
//            Navigation()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        super.onDestroy()

    }
}