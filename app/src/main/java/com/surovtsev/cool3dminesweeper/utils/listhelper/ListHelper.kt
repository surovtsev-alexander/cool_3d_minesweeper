package com.surovtsev.cool3dminesweeper.utils.listhelper

object ListHelper {
    fun <T> List<T>.joinToCSVLine(): String = this.joinToString(separator = ",")
}