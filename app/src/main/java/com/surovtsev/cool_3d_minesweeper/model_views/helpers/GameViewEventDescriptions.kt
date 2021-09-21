package com.surovtsev.cool_3d_minesweeper.model_views.helpers

import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData

object GameViewEventsNames {
    const val Marking = "marking"
    const val ElapsedTime = "elapsedTime"
    const val BombsLeft = "bombsLeft"
    const val ShowDialog = "showDialog"
}

typealias MarkingEvent = MyLiveData<Boolean>
typealias ElapsedTimeEvent = MyLiveData<Long>
typealias BombsLeftEvent = MyLiveData<Int>
typealias ShowDialogEvent = MyLiveData<Boolean>
