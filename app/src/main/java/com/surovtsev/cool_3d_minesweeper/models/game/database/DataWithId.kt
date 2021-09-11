package com.surovtsev.cool_3d_minesweeper.models.game.database

data class DataWithId<T>(
    val id: Int,
    val data: T
)
