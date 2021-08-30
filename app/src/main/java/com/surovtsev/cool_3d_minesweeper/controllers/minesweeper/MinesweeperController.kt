package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.views.gles_renderer.GLESRenderer
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.GameConfigFactory
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.Scene
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.GameControls
import com.surovtsev.cool_3d_minesweeper.models.gles.game_views_holder.GameViewsHolder
import com.surovtsev.cool_3d_minesweeper.utils.gles.view.pointer.GLPointerView
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResumeDestroy
import com.surovtsev.cool_3d_minesweeper.utils.time.TimeSpanHelper
import com.surovtsev.cool_3d_minesweeper.views.opengl.CubeView
import glm_.vec2.Vec2i

interface IHandleOpenGLEvents {
    fun onSurfaceCreated()
    fun onSurfaceChanged(width: Int, height: Int)
    fun onDrawFrame()
}

class MinesweeperController(
    private val context: Context,
    gameEventsReceiver: IGameEventsReceiver
):
    IHandleOpenGLEvents,
    IHandlePauseResumeDestroy
{
    val gameRenderer = GLESRenderer(this)

    private val timeSpanHelper = TimeSpanHelper()
    val touchReceiver = TouchReceiver(timeSpanHelper)

    private val gameConfig: GameConfig = GameConfigFactory.createGameConfig()

    private val gameObjectsHolder: GameObjectsHolder

    private val cameraInfo = CameraInfo()

    var gameLogic: GameLogic
        private set
    var scene: Scene? = null
        private set

    val gameControls = GameControls()

    private var gameViewsHolder: GameViewsHolder? = null

    init {
        gameObjectsHolder = GameObjectsHolder(gameConfig)

        gameLogic =
            GameLogic(
                gameObjectsHolder.cubeSkin,
                null,
                gameConfig,
                gameEventsReceiver,
                timeSpanHelper
            )
    }

    override fun onSurfaceCreated() {
        gameViewsHolder = GameViewsHolder.createObject(
            context,
            gameObjectsHolder.cubeCoordinates
        )
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        val displaySize = Vec2i(width, height)

        val createScene = (
                scene == null ||
                scene!!.cameraInfoHelper.displaySize != displaySize)

        if (createScene) {
            scene =
                Scene(
                    gameLogic,
                    gameObjectsHolder,
                    cameraInfo,
                    timeSpanHelper,
                    displaySize,
                    gameControls
                )
        }

        gameLogic.textureUpdater = gameViewsHolder!!.cubeView

        scene!!.gameViewsHolder = gameViewsHolder

        scene!!.onSurfaceChanged()

        gameViewsHolder!!.cubeView.updateTexture(gameObjectsHolder.cubeSkin)

        timeSpanHelper.tick()
        gameLogic.gameLogicStateHelper.onResume()
    }

    override fun onDrawFrame() {
        timeSpanHelper.tick()
        touchReceiver.tick()
        gameLogic.gameLogicStateHelper.tick()

        if (touchReceiver.isUpdated()) {
            scene?.touchHandler?.handleTouch(touchReceiver.touchPos, touchReceiver.touchType)
            touchReceiver.release()
        }

        scene?.onDrawFrame()
    }

    override fun onPause() {
        gameLogic.gameLogicStateHelper.onPause()
    }

    override fun onResume() {

    }

    override fun onDestroy() {

    }
}