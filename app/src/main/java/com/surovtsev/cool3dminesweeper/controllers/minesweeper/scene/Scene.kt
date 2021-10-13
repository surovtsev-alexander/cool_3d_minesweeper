package com.surovtsev.cool3dminesweeper.controllers.minesweeper.scene

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.GameLogic
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.CameraInfoHelper
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.IntersectionCalculator
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.interaction.touch.TouchHandler
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.interaction.GameControls
import com.surovtsev.cool3dminesweeper.utils.gles.model.pointer.Pointer
import com.surovtsev.cool3dminesweeper.utils.gles.view.pointer.GLPointerModel
import com.surovtsev.cool3dminesweeper.utils.time.TimeSpanHelper
import com.surovtsev.cool3dminesweeper.views.opengl.CubeView
import glm_.vec2.Vec2i
import javax.inject.Inject

@GameScope
class Scene @Inject constructor(
    private val gameLogic: GameLogic,
    private val timeSpanHelper: TimeSpanHelper,
    private val gameControls: GameControls,
    private val cameraInfoHelper: CameraInfoHelper,
    private val pointer: Pointer,
    val touchHandler: TouchHandler,
    private val intersectionCalculator: IntersectionCalculator,
    private val glPointerModel: GLPointerModel,
    private val cubeView: CubeView
) {

    fun onSurfaceChanged(newDisplaySize: Vec2i) {
        cameraInfoHelper.onSurfaceChanged(newDisplaySize)
    }

    fun onDrawFrame() {
        val cameraMoved = cameraInfoHelper.getAndRelease()
        val clicked = touchHandler.getAndRelease()

        if (gameControls.markOnShortTapControl.getAndRelease()) {
            gameLogic.markingOnShotTap = gameControls.markOnShortTapControl.isOn()
        }

        if (cameraMoved) {
            cameraInfoHelper.cameraInfo.recalculateMVPMatrix()
        }

        do {
//            if (clicked) {
//                gameObjectsHolder.glPointerView.turnOn()
//            }

            if (!glPointerModel.isOn()) {
                break
            }

            if (clicked) {
                glPointerModel.updatePoints()
            }

            glPointerModel.mGLESProgram!!.useProgram()
            if (cameraMoved) {
                with(glPointerModel.mGLESProgram!!) {
                    fillMVP(cameraInfoHelper.cameraInfo.mVP)
                }
            }
            glPointerModel.bindData()
            glPointerModel.draw()
        } while (false)

        cubeView.cubeGLESProgram!!.useProgram()

        cubeView.bindData()

        gameLogic.openCubes()

        if (gameControls.removeMarkedBombsControl.getAndRelease()) {
            gameLogic.storeSelectedBombs()
        }
        if (gameControls.removeZeroBordersControl.getAndRelease()) {
            gameLogic.storeZeroBorders()
        }
        gameLogic.removeCubes()

        if (clicked) {
            val cell = intersectionCalculator.getCell()
            if (cell != null) {
                gameLogic.touchCell(pointer.touchType, cell, timeSpanHelper.timeAfterDeviceStartup)
            }

        }
        if (cameraMoved) {
            with(cubeView.cubeGLESProgram!!) {
                fillMVP(cameraInfoHelper.cameraInfo.mVP)
            }
        }
        cubeView.draw()
    }
}
