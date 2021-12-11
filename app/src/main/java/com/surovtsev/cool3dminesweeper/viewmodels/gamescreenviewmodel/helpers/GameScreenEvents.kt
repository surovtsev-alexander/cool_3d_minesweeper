package com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.helpers
import com.surovtsev.game.dagger.GameScope
import javax.inject.Inject
import javax.inject.Named

@GameScope
class GameScreenEvents @Inject constructor(
    var markingEvent: MarkingEvent,
    @Named(GameScreenEventsNames.ElapsedTime)
    var elapsedTimeEvent: ElapsedTimeEvent,
    @Named(GameScreenEventsNames.BombsLeft)
    var bombsLeftEvent: BombsLeftEvent,
    @Named(GameScreenEventsNames.ShowDialog)
    var showDialogEvent: ShowDialogEvent,
    @Named(GameScreenEventsNames.LastWinPlace)
    val lastWinPlaceEvent: LastWinPlaceEvent,
)
