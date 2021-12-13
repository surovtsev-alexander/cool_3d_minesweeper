package com.surovtsev.core.helpers.sorting

typealias DirectionOfSortableColumns = Map<RankingTableColumn.SortableTableColumn, SortDirection>

val DefaultSortDirectionForSortableColumns: DirectionOfSortableColumns = mapOf(
    RankingTableColumn.SortableTableColumn.DateTableColumn to SortDirection.Descending,
    RankingTableColumn.SortableTableColumn.SolvingTimeTableColumn to SortDirection.Ascending
)

fun defaultRankingTableSortParameters(
    sortableTableColumn: RankingTableColumn.SortableTableColumn
) = RankingTableSortParameters(
    sortableTableColumn,
    DefaultSortDirectionForSortableColumns[sortableTableColumn]!!
)

val DefaultRankingTableSortParameters = defaultRankingTableSortParameters(
    RankingTableColumn.SortableTableColumn.DateTableColumn
)



