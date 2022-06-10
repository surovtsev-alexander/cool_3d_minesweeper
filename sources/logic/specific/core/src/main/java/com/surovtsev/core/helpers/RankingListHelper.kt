/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


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