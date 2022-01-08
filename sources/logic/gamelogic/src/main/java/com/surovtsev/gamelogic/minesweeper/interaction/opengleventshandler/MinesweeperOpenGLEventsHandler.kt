package com.surovtsev.gamelogic.minesweeper.interaction.opengleventshandler

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.interaction.commandhandler.CommandHandler
import com.surovtsev.gamelogic.minesweeper.interaction.commandhandler.CommandToMinesweeper
import com.surovtsev.gamelogic.models.gles.gameviewsholder.GameViewsHolder
import com.surovtsev.utils.gles.renderer.OpenGLEventsHandler
import com.surovtsev.utils.timers.async.ManuallyUpdatableTimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.fpscalculator.FPSCalculator
import javax.inject.Inject

@GameScope
class MinesweeperOpenGLEventsHandler @Inject constructor(
    private val manuallyUpdatableTimeAfterDeviceStartupFlowHolder: ManuallyUpdatableTimeAfterDeviceStartupFlowHolder,
    private val gameViewsHolder: GameViewsHolder,
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

        manuallyUpdatableTimeAfterDeviceStartupFlowHolder.tick()
    }

    override fun onDrawFrame() {
        manuallyUpdatableTimeAfterDeviceStartupFlowHolder.tick()
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