package com.surovtsev.ranking.rankinscreenviewmodel

import com.surovtsev.core.helpers.RankingListWithPlaces
import com.surovtsev.core.helpers.sorting.DirectionOfSortableColumns
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.room.dao.WinsCountMap
import com.surovtsev.core.settings.SettingsList

val RankingScreenInitialState = RankingScreenState.Idle(
    RankingScreenData.NoData)


sealed class RankingScreenState(
    val rankingScreenData: RankingScreenData
) {

    class Loading(
        rankingScreenData: RankingScreenData
    ): RankingScreenState(rankingScreenData)

    class Error(
        rankingScreenData: RankingScreenData,
        val message: String
    ): RankingScreenState(rankingScreenData)

    class Idle(
        rankingScreenData: RankingScreenData
    ): RankingScreenState(rankingScreenData)
}

sealed class RankingScreenData() {

    object NoData: RankingScreenData()

    open class SettingsListIsLoaded(
        val settingsList: SettingsList,
        val winsCountMap: WinsCountMap,
    ): RankingScreenData()

    open class RankingListIsPrepared(
        settingsListIsLoaded: SettingsListIsLoaded,
        val selectedSettingsId: Long,
        val rankingListWithPlaces: RankingListWithPlaces,
    ) : SettingsListIsLoaded(
        settingsListIsLoaded.settingsList,
        settingsListIsLoaded.winsCountMap
    )

    class RankingListIsSorted(
        rankingListIsPrepared: RankingListIsPrepared,
        val rankingTableSortParameters: RankingTableSortParameters,
        val sortedRankingList: RankingListWithPlaces,
        val directionOfSortableColumns: DirectionOfSortableColumns,
    ): RankingListIsPrepared(
        rankingListIsPrepared,
        rankingListIsPrepared.selectedSettingsId,
        rankingListIsPrepared.rankingListWithPlaces
    )
}

