package com.surovtsev.cool3dminesweeper.controllers.minesweeper

import android.util.Log
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.interaction.touch.TouchReceiver
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.scene.Scene
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool3dminesweeper.models.game.config.GameConfig
import com.surovtsev.cool3dminesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool3dminesweeper.models.game.save.Save
import com.surovtsev.cool3dminesweeper.models.gles.game_views_holder.GameViewsHolder
import com.surovtsev.cool3dminesweeper.utils.android_view.touch_listener.TouchListener
import com.surovtsev.cool3dminesweeper.utils.gles.interfaces.IHandleOpenGLEvents
import com.surovtsev.cool3dminesweeper.utils.interfaces.IHandlePauseResumeDestroy
import com.surovtsev.cool3dminesweeper.utils.time.TimeSpanHelper
import glm_.vec2.Vec2i
import javax.inject.Inject

@GameScope
class MinesweeperController @Inject constructor(
    private val timeSpanHelper: TimeSpanHelper,
    private val touchReceiver: TouchReceiver,
    private val saveController: SaveController,
    private val gameConfig: GameConfig,
    private val cameraInfo: CameraInfo,
    private val gameObjectsHolder: GameObjectsHolder,
    val gameLogic: GameLogic,
    private val gameViewsHolder: GameViewsHolder,
    private val scene: Scene,
    val touchListener: TouchListener
):
    IHandleOpenGLEvents,
    IHandlePauseResumeDestroy
{
    override fun onSurfaceCreated() {
        gameViewsHolder.onSurfaceCreated()
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        val displaySize = Vec2i(width, height)

        scene.onSurfaceChanged(displaySize)

        gameViewsHolder.cubeView.updateTexture(gameObjectsHolder.cubeSkin)

        timeSpanHelper.tick()
        gameLogic.gameLogicStateHelper.onResume()

        gameLogic.notifyBombsCountUpdated()
        gameLogic.gameLogicStateHelper.notifyTimeUpdated()
    }

    @Synchronized fun syncExecution(x: () -> Unit) {
        x()
    }

    override fun onDrawFrame() {
        timeSpanHelper.tick()
        touchReceiver.tick()
        gameLogic.gameLogicStateHelper.tick()

        if (touchReceiver.isUpdated()) {
            scene.touchHandler.handleTouch(touchReceiver.touchPos, touchReceiver.touchType)
            touchReceiver.release()
        }

        syncExecution {
            scene.onDrawFrame()
        }
    }

    override fun onPause() {
        Log.d("TEST+++", "MinesweeperController onPause")
        if (!gameLogic.gameLogicStateHelper.isGameInProgress()) {
            return
        }

        syncExecution {
            gameLogic.gameLogicStateHelper.onPause()

            val save = Save.createObject(
                gameConfig,
                cameraInfo,
                gameLogic,
                gameObjectsHolder.cubeSkin
            )
            saveController.save(
                SaveTypes.SaveGameJson,
                save
            )
        }
    }

    override fun onResume() {
        Log.d("TEST+++", "MinesweeperController onResume")
    }

    override fun onDestroy() {
        Log.d("TEST+++", "MinesweeperController onResume")
    }
}