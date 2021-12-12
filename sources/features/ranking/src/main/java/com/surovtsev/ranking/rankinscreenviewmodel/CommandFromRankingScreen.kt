package com.surovtsev.ranking.rankinscreenviewmodel

import com.surovtsev.core.ranking.RankingTableSortType

sealed class CommandFromRankingScreen {
    object LoadData: CommandFromRankingScreen()

    object CloseError: CommandFromRankingScreen()

    class FilterList(
        val selectedSettingsId: Long
    ): CommandFromRankingScreen()

    class SortList(
        val rankingTableSortType: RankingTableSortType
    ): CommandFromRankingScreen()
}
