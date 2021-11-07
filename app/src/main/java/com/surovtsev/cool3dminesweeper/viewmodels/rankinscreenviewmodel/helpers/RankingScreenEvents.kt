package com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers

import com.surovtsev.cool3dminesweeper.dagger.app.RankingScope
import com.surovtsev.cool3dminesweeper.dagger.app.SettingsListData
import com.surovtsev.cool3dminesweeper.models.room.dao.RankingList
import com.surovtsev.cool3dminesweeper.models.room.dao.RankingListWithPlaces
import com.surovtsev.cool3dminesweeper.models.room.dao.WinsCountMap
import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData
import javax.inject.Inject
import javax.inject.Named

typealias RankingListData = MyLiveData<RankingList>
typealias RankingListWithPlacesData = MyLiveData<RankingListWithPlaces>
typealias SelectedSettingsIdData = MyLiveData<Long>
typealias WinsCountMapData = MyLiveData<WinsCountMap>


@RankingScope
class RankingScreenEvents @Inject constructor(
    val settingsListData: SettingsListData,
    @Named(RankingListName)
    val rankingListData: RankingListData,
    @Named(FilteredRankingListName)
    val filteredRankingList: RankingListWithPlacesData,
    @Named(RankingListToDisplay)
    val rankingListToDisplay: RankingListWithPlacesData,
    @Named(SelectedSettingsIdName)
    val selectedSettingsIdData: SelectedSettingsIdData,
    val winsCountListData: WinsCountMapData,
) {
    companion object {
        const val RankingListName = "rankingList"
        const val FilteredRankingListName = "filteredRankingList"
        const val RankingListToDisplay = "rankingListToDisplay"
        const val SelectedSettingsIdName = "selectedSettingsId"
    }
}
