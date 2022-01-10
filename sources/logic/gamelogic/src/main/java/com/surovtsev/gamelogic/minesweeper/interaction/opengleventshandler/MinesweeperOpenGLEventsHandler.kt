package com.surovtsev.gamelogic.minesweeper.interaction.opengleventshandler

import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.interaction.eventhandler.EventHandler
import com.surovtsev.gamelogic.minesweeper.interaction.eventhandler.EventToMinesweeper
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
    private val eventHandler: EventHandler,
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
        eventHandler.handleEventWithBlocking(
            EventToMinesweeper.Tick
        )
        gameViewsHolder.onDrawFrame()
    }
}