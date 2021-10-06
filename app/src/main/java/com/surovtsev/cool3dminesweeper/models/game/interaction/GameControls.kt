package com.surovtsev.cool3dminesweeper.models.game.interaction
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
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
