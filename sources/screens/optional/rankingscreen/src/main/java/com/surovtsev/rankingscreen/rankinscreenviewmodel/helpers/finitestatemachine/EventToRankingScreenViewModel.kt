package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.viewmodel.EventToViewModel

sealed class EventToRankingScreenViewModel(
    override val doNotPushToQueue: Boolean = false,
    override val pushToHead: Boolean = false,
    override val setLoadingStateBeforeProcessing: Boolean = true,
): EventToViewModel {
    class HandleScreenLeaving(
        override val owner: LifecycleOwner
    ):
        EventToRankingScreenViewModel(),
        EventToViewModel.HandleScreenLeaving

    object LoadData: EventToRankingScreenViewModel(), EventToViewModel.Init

    object CloseError: EventToRankingScreenViewModel(), EventToViewModel.CloseError

    object CloseErrorAndFinish: EventToRankingScreenViewModel(), EventToViewModel.CloseErrorAndFinish

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
        CloseError,
        CloseErrorAndFinish,
        { HandleScreenLeaving(it) },
    )
}
