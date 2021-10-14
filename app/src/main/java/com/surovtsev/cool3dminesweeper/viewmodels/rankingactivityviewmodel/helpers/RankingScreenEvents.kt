package com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers

import com.surovtsev.cool3dminesweeper.dagger.app.RankingScope
import com.surovtsev.cool3dminesweeper.dagger.app.SettingsDataWithIdsListData
import com.surovtsev.cool3dminesweeper.models.game.database.RankingData
import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData
import javax.inject.Inject
import javax.inject.Named

typealias RankingDataList = List<RankingData>
typealias RankingDataListData = MyLiveData<RankingDataList>
typealias RankingListWithPlacesData = MyLiveData<RankingListWithPlaces>
typealias SelectedSettingsId = MyLiveData<Int>
typealias WinsCount = MyLiveData<Map<Int, Int>>


@RankingScope
class RankingScreenEvents @Inject constructor(
    val settingsDataWithIdsListData: SettingsDataWithIdsListData,
    @Named(RankingListName)
    val rankingDataListData: RankingDataListData,
    @Named(FilteredRankingListName)
    val filteredRankingList: RankingListWithPlacesData,
    @Named(RankingListToDisplay)
    val rankingListToDisplay: RankingListWithPlacesData,
    val selectedSettingsId: SelectedSettingsId,
    val winsCount: WinsCount
) {
    companion object {
        const val RankingListName = "rankingList"
        const val FilteredRankingListName = "filteredRankingList"
        const val RankingListToDisplay = "rankingListToDisplay"
    }
}
