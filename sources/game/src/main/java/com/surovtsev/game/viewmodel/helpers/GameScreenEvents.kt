package com.surovtsev.game.viewmodel.helpers
import com.surovtsev.game.dagger.GameScope
import javax.inject.Inject
import javax.inject.Named

@GameScope
class GameScreenEvents @Inject constructor(
    var markingEvent: MarkingEvent,
    @Named(GameScreenEventsNames.ShowDialog)
    var showDialogEvent: ShowDialogEvent,
    @Named(GameScreenEventsNames.LastWinPlace)
    val lastWinPlaceEvent: LastWinPlaceEvent,
)
