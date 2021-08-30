package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.utils.time.CustomRealtime
import com.surovtsev.cool_3d_minesweeper.utils.time.Ticker
import com.surovtsev.cool_3d_minesweeper.views.gles_renderer.GLESRenderer
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.GameConfigFactory
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.Scene
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResumeDestroy
import glm_.vec2.Vec2i

interface IHandleOpenGLEvents {
    fun onSurfaceCreated()
    fun onSurfaceChanged(width: Int, height: Int)
    fun onDrawFrame()
}

class MinesweeperController(
    private val context: Context,
    private val gameStatusesReceiver: IGameStatusesReceiver,
    private val timeUpdated: () -> Unit

):
    IHandleOpenGLEvents,
    IHandlePauseResumeDestroy
{
    val gameRenderer = GLESRenderer(this)

    private val realtime = CustomRealtime()
    val touchReceiver = TouchReceiver(realtime)

    // TODO: move to gameLogic
    val gameTimeTicker = Ticker(1000L, realtime)

    private val gameConfig: GameConfig = GameConfigFactory.createGameConfig()

    var gameObjectsHolder: GameObjectsHolder? = null
        private set
    var gameLogic: GameLogic? = null
        private set
    var scene: Scene? = null
        private set

    override fun onSurfaceCreated() {
        gameObjectsHolder = GameObjectsHolder(context, gameConfig)

        gameLogic =
            GameLogic(
                gameObjectsHolder!!.cubeSkin,
                gameObjectsHolder!!.cubeView,
                gameStatusesReceiver,
                gameConfig
            )
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        val displaySize = Vec2i(width, height)

        val createScene = (
                scene == null ||
                scene!!.cameraInfo.displaySize != displaySize)

        if (createScene) {
            scene =
                Scene(
                    gameLogic!!,
                    gameObjectsHolder!!,
                    realtime,
                    displaySize
                )
        }

        scene!!.onSurfaceChanged()
    }

    override fun onDrawFrame() {
        realtime.updateTime()
        touchReceiver.tick()

        if (touchReceiver.isUpdated()) {
            scene?.touchHandler?.handleTouch(touchReceiver.touchPos, touchReceiver.touchType)
            touchReceiver.release()
        }

        if (gameTimeTicker.isOn()) {
            gameTimeTicker.tick()
            if (gameTimeTicker.getAndRelease()) {
                timeUpdated()
            }
        }

        scene?.onDrawFrame()
    }

    override fun onPause() {
        gameTimeTicker.turnOff()
    }

    override fun onResume() {
        if (gameLogic!= null) {
            if (gameLogic!!.isGameInProgress()) {
                realtime.updateTime()
                gameTimeTicker.turnOn()
            }
        }
    }

    override fun onDestroy() {

    }
}