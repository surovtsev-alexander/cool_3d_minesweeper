package com.surovtsev.core.helpers.sorting

data class RankingTableSortParameters(
    val rankingTableColumn: RankingTableColumn.SortableTableColumn,
    val sortDirection: SortDirection
)
