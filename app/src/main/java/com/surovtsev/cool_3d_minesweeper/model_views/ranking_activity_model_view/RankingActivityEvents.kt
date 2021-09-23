package com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_model_view

import com.surovtsev.cool_3d_minesweeper.dagger.app.ranking.RankingScope
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.RankingData
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import javax.inject.Inject
import javax.inject.Named

typealias SettingsListWithIds = MyLiveData<List<DataWithId<SettingsData>>>
typealias RankingList = MyLiveData<List<RankingData>>
typealias SelectedSettingsId = MyLiveData<Int>
typealias WinsCount = MyLiveData<Map<Int, Int>>


@RankingScope
class RankingActivityEvents @Inject constructor(
    val settingsListWithIds: SettingsListWithIds,
    @Named(RankingListName)
    val rankingList: RankingList,
    @Named(FilteredRankingListName)
    val filteredRankingList: RankingList,
    val selectedSettingsId: SelectedSettingsId,
    val winsCount: WinsCount
) {
    companion object {
        const val RankingListName = "rankingList"
        const val FilteredRankingListName = "filteredRankingList"
    }
}
