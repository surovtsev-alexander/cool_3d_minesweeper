package com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool3dminesweeper.dagger.app.RankingScope
import javax.inject.Inject


@RankingScope
class RankingListHelper @Inject constructor(
    private val rankingDBQueries: RankingDBQueries
) {
    fun loadData(): RankingDataList {
        return rankingDBQueries.getRankingList()
    }

    fun filterData(
        data: RankingDataList,
        settingsId: Int
    ): RankingListWithPlaces {
        val filteredData =  data.filter {
            it.settingId == settingsId
        }

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