package com.surovtsev.core.ranking

import com.surovtsev.core.dataconstructions.MyLiveData

typealias RankingTableSortTypeData = MyLiveData<RankingTableSortType>

typealias DirectionOfSortableColumns = Map<RankingColumn.SortableColumn, SortDirection>

val DefaultSortDirectionForSortableColumns: DirectionOfSortableColumns = mapOf(
    RankingColumn.SortableColumn.DateColumn to SortDirection.Descending,
    RankingColumn.SortableColumn.SolvingTimeColumn to SortDirection.Ascending
)

fun defaultRankingTableSortType(
    sortableColumn: RankingColumn.SortableColumn
) = RankingTableSortType(
    sortableColumn,
    DefaultSortDirectionForSortableColumns[sortableColumn]!!
)

val DefaultRankingTableSortType = defaultRankingTableSortType(
    RankingColumn.SortableColumn.DateColumn
)

data class RankingTableSortType(
    val rankingColumn: RankingColumn.SortableColumn,
    val sortDirection: SortDirection
) {
    fun nextSortType(): RankingTableSortType {
        return this.copy(
            sortDirection = sortDirection.nextSortType()
        )
    }
}

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

enum class SortDirection(val symbol: Char) {
    Descending('d'),
    Ascending('a')
}

val SortDirectionValuesCount = SortDirection.values().count()

fun SortDirection.nextSortType(): SortDirection {
    return SortDirection.values()[
            (ordinal + 1).mod(SortDirectionValuesCount)
    ]
}
