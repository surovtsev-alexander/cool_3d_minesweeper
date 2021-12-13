package com.surovtsev.core.helpers

import com.surovtsev.core.helpers.sorting.RankingTableColumn
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.helpers.sorting.SortDirection
import com.surovtsev.core.room.dao.RankingDao

typealias RankingListWithPlaces = List<RankingDataWithPlaces>


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
        rankingTableSortParameters: RankingTableSortParameters,
    ): RankingListWithPlaces {
        val sortingSelector = { x: RankingDataWithPlaces ->
            when (rankingTableSortParameters.rankingTableColumn) {
                RankingTableColumn.SortableTableColumn.DateTableColumn -> x.rankingData.dateTime
                RankingTableColumn.SortableTableColumn.SolvingTimeTableColumn -> x.rankingData.elapsed
            }
        }
        val comparator: Comparator<RankingDataWithPlaces> =
            if (rankingTableSortParameters.sortDirection == SortDirection.Ascending) {
                Comparator { a, b -> compareValuesBy(a, b, sortingSelector) }
            } else {
                Comparator { a, b -> compareValuesBy(b, a, sortingSelector)}
            }

        return rankingListWithPlaces.sortedWith(
            comparator
        )
    }
}