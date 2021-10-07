package com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.helpers
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.gamestatus.GameStatus
import com.surovtsev.cool3dminesweeper.models.game.interaction.GameControlsNames
import com.surovtsev.cool3dminesweeper.models.game.interaction.MarkOnShortTapControl
import com.surovtsev.cool3dminesweeper.utils.dataconstructions.MyLiveData
import javax.inject.Inject
import javax.inject.Named

object GameScreenEventsNames {
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