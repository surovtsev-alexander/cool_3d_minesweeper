package com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers

import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData

typealias RankingTableSortTypeData = MyLiveData<RankingTableSortType>

data class RankingTableSortType(
    val rankingColumn: RankingColumn.SortableColumn,
    val sortDirection: SortDirection
)

sealed class RankingColumn(
    val columnName: String
) {
    object IdColumn: RankingColumn("#")

    sealed class SortableColumn(
        columnName: String
    ): RankingColumn(columnName) {
        object DateColumn: SortableColumn("date")
        object SolvingTimeColumn: SortableColumn("seconds")
    }
}

enum class SortDirection {
    Ascending,
    Descending
}

val SortDirectionValuesCount = SortDirection.values().count()

fun nextSortType(sortDirection: SortDirection): SortDirection {
    return SortDirection.values()[
            (sortDirection.ordinal + 1).mod(SortDirectionValuesCount)
    ]
}
