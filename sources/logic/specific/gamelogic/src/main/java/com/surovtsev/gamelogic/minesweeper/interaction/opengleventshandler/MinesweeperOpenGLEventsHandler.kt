/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


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