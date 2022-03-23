package com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine

import com.surovtsev.core.helpers.RankingListWithPlaces
import com.surovtsev.core.helpers.sorting.DirectionOfSortableColumns
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.room.dao.SettingsList
import com.surovtsev.core.room.dao.WinsCountMap
import com.surovtsev.templateviewmodel.finitestatemachine.screendata.ViewModelData
import com.surovtsev.finitestatemachine.state.data.InitializationIsNotFinished

sealed interface RankingScreenData: ViewModelData.UserData {

    open class SettingsListIsLoaded(
        val settingsList: SettingsList,
        val winsCountMap: WinsCountMap,
    ): RankingScreenData, InitializationIsNotFinished

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
