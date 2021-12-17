package com.surovtsev.game.viewmodel.helpers

import com.surovtsev.core.dataconstructions.MyLiveData
import com.surovtsev.game.dagger.GameScope
import com.surovtsev.game.models.game.gamestatus.GameStatus
import com.surovtsev.game.models.game.interaction.GameControlsNames
import com.surovtsev.game.models.game.interaction.MarkOnShortTapControl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Named

typealias BombsLeftData = MutableStateFlow<Int>
typealias BombsLeftValue = StateFlow<Int>

object GameScreenEventsNames {
    const val ElapsedTime = "elapsedTime"
    const val ShowDialog = "showDialog"
    const val GameStatus = "gameStatus"
    const val LastWinPlace = "lastWinPlace"
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

sealed class Place {
    object NoPlace : Place()
    class WinPlace(val place: Int): Place()
}

typealias ElapsedTimeEvent = MyLiveData<Long>
typealias ShowDialogEvent = MyLiveData<Boolean>
typealias GameStatusEvent = MyLiveData<GameStatus>
typealias LastWinPlaceEvent = MyLiveData<Place>
