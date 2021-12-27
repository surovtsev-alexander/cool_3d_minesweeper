package com.surovtsev.gamescreen.minesweeper.scene

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.gamelogic.GameLogic
import com.surovtsev.utils.math.camerainfo.CameraInfoHelper
import com.surovtsev.gamescreen.minesweeper.helpers.IntersectionCalculator
import com.surovtsev.gamescreen.minesweeper.interaction.touch.TouchHandlerImp
import com.surovtsev.gamescreen.models.game.interaction.GameControlsImp
import com.surovtsev.gamescreen.utils.gles.model.pointer.Pointer
import com.surovtsev.gamescreen.utils.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.gamescreen.utils.utils.gles.view.pointer.PointerOpenGLModel.Companion.PointerEnabledName
import com.surovtsev.gamescreen.views.opengl.CubeOpenGLModel
import com.surovtsev.utils.timers.async.ManuallyUpdatableTimeAfterDeviceStartupFlowHolder
import javax.inject.Inject
import javax.inject.Named

@GameScope
class SceneCalculator @Inject constructor(
    private val gameLogic: GameLogic,
    private val timeAfterDeviceStartupFlowHolder: ManuallyUpdatableTimeAfterDeviceStartupFlowHolder,
    private val gameControls: GameControlsImp,
    private val cameraInfoHelper: CameraInfoHelper,
    private val pointer: Pointer,
    private val touchHandler: TouchHandlerImp,
    private val intersectionCalculator: IntersectionCalculator,
    private val pointerOpenGLModel: PointerOpenGLModel,
    private val cubeOpenGLModel: CubeOpenGLModel,
    @Named(PointerEnabledName)
    private val pointerEnabled: Boolean,
) {
    fun nextIteration() {
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


        gameLogic.openCubes()

        if (gameControls.removeFlaggedCells) {
            gameLogic.storeSelectedBombs()
        }
        if (gameControls.removeOpenedSlices) {
            gameLogic.collectOpenedNotEmptyBorders()
        }
        gameControls.flush()

        gameLogic.removeCubes()

        if (clicked) {
            val cell = intersectionCalculator.getCell()
            if (cell != null) {
                gameLogic.touchCell(pointer.touchType, cell, timeAfterDeviceStartupFlowHolder.timeAfterDeviceStartupFlow.value)
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