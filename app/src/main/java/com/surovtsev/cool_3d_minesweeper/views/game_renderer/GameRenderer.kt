package com.surovtsev.cool_3d_minesweeper.views.game_renderer

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.opengl.helpers.*
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.ApplicationController
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.scene.Scene
import com.surovtsev.cool_3d_minesweeper.utils.math.RotationMatrixDecomposer
import com.surovtsev.cool_3d_minesweeper.utils.time.CustomClock
import com.surovtsev.cool_3d_minesweeper.utils.time.Ticker
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRenderer(
    private val context: Context,
    private val gameStatusesReceiver: IGameStatusesReceiver,
    private val timeUpdated: () -> Unit
): GLSurfaceView.Renderer {

    var gameObjectsHolder: GameObjectsHolder? = null
    var scene: Scene? = null

    private val rendererClock = CustomClock()
    val clickHelper = ClickHelper(rendererClock)

    val gameTimeTicker =
        Ticker(1000L, rendererClock)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0.0f, 0.3f, 0.3f, 0.0f)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_CULL_FACE)

        gameObjectsHolder = GameObjectsHolder(context, gameStatusesReceiver)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        scene =
            Scene(
                gameObjectsHolder!!,
                clickHelper,
                rendererClock,
                width,
                height
            )

        scene!!.onSurfaceChanged()
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        glEnable (GL_BLEND);
        glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        rendererClock.updateTime()
        clickHelper.tick()

        if (clickHelper.isClicked()) {

            scene?.clickHandler?.handleClick(clickHelper.clickPos, clickHelper.clickType)
            clickHelper.release()

            if (false) {
                val x = RotationMatrixDecomposer.getAngles(
                    scene!!.cameraInfo.moveHandler.rotMatrix
                )
                ApplicationController.instance!!.messagesComponent?.addMessageUI("$x")
            }
        }

        if (gameTimeTicker.isOn()) {
            gameTimeTicker.tick()
            if (gameTimeTicker.getAndRelease()) {
                timeUpdated()
            }
        }

        scene?.onDrawFrame()
    }
}
