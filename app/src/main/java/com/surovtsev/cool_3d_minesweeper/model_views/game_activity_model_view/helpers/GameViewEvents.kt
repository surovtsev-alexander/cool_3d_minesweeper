package com.surovtsev.cool_3d_minesweeper.model_views.game_activity_model_view.helpers

import com.surovtsev.cool_3d_minesweeper.dagger.app.game.GameScope
import javax.inject.Inject
import javax.inject.Named

@GameScope
class GameViewEvents @Inject constructor(
    var markingEvent: MarkingEvent,
    @Named(GameViewEventsNames.ElapsedTime)
    var elapsedTimeEvent: ElapsedTimeEvent,
    @Named(GameViewEventsNames.BombsLeft)
    var bombsLeftEvent: BombsLeftEvent,
    @Named(GameViewEventsNames.ShowDialog)
    var showDialogEvent: ShowDialogEvent
)
