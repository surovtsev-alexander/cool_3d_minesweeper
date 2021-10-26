package com.surovtsev.cool3dminesweeper.utils.listhelper

object ListHelper {
    const val cSVSeparator = ";"

    fun <T> List<T>.joinToCSVLine(): String = this.joinToString(separator = cSVSeparator)
}