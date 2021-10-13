package com.surovtsev.cool3dminesweeper.controllers.minesweeper

import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.GameLogic
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveTypes
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.interaction.touch.TouchReceiver
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.scene.Scene
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.camerainfo.CameraInfo
import com.surovtsev.cool3dminesweeper.models.game.config.GameConfig
import com.surovtsev.cool3dminesweeper.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.cool3dminesweeper.models.game.save.Save
import com.surovtsev.cool3dminesweeper.models.gles.gameviewsholder.GameViewsHolder
import com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.TouchListener
import com.surovtsev.cool3dminesweeper.utils.gles.interfaces.OpenGLEventsHandler
import com.surovtsev.cool3dminesweeper.utils.interfaces.PauseResumeDestroyHandler
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
    private val cubeInfo: CubeInfo,
    val gameLogic: GameLogic,
    private val gameViewsHolder: GameViewsHolder,
    private val scene: Scene,
    val touchListener: TouchListener
):
    OpenGLEventsHandler,
    PauseResumeDestroyHandler
{
    override fun onSurfaceCreated() {
        gameViewsHolder.onSurfaceCreated()
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        val displaySize = Vec2i(width, height)

        scene.onSurfaceChanged(displaySize)

        gameViewsHolder.cubeOpenGLModel.updateTexture(cubeInfo.cubeSkin)

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
        if (!gameLogic.gameLogicStateHelper.isGameInProgress()) {
            return
        }

        syncExecution {
            gameLogic.gameLogicStateHelper.onPause()

            val save = Save.createObject(
                gameConfig,
                cameraInfo,
                gameLogic,
                cubeInfo.cubeSkin
            )
            saveController.save(
                SaveTypes.SaveGameJson,
                save
            )
        }
    }

    override fun onResume() {
    }

    override fun onDestroy() {
    }
}