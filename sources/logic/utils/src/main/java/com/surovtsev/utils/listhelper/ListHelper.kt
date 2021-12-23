package com.surovtsev.utils.listhelper

object ListHelper {
    const val cSVSeparator = ";"

    fun <T> List<T>.joinToCSVLine(): String = this.joinToString(separator = cSVSeparator)
}