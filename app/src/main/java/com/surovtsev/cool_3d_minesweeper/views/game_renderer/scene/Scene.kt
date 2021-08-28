package com.surovtsev.cool_3d_minesweeper.views.game_renderer.scene

import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.time.CustomClock
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.CameraInfo
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.ClickHandler
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.ClickHelper

class Scene(
    private val gameObjectsHolder: GameObjectsHolder,
    private val clickHelper: ClickHelper,
    private val rendererClock: CustomClock,
    width: Int,
    height: Int
) {
    val cameraInfo: CameraInfo = CameraInfo(width, height)
    val clickHandler: ClickHandler = ClickHandler(cameraInfo)
    val removeBombs =
        Updatable(false)
    val removeBorderZeros =
        Updatable(false)
    private val drawPointer = false

    fun onSurfaceChanged() {
        val glObject = gameObjectsHolder.glCubes.glObject

        val mVPMatrix = cameraInfo.MVP
        with(glObject.modelModelGLSLProgram) {
            use_program()
            fillU_MVP(mVPMatrix)
        }

        with(gameObjectsHolder.clickPointer.mGLSLProgram) {
            use_program()
            fillU_MVP(mVPMatrix)
        }
    }

    fun onDrawFrame() {
        val moveHandler = cameraInfo.moveHandler
        val moved = moveHandler.getAndRelease()
        val clicked = clickHandler.isUpdated()

        if (moved) {
            cameraInfo.recalculateMVPMatrix()
        }

        do {
            if (clicked) {
                clickHandler.release()
                gameObjectsHolder.clickPointer.needToBeDrawn = true
            }

            if (!gameObjectsHolder.clickPointer.needToBeDrawn) {
                break
            }

            if (clicked) {
                gameObjectsHolder.clickPointer.setPoints(clickHandler.pointer)
            }

            if (drawPointer) {
                gameObjectsHolder.clickPointer.mGLSLProgram.use_program()
                if (moved) {
                    with(gameObjectsHolder.clickPointer.mGLSLProgram) {
                        fillU_MVP(cameraInfo.MVP)
                    }
                }
                gameObjectsHolder.clickPointer.bindData()
                gameObjectsHolder.clickPointer.draw()
            }
        } while (false)


        val glCubes = gameObjectsHolder.glCubes
        val glObject = glCubes.glObject
        glObject.modelModelGLSLProgram.use_program()

        glObject.bindData()

        val gameTouchHandler = glCubes.gameTouchHandler
        gameTouchHandler.openCubes()

        if (removeBombs.getAndRelease()) {
            gameTouchHandler.storeSelectedBombs()
        }
        if (removeBorderZeros.getAndRelease()) {
            //gameTouchHandler.openCubes()
            gameTouchHandler.storeZeroBorders()
        }
        gameTouchHandler.removeCubes()

        if (clicked) {
            glCubes.testPointer(clickHandler.pointer, clickHelper.clickType, rendererClock.time)
        }
        if (moved) {
            with(glObject.modelModelGLSLProgram) {
                fillU_MVP(cameraInfo.MVP)
            }
        }
        glObject.draw()
    }
}
