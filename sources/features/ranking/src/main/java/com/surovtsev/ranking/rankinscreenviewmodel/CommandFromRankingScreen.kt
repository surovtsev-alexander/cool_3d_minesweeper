package com.surovtsev.ranking.rankinscreenviewmodel

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.viewmodel.CommandFromScreen

sealed interface CommandFromRankingScreen: CommandFromScreen {
    class HandleScreenLeaving(owner: LifecycleOwner):
        CommandFromRankingScreen,
        CommandFromScreen.HandleScreenLeaving(owner)

    object LoadData: CommandFromRankingScreen, CommandFromScreen.Init

    object CloseError: CommandFromRankingScreen

    class FilterList(
        val selectedSettingsId: Long
    ): CommandFromRankingScreen

    open class SortList(
        val rankingTableSortParameters: RankingTableSortParameters
    ): CommandFromRankingScreen

    class SortListWithNoDelay(
        rankingTableSortParameters: RankingTableSortParameters
    ): SortList(
        rankingTableSortParameters
    )
}
