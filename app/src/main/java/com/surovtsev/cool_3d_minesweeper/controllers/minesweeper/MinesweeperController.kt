package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.game_controller.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.utils.time.CustomClock
import com.surovtsev.cool_3d_minesweeper.utils.time.Ticker
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.GameRenderer
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchReceiver
import com.surovtsev.cool_3d_minesweeper.views.game_renderer.scene.Scene

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

    private val rendererClock = CustomClock()
    val clickHelper =
        TouchReceiver(
            rendererClock
        )

    val gameTimeTicker =
        Ticker(1000L, rendererClock)

    val gameRenderer = GameRenderer(this)

    override fun onSurfaceCreated() {
        gameObjectsHolder = GameObjectsHolder(context, gameStatusesReceiver)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
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

    override fun onDrawFrame() {
        rendererClock.updateTime()
        clickHelper.tick()

        if (clickHelper.getState()) {

            scene?.touchHandler?.handleClick(clickHelper.clickPos, clickHelper.touchType)
            clickHelper.release()
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