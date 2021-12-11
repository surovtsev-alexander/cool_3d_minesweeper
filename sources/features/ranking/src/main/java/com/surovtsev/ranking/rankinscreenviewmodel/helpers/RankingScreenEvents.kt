package com.surovtsev.ranking.rankinscreenviewmodel.helpers

import com.surovtsev.core.room.dao.RankingList
import com.surovtsev.core.room.dao.WinsCountMap
import com.surovtsev.ranking.dagger.RankingScope
import com.surovtsev.core.dataconstructions.MyLiveData
import com.surovtsev.core.ranking.RankingListWithPlaces
import com.surovtsev.core.settings.SettingsListData
import javax.inject.Inject
import javax.inject.Named

typealias RankingListData = MyLiveData<RankingList>
typealias RankingListWithPlacesData = MyLiveData<RankingListWithPlaces>
typealias SelectedSettingsIdData = MyLiveData<Long>
typealias WinsCountMapData = MyLiveData<WinsCountMap>


@RankingScope
class RankingScreenEvents @Inject constructor(
    val settingsListData: SettingsListData,
    @Named(FilteredRankingListName)
    val filteredRankingList: RankingListWithPlacesData,
    @Named(RankingListToDisplay)
    val rankingListToDisplay: RankingListWithPlacesData,
    @Named(SelectedSettingsIdName)
    val selectedSettingsIdData: SelectedSettingsIdData,
    val winsCountMapData: WinsCountMapData,
) {
    companion object {
        const val FilteredRankingListName = "filteredRankingList"
        const val RankingListToDisplay = "rankingListToDisplay"
        const val SelectedSettingsIdName = "selectedSettingsId"
    }
}
