package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine

import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.viewmodel.EventToViewModel
import com.surovtsev.core.viewmodel.InitEvent

sealed class EventToRankingScreenViewModel: EventToViewModel.UserEvent() {

    object LoadData: EventToRankingScreenViewModel(), InitEvent

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

    object MandatoryEvents: EventToViewModel.MandatoryEvents(
        LoadData,
    )
}
