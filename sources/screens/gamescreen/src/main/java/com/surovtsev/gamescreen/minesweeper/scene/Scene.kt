package com.surovtsev.gamescreen.minesweeper.scene

import com.surovtsev.gamescreen.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.CameraInfoHelper
import com.surovtsev.gamescreen.minesweeper.helpers.IntersectionCalculator
import com.surovtsev.gamescreen.minesweeper.interaction.touch.TouchHandlerImp
import com.surovtsev.gamescreen.utils.gles.model.pointer.Pointer
import com.surovtsev.utils.timers.TimeSpanHelperImp
import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.models.game.interaction.GameControls
import com.surovtsev.gamescreen.utils.utils.gles.view.pointer.PointerOpenGLModel
import com.surovtsev.gamescreen.views.opengl.CubeOpenGLModel
import glm_.vec2.Vec2i
import javax.inject.Inject
import javax.inject.Named

@GameScope
class Scene @Inject constructor(
    private val gameLogic: GameLogic,
    private val timeSpanHelper: TimeSpanHelperImp,
    private val gameControls: GameControls,
    private val cameraInfoHelper: CameraInfoHelper,
    private val pointer: Pointer,
    private val touchHandler: TouchHandlerImp,
    private val intersectionCalculator: IntersectionCalculator,
    private val pointerOpenGLModel: PointerOpenGLModel,
    private val cubeOpenGLModel: CubeOpenGLModel,
    @Named(PointerEnabledName)
    private val pointerEnabled: Boolean,
) {

    companion object {
        const val PointerEnabledName = "pointerEnabled"
    }

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

            pointerOpenGLModel.mGLESProgram.useProgram()
            if (cameraMoved) {
                with(pointerOpenGLModel.mGLESProgram) {
                    fillMVP(cameraInfoHelper.cameraInfo.mVP)
                }
            }
            pointerOpenGLModel.bindData()
            pointerOpenGLModel.draw()
        } while (false)

        cubeOpenGLModel.cubeGLESProgram.useProgram()

        cubeOpenGLModel.bindData()

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
                gameLogic.touchCell(pointer.touchType, cell, timeSpanHelper.timeAfterDeviceStartupFlow.value)
            }

        }
        if (cameraMoved) {
            with(cubeOpenGLModel.cubeGLESProgram) {
                fillMVP(cameraInfoHelper.cameraInfo.mVP)
            }
        }
        cubeOpenGLModel.draw()
    }
}
