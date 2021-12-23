package com.surovtsev.gamescreen.viewmodel.helpers

import com.surovtsev.core.dataconstructions.MyLiveData
import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.models.game.gamestatus.GameStatus
import com.surovtsev.gamescreen.models.game.interaction.GameControlsNames
import com.surovtsev.gamescreen.models.game.interaction.MarkOnShortTapControl
import javax.inject.Inject
import javax.inject.Named


object GameScreenEventsNames {
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

typealias ShowDialogEvent = MyLiveData<Boolean>
typealias GameStatusEvent = MyLiveData<GameStatus>
typealias LastWinPlaceEvent = MyLiveData<Place>
