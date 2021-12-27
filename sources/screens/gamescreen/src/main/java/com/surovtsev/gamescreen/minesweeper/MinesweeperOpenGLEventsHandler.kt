package com.surovtsev.gamescreen.minesweeper

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.commandhandler.CommandHandler
import com.surovtsev.gamescreen.minesweeper.commandhandler.CommandToMinesweeper
import com.surovtsev.gamescreen.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamescreen.minesweeper.helpers.MinesweeperGameStatusReceiver
import com.surovtsev.gamescreen.models.gles.gameviewsholder.GameViewsHolder
import com.surovtsev.utils.gles.renderer.OpenGLEventsHandler
import com.surovtsev.utils.timers.async.ManuallyUpdatableTimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.fpscalculator.FPSCalculator
import javax.inject.Inject

@GameScope
class MinesweeperOpenGLEventsHandler @Inject constructor(
    private val manuallyUpdatableTimeSpanHelper: ManuallyUpdatableTimeAfterDeviceStartupFlowHolder,
    private val gameViewsHolder: GameViewsHolder,
    /* Do not delete this. It is used:
        - to add new record into Ranking table when game is won;
        - to notify view about game status change.
    */
    private val minesweeperGameStatusReceiver: MinesweeperGameStatusReceiver,
    private val fpsCalculator: FPSCalculator,
    private val commandHandler: CommandHandler,
):
    OpenGLEventsHandler
{
    override fun onSurfaceCreated() {
        gameViewsHolder.onSurfaceCreated()
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        gameViewsHolder.onSurfaceChanged()

        manuallyUpdatableTimeSpanHelper.tick()
    }

    override fun onDrawFrame() {
        manuallyUpdatableTimeSpanHelper.tick()
        fpsCalculator.onNextFrame()

        gameViewsHolder.onSurfaceChanged(
            force = false
        )
        commandHandler.handleCommandWithBlocking(
            CommandToMinesweeper.Tick
        )
        gameViewsHolder.onDrawFrame()
    }
}