package com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers

import com.surovtsev.cool3dminesweeper.models.room.dao.RankingDao
import com.surovtsev.cool3dminesweeper.models.room.dao.RankingList
import com.surovtsev.cool3dminesweeper.models.room.dao.RankingListWithPlaces


class RankingListHelper(
    private val rankingDao: RankingDao
) {
    fun createRankingListWithPlaces(
        settingsId: Long
    ): RankingListWithPlaces {
        val filteredData = rankingDao.getRankingListForSettingsId(
            settingsId
        )

        return RankingListWithPlacesHelper.create(filteredData)
    }

    fun sortData(
        rankingListWithPlaces: RankingListWithPlaces,
        rankingTableSortType: RankingTableSortType,
    ): RankingListWithPlaces {
        val sortingSelector = { x: RankingDataWithPlaces ->
            when (rankingTableSortType.rankingColumn) {
                RankingColumn.SortableColumn.DateColumn -> x.rankingData.dateTime
                RankingColumn.SortableColumn.SolvingTimeColumn -> x.rankingData.elapsed
            }
        }
        val comparator: Comparator<RankingDataWithPlaces> =
            if (rankingTableSortType.sortDirection == SortDirection.Ascending) {
                Comparator { a, b -> compareValuesBy(a, b, sortingSelector) }
            } else {
                Comparator { a, b -> compareValuesBy(b, a, sortingSelector)}
            }

        return rankingListWithPlaces.sortedWith(
            comparator
        )
    }
}