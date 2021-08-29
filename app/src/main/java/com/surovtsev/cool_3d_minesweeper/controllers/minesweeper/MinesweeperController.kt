package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.utils.time.CustomRealtime
import com.surovtsev.cool_3d_minesweeper.utils.time.Ticker
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.GameRenderer
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.Scene
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import glm_.vec3.Vec3
import glm_.vec3.Vec3s

interface IHandleOpenGLEvents {
    fun onSurfaceCreated()
    fun onSurfaceChanged(width: Int, height: Int)
    fun onDrawFrame()
}

class MinesweeperController(
    private val context: Context,
    private val gameStatusesReceiver: IGameStatusesReceiver,
    private val timeUpdated: () -> Unit

): IHandleOpenGLEvents {
    var gameObjectsHolder: GameObjectsHolder? = null
    var scene: Scene? = null

    private val realtime = CustomRealtime()
    val touchReceiver = TouchReceiver(realtime)

    val gameTimeTicker = Ticker(1000L, realtime)

    val gameRenderer = GameRenderer(this)

    val gameConfig: GameConfig

    init {
        val d: Short = if (true) {
            12
        } else {
            7
        }

        val xDim = d
        val yDim = d
        val zDim = d

        val counts = Vec3s(xDim, yDim, zDim)

        val dimensions = Vec3(5f, 5f, 5f)
        val gaps = if (false) dimensions / counts / 40 else if (true) Vec3() else dimensions / counts / 10
        val bombsRate =  if (true)  {
            0.2f
        } else {
            0.1f
        }
        gameConfig =
            GameConfig(
                counts,
                dimensions,
                gaps,
                bombsRate
            )
    }

    override fun onSurfaceCreated() {
        gameObjectsHolder = GameObjectsHolder(context, gameStatusesReceiver, gameConfig)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        scene =
            Scene(
                gameObjectsHolder!!,
                realtime,
                width,
                height
            )

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
}