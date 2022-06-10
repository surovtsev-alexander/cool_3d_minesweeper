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


package com.surovtsev.gamelogic.minesweeper.scene

import com.surovtsev.core.models.game.cellpointers.PointedCell
import com.surovtsev.core.models.gles.pointer.Pointer
import com.surovtsev.gamelogic.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.camerainfohelperholder.CameraInfoHelperHolder
import com.surovtsev.gamelogic.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamelogic.minesweeper.helpers.IntersectionCalculator
import com.surovtsev.gamelogic.minesweeper.interaction.screeninteractionhandler.touch.TouchHandlerImp
import com.surovtsev.gamelogic.models.game.interaction.GameControlsImp
import com.surovtsev.gamelogic.utils.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.gamelogic.utils.utils.gles.view.pointer.PointerOpenGLModel.Companion.PointerEnabledName
import com.surovtsev.gamelogic.views.opengl.CubeOpenGLModel
import com.surovtsev.utils.timers.async.ManuallyUpdatableTimeAfterDeviceStartupFlowHolder
import javax.inject.Inject
import javax.inject.Named

@GameScope
class SceneCalculator @Inject constructor(
    private val gameLogic: GameLogic,
    private val timeAfterDeviceStartupFlowHolder: ManuallyUpdatableTimeAfterDeviceStartupFlowHolder,
    private val gameControls: GameControlsImp,
    private val cameraInfoHelperHolder: CameraInfoHelperHolder,
    private val pointer: Pointer,
    private val touchHandler: TouchHandlerImp,
    private val intersectionCalculator: IntersectionCalculator,
    private val pointerOpenGLModel: PointerOpenGLModel,
    private val cubeOpenGLModel: CubeOpenGLModel,
    @Named(PointerEnabledName)
    private val pointerEnabled: Boolean,
) {
    fun nextIteration() {
        val clicked = touchHandler.getAndRelease()

        cameraInfoHelperHolder.cameraInfoHelperFlow.value?.let { cameraInfoHelper ->
            val cameraMoved = cameraInfoHelper.getAndRelease()

            if (cameraMoved) {
                cameraInfoHelper.cameraInfo.recalculateMVPMatrix()
            }

            do {
                if (!pointerEnabled) {
                    break
                }

                if (clicked) {
                    pointerOpenGLModel.turnOn()
                }

                if (!pointerOpenGLModel.isOn()) {
                    break
                }

                if (clicked) {
                    pointerOpenGLModel.updatePoints()
                }

                if (cameraMoved) {
                    pointerOpenGLModel.mGLESProgram.useProgram()
                    with(pointerOpenGLModel.mGLESProgram) {
                        fillMVP(cameraInfoHelper.cameraInfo.mVP)
                    }
                }
            } while (false)


            if (cameraMoved) {
                cubeOpenGLModel.cubeGLESProgram.useProgram()
                with(cubeOpenGLModel.cubeGLESProgram) {
                    fillMVP(cameraInfoHelper.cameraInfo.mVP)
                }
            }
        }

        val gameTouchHandler = gameLogic.gameTouchHandlerFlow.value ?: return

        gameTouchHandler.openCubes()

        if (gameControls.removeFlaggedCells) {
            gameTouchHandler.storeSelectedBombs()
        }
        if (gameControls.removeOpenedSlices) {
            gameTouchHandler.collectOpenedNotEmptyBorders()
        }
        gameControls.flush()

        gameTouchHandler.removeCubes()

        if (clicked) {
            val cell: PointedCell? = intersectionCalculator.getCell()

            if (cell != null) {
                gameTouchHandler.touchCell(pointer.touchType, cell, timeAfterDeviceStartupFlowHolder.timeAfterDeviceStartupFlow.value)
            }
        }
    }
}