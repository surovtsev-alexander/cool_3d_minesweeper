package com.surovtsev.rankingscreen.rankinscreenviewmodel.alt

import androidx.lifecycle.LifecycleOwner
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters

sealed class EventToRankingScreenViewModelAlt(
    override val pushToHead: Boolean = false,
    override val doNotPushToQueue: Boolean = false,
    override val setLoadingStateBeforeProcessing: Boolean = true,
): EventToViewModelAlt {
    class HandleScreenLeaving(
        override val owner: LifecycleOwner
    ):
        EventToRankingScreenViewModelAlt(),
        EventToViewModelAlt.HandleScreenLeaving

    object LoadData: EventToRankingScreenViewModelAlt(), EventToViewModelAlt.Init

    object CloseError: EventToRankingScreenViewModelAlt(), EventToViewModelAlt.CloseError

    object CloseErrorAndFinish: EventToRankingScreenViewModelAlt(), EventToViewModelAlt.CloseErrorAndFinish

    class FilterList(
        val selectedSettingsId: Long
    ): EventToRankingScreenViewModelAlt()

    open class SortList(
        val rankingTableSortParameters: RankingTableSortParameters
    ): EventToRankingScreenViewModelAlt()

    class SortListWithNoDelay(
        rankingTableSortParameters: RankingTableSortParameters
    ): SortList(
        rankingTableSortParameters
    )

    object MandatoryEvents: EventToViewModelAlt.MandatoryEvents<EventToRankingScreenViewModelAlt>(
        LoadData,
        CloseError,
        CloseErrorAndFinish,
        { HandleScreenLeaving(it) },
    )
}
