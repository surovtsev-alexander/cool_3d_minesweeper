package com.surovtsev.ranking.rankinscreenviewmodel

import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.viewmodel.CommandFromScreen

sealed class CommandFromRankingScreen: CommandFromScreen {
    object HandleScreenLeaving: CommandFromRankingScreen()

    object LoadData: CommandFromRankingScreen()

    object CloseError: CommandFromRankingScreen()

    class FilterList(
        val selectedSettingsId: Long
    ): CommandFromRankingScreen()

    open class SortList(
        val rankingTableSortParameters: RankingTableSortParameters
    ): CommandFromRankingScreen()

    class SortListWithNoDelay(
        rankingTableSortParameters: RankingTableSortParameters
    ): SortList(
        rankingTableSortParameters
    )
}
