package com.surovtsev.cool_3d_minesweeper.views

sealed class Screen(val route: String) {
    object MainScreen: Screen("main_screen")
    object RankingScreen: Screen("ranking_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}
