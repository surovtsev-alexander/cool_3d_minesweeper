package com.surovtsev.gamelogic.minesweeper.scene

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
        val cameraInfoHelper = cameraInfoHelperHolder.cameraInfoHelperFlow.value

        val cameraMoved = cameraInfoHelper.getAndRelease()

        val clicked = touchHandler.getAndRelease()

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


        val gameTouchHandler = gameLogic.gameTouchHandlerFlow.value

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
            val cell = intersectionCalculator.getCell()
            if (cell != null) {
                gameTouchHandler.touchCell(pointer.touchType, cell, timeAfterDeviceStartupFlowHolder.timeAfterDeviceStartupFlow.value)
            }

        }
        if (cameraMoved) {
            cubeOpenGLModel.cubeGLESProgram.useProgram()
            with(cubeOpenGLModel.cubeGLESProgram) {
                fillMVP(cameraInfoHelper.cameraInfo.mVP)
            }
        }
    }
}