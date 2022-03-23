package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine

import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.viewmodel.templatescreenviewmodel.finitestatemachine.eventtoviewmodel.EventToViewModel

sealed class EventToRankingScreenViewModel: EventToViewModel.UserEvent() {
    companion object {
        @Suppress("FunctionName")
        fun LoadData() = EventToViewModel.Init
    }

    class FilterList(
        val selectedSettingsId: Long
    ): EventToRankingScreenViewModel()

    open class SortList(
        val rankingTableSortParameters: RankingTableSortParameters
    ): EventToRankingScreenViewModel()

    class SortListWithNoDelay(
        rankingTableSortParameters: RankingTableSortParameters
    ): SortList(
        rankingTableSortParameters
    )
}
