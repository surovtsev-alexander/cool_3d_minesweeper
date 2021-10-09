package com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database

import android.database.sqlite.SQLiteDatabase

typealias DatabaseAction<T> = (db: SQLiteDatabase) -> T

interface DBHelper {
    fun <T> actionWithDB(f: DatabaseAction<T>): T
}
