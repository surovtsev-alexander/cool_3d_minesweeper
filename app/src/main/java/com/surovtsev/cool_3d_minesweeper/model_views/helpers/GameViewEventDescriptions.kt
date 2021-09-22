package com.surovtsev.cool_3d_minesweeper.model_views.helpers

import com.surovtsev.cool_3d_minesweeper.dagger.app.game.GameScope
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.GameControlsNames
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.MarkOnShortTapControl
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import javax.inject.Inject
import javax.inject.Named

object GameViewEventsNames {
    const val ElapsedTime = "elapsedTime"
    const val BombsLeft = "bombsLeft"
    const val ShowDialog = "showDialog"
    const val GameStatus = "gameStatus"
}

@GameScope
class MarkingEvent @Inject constructor(
    @Named(GameControlsNames.MarkOnShortTap)
    private val markOnShortTapControl: MarkOnShortTapControl
): MyLiveData<Boolean>(false) {

    override fun onDataChanged(newValue: Boolean) {
        super.onDataChanged(newValue)
        if (newValue) {
            markOnShortTapControl.turnOn()
        } else {
            markOnShortTapControl.turnOff()
        }
    }
}

typealias ElapsedTimeEvent = MyLiveData<Long>
typealias BombsLeftEvent = MyLiveData<Int>
typealias ShowDialogEvent = MyLiveData<Boolean>
typealias GameStatusEvent = MyLiveData<GameStatus>