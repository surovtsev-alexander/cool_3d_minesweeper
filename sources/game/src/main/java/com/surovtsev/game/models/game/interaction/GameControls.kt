package com.surovtsev.game.models.game.interaction
import com.surovtsev.game.dagger.GameScope
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
