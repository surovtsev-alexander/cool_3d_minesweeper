package com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_view_model.helpers

import com.surovtsev.cool_3d_minesweeper.dagger.app.RankingScope
import com.surovtsev.cool_3d_minesweeper.dagger.app.SettingsListWithIds
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.RankingData
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import javax.inject.Inject
import javax.inject.Named

typealias RankingList = MyLiveData<List<RankingData>>
typealias SelectedSettingsId = MyLiveData<Int>
typealias WinsCount = MyLiveData<Map<Int, Int>>


@RankingScope
class RankingScreenEvents @Inject constructor(
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
