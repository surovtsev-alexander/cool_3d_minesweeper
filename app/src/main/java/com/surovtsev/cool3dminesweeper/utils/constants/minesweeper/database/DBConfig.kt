package com.surovtsev.cool3dminesweeper.utils.constants.minesweeper.database

object DBConfig {
    const val dataBaseName = "minesweeperDB"
    const val dataBaseVersion = 1

    fun glueWithUnderscores(s: String) = s.replace(' ', '_')


    const val rankingTableName = "ranking"
    const val settingsTableName = "settings"
}