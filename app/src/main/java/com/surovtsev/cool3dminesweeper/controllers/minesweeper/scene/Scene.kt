package com.surovtsev.cool3dminesweeper.controllers.minesweeper.scene

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.game_logic.helpers.CameraInfoHelper
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.IntersectionCalculator
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.interaction.touch.TouchHandler
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.interaction.GameControls
import com.surovtsev.cool3dminesweeper.utils.gles.model.pointer.IPointer
import com.surovtsev.cool3dminesweeper.utils.gles.view.pointer.GLPointerView
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
    private val pointer: IPointer,
    val touchHandler: TouchHandler,
    private val intersectionCalculator: IntersectionCalculator,
    private val glPointerView: GLPointerView,
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

            if (!glPointerView.isOn()) {
                break
            }

            if (clicked) {
                glPointerView.setPoints(touchHandler.pointer)
            }

            glPointerView.mGLESProgram!!.useProgram()
            if (cameraMoved) {
                with(glPointerView.mGLESProgram!!) {
                    fillMVP(cameraInfoHelper.cameraInfo.mVP)
                }
            }
            glPointerView.bindData()
            glPointerView.draw()
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
