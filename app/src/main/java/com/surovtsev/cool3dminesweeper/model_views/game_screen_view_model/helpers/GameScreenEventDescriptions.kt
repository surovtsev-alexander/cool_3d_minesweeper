package com.surovtsev.cool3dminesweeper.model_views.game_screen_view_model.helpers
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool3dminesweeper.models.game.interaction.GameControlsNames
import com.surovtsev.cool3dminesweeper.models.game.interaction.MarkOnShortTapControl
import com.surovtsev.cool3dminesweeper.utils.data_constructions.MyLiveData
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
