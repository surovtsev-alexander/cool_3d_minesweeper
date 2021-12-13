package com.surovtsev.core.helpers.sorting

sealed class RankingTableColumn(
    val columnName: String
) {
    object IdTableColumn: RankingTableColumn("#")

    sealed class SortableTableColumn(
        columnName: String
    ): RankingTableColumn(columnName) {
        object DateTableColumn: SortableTableColumn("date")
        object SolvingTimeTableColumn: SortableTableColumn("seconds (place)")
    }
}
