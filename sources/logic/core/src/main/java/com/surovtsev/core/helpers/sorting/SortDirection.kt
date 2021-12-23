package com.surovtsev.core.helpers.sorting

enum class SortDirection(val symbol: Char) {
    Descending('d'),
    Ascending('a');

    companion object {
        private val sortDirectionValuesCount = values().count()
    }

    fun nextSortDirection(): SortDirection {
        return SortDirection.values()[
                (ordinal + 1).mod(sortDirectionValuesCount)
        ]
    }

}
