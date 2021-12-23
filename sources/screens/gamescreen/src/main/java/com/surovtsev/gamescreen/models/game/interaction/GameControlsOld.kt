package com.surovtsev.gamescreen.models.game.interaction
import com.surovtsev.gamescreen.dagger.GameScope
import javax.inject.Inject
import javax.inject.Named

@GameScope
class GameControlsOld @Inject constructor(
    @Named(GameControlsNames.RemoveMarkedBombs)
    val removeMarkedBombsControl: RemoveMarkedBombsControl,
    @Named(GameControlsNames.RemoveZeroBorders)
    val removeZeroBordersControl: RemoveZeroBordersControl,
    @Named(GameControlsNames.MarkOnShortTap)
    val markOnShortTapControl: MarkOnShortTapControl
)
