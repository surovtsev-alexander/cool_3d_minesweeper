package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameStatusesReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.views.gles_renderer.GLESRenderer
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.GameConfigFactory
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.Scene
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.control.GameControls
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
    gameStatusesReceiver: IGameStatusesReceiver,
    timeUpdated: () -> Unit
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

    var gameLogic: GameLogic? = null
        private set
    var scene: Scene? = null
        private set

    val gameControls = GameControls()

    private var glPointerView: GLPointerView? = null
    private var cubeView: CubeView? = null


    init {
        gameObjectsHolder = GameObjectsHolder(context, gameConfig)

        gameLogic =
            GameLogic(
                gameObjectsHolder!!.cubeSkin,
                null,
                gameConfig,
                gameStatusesReceiver,
                timeUpdated,
                timeSpanHelper
            )
    }

    override fun onSurfaceCreated() {
        val cubeViewHelper = gameObjectsHolder!!.cubeViewHelper
        cubeView =
            CubeView(
                context,
                cubeViewHelper.triangleCoordinates,
                cubeViewHelper.isEmpty,
                cubeViewHelper.textureCoordinates
            )

        glPointerView = GLPointerView(context)
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        val displaySize = Vec2i(width, height)

        val createScene = (
                scene == null ||
                scene!!.cameraInfoHelper.displaySize != displaySize)

        if (createScene) {
            scene =
                Scene(
                    gameLogic!!,
                    gameObjectsHolder!!,
                    cameraInfo,
                    timeSpanHelper,
                    displaySize,
                    null,
                    null,
                    gameControls
                )
        }

        gameLogic!!.textureUpdater = cubeView

        scene!!.cubeView = cubeView
        scene!!.glPointerView = glPointerView


        scene!!.onSurfaceChanged()

        timeSpanHelper.tick()
        gameLogic!!.gameLogicStateHelper.onResume()
    }

    override fun onDrawFrame() {
        timeSpanHelper.tick()
        touchReceiver.tick()
        gameLogic!!.gameLogicStateHelper.tick()

        if (touchReceiver.isUpdated()) {
            scene?.touchHandler?.handleTouch(touchReceiver.touchPos, touchReceiver.touchType)
            touchReceiver.release()
        }

        scene?.onDrawFrame()
    }

    override fun onPause() {
        gameLogic?.gameLogicStateHelper?.onPause()
    }

    override fun onResume() {

    }

    override fun onDestroy() {

    }
}