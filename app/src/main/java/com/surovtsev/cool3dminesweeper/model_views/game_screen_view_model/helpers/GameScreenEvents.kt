package com.surovtsev.cool3dminesweeper.model_views.game_screen_view_model.helpers
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
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
    var showDialogEvent: ShowDialogEvent
)
