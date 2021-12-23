package com.surovtsev.gamescreen.viewmodel.helpers
import com.surovtsev.gamescreen.dagger.GameScope
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
