package com.surovtsev.ranking.rankinscreenviewmodel

import com.surovtsev.core.ranking.RankingTableSortType

sealed class CommandsToRankingScreenViewModel {
    object LoadData: CommandsToRankingScreenViewModel()

    object CloseError: CommandsToRankingScreenViewModel()

    class FilterList(
        val selectedSettingsId: Long
    ): CommandsToRankingScreenViewModel()

    class SortList(
        val rankingTableSortType: RankingTableSortType
    ): CommandsToRankingScreenViewModel()
}
