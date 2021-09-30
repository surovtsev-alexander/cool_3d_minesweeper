package com.surovtsev.cool_3d_minesweeper.presentation

sealed class Screen(
    val route: String
) {
    companion object {
        const val MainScreenName = "main_screen"
        const val GameScreenName = "game_screen"
        const val RankingScreenName = "ranking_screen"
        const val SettingsScreenName = "settings_screen"
    }

    object MainScreen: Screen(MainScreenName)
    object GameScreen: Screen(GameScreenName)
    object RankingScreen: Screen(RankingScreenName)
    object SettingsScreen: Screen(SettingsScreenName)

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
