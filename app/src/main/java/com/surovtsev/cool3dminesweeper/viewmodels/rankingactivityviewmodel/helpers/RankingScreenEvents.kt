package com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers

import com.surovtsev.cool3dminesweeper.dagger.app.RankingScope
import com.surovtsev.cool3dminesweeper.dagger.app.SettingsListWithIds
import com.surovtsev.cool3dminesweeper.models.game.database.RankingData
import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData
import javax.inject.Inject
import javax.inject.Named

typealias RankingList = MyLiveData<List<RankingData>>
typealias RankingListWithPlacesData = MyLiveData<RankingListWithPlaces>
typealias SelectedSettingsId = MyLiveData<Int>
typealias WinsCount = MyLiveData<Map<Int, Int>>


@RankingScope
class RankingScreenEvents @Inject constructor(
    val settingsListWithIds: SettingsListWithIds,
    @Named(RankingListName)
    val rankingList: RankingList,
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
