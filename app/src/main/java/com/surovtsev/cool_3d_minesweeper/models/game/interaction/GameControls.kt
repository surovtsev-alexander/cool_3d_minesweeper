package com.surovtsev.cool_3d_minesweeper.models.game.interaction

import com.surovtsev.cool_3d_minesweeper.dagger.GameScope
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.GameViewEventsNames
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.MarkingEvent
import javax.inject.Inject
import javax.inject.Named

@GameScope
class GameControls @Inject constructor(
    @Named(GameControlsNames.RemoveMarkedBombs)
    val removeMarkedBombsControl: RemoveMarkedBombsControl,
    @Named(GameControlsNames.RemoveZeroBorders)
    val removeZeroBordersControl: RemoveZeroBordersControl,
    @Named(GameControlsNames.MarkOnShortTap)
    val markOnShortTapControl: MarkOnShortTapControl
)
