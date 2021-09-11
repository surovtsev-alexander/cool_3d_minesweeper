package com.surovtsev.cool_3d_minesweeper.utils.interfaces.minesweeper.database

import android.database.sqlite.SQLiteDatabase

typealias DatabaseAction<T> = (db: SQLiteDatabase) -> T

interface IDBHelper {
    fun <T> actionWithDB(f: DatabaseAction<T>): T
}
