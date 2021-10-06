package com.surovtsev.cool3dminesweeper.models.game.database

data class DataWithId<T>(
    val id: Int,
    val data: T
)
