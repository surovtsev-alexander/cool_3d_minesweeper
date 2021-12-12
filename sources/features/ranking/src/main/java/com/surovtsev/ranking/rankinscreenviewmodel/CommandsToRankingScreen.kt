package com.surovtsev.ranking.rankinscreenviewmodel

import com.surovtsev.core.ranking.RankingTableSortType

sealed class CommandsToRankingScreen {
    object LoadData: CommandsToRankingScreen()

    object CloseError: CommandsToRankingScreen()

    class FilterList(
        val settingsId: Int
    ): CommandsToRankingScreen()

    class SortList(
        val rankingTableSortType: RankingTableSortType
    ): CommandsToRankingScreen()
}
