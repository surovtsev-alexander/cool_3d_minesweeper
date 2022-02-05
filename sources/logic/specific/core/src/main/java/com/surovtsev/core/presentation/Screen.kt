package com.surovtsev.core.presentation

sealed class Screen(
    val route: String
) {
    companion object {
        const val MainScreenName        = "main_screen"
        const val GameScreenName        = "game_screen"
        const val RankingScreenName     = "ranking_screen"
        const val SettingsScreenName    = "settings_screen"
        const val HelpScreenName        = "help_screen"
    }

    object MainScreen:      Screen(MainScreenName)
    object GameScreen:      Screen(GameScreenName)
    object RankingScreen:   Screen(RankingScreenName)
    object SettingsScreen:  Screen(SettingsScreenName)
    object HelpScreen:      Screen(HelpScreenName)

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
