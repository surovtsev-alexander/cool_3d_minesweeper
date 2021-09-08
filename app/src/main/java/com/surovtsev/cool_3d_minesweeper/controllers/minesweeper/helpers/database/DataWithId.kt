package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database

data class DataWithId<T>(
    val id: Int,
    val data: T
)
