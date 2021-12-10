package com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers

import com.surovtsev.utils.dataconstructions.MyLiveData

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
        object SolvingTimeColumn: SortableColumn("seconds (place)")
    }
}

enum class SortDirection {
    Descending,
    Ascending
}

val SortDirectionValuesCount = SortDirection.values().count()

fun nextSortType(sortDirection: SortDirection): SortDirection {
    return SortDirection.values()[
            (sortDirection.ordinal + 1).mod(SortDirectionValuesCount)
    ]
}
