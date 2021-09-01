package com.surovtsev.cool_3d_minesweeper.controllers.minesweeper

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.views.gles_renderer.GLESRenderer
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.interaction.touch.TouchReceiver
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.GameConfigFactory
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.Scene
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.GameControls
import com.surovtsev.cool_3d_minesweeper.models.game.save.Save
import com.surovtsev.cool_3d_minesweeper.models.gles.game_views_holder.GameViewsHolder
import com.surovtsev.cool_3d_minesweeper.utils.interfaces.IHandlePauseResumeDestroy
import com.surovtsev.cool_3d_minesweeper.utils.time.TimeSpanHelper
import glm_.vec2.Vec2i
import java.lang.Exception
import java.util.concurrent.locks.ReentrantLock

interface IHandleOpenGLEvents {
    fun onSurfaceCreated()
    fun onSurfaceChanged(width: Int, height: Int)
    fun onDrawFrame()
}

class MinesweeperController(
    private val context: Context,
    gameEventsReceiver: IGameEventsReceiver,
    loadGame: Boolean
):
    IHandleOpenGLEvents,
    IHandlePauseResumeDestroy
{
    val gameRenderer = GLESRenderer(this)

    private val timeSpanHelper = TimeSpanHelper()
    val touchReceiver = TouchReceiver(timeSpanHelper)

    private val gameConfig: GameConfig

    private val gameObjectsHolder: GameObjectsHolder

    private val cameraInfo: CameraInfo

    var gameLogic: GameLogic
        private set
    var scene: Scene? = null
        private set

    val gameControls = GameControls()

    private var gameViewsHolder: GameViewsHolder? = null

    private var save: Save? = null

    init {
        if (loadGame) {
            val gson = Gson()
            val saveController = SaveController(context)
            try {
                val data = saveController.loadData()
                save = gson.fromJson<Save>(
                    data,
                    Save::class.java
                )
            } catch (ex: Exception) {
                Log.d("Minesweeper", "error while loading save\n${ex.message}\n${ex.printStackTrace()}")
                saveController.emptyData()
            }
        }

        if (save != null) {
            gameConfig = save!!.gameConfig
        } else {
            gameConfig = GameConfigFactory.createGameConfig()
        }

        gameObjectsHolder = GameObjectsHolder(gameConfig)

        gameLogic =
            GameLogic(
                gameObjectsHolder.cubeSkin,
                null,
                gameConfig,
                gameEventsReceiver,
                timeSpanHelper
            )

        if (save != null) {
            cameraInfo = save!!.cameraInfoToSave.getCameraInfo()

            save!!.gameLogicToSave.applySavedData(gameLogic)

            save!!.cubeSkinToSave.applySavedData(
                gameObjectsHolder.cubeSkin,
                gameLogic
            )
        } else {
            cameraInfo = CameraInfo()
        }

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

        gameLogic.gameEventsReceiver.bombCountUpdated()
        gameLogic.gameEventsReceiver.timeUpdated()
    }

    @Synchronized fun SyncExecution(x: () -> Unit) {
        x()
    }


    override fun onDrawFrame() {
        timeSpanHelper.tick()
        touchReceiver.tick()
        gameLogic.gameLogicStateHelper.tick()

        if (touchReceiver.isUpdated()) {
            scene?.touchHandler?.handleTouch(touchReceiver.touchPos, touchReceiver.touchType)
            touchReceiver.release()
        }

        SyncExecution {
            scene?.onDrawFrame()
        }
    }

    override fun onPause() {
        Log.d("TEST+++", "MinesweeperController onPause")
        SyncExecution {
            gameLogic.gameLogicStateHelper.onPause()

            val gson = Gson()
            val save = Save.createObject(
                gameConfig,
                cameraInfo,
                gameLogic,
                gameObjectsHolder.cubeSkin
            )
            val saveJson = gson.toJson(save)

            SaveController(context).save(saveJson)
        }
    }

    override fun onResume() {
        Log.d("TEST+++", "MinesweeperController onResume")
    }

    override fun onDestroy() {
        Log.d("TEST+++", "MinesweeperController onResume")
    }
}