package com.surovtsev.game.minesweeper

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.surovtsev.game.minesweeper.gamelogic.GameLogic
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.game.minesweeper.scene.Scene
import com.surovtsev.game.utils.time.TimeSpanHelperImp
import com.surovtsev.game.dagger.GameScope
import com.surovtsev.game.models.game.camerainfo.CameraInfo
import com.surovtsev.game.models.game.config.GameConfig
import com.surovtsev.game.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.game.models.game.save.Save
import com.surovtsev.game.models.gles.gameviewsholder.GameViewsHolder
import glm_.vec2.Vec2i
import javax.inject.Inject
import com.surovtsev.game.utils.utils.gles.interfaces.OpenGLEventsHandler

@GameScope
class MinesweeperController @Inject constructor(
    private val timeSpanHelper: TimeSpanHelperImp,
    private val saveController: SaveController,
    private val gameConfig: GameConfig,
    private val cameraInfo: CameraInfo,
    private val cubeInfo: CubeInfo,
    val gameLogic: GameLogic,
    private val gameViewsHolder: GameViewsHolder,
    private val scene: Scene,
):
    OpenGLEventsHandler,
    DefaultLifecycleObserver
{
    override fun onSurfaceCreated() {
        gameViewsHolder.onSurfaceCreated()
    }

    /*
     * Init GameLogic
     */
    override fun onSurfaceChanged(width: Int, height: Int) {
        val displaySize = Vec2i(width, height)

        scene.onSurfaceChanged(displaySize)

        gameViewsHolder.cubeOpenGLModel.updateTexture(cubeInfo.cubeSkin)

        timeSpanHelper.tick()
        gameLogic.gameLogicStateHelper.resumeIfNeeded()

        gameLogic.notifyBombsCountUpdated()
        gameLogic.gameLogicStateHelper.notifyTimeUpdated()
    }

    override fun onDrawFrame() {
        timeSpanHelper.tick()

        syncExecution {
            scene.onDrawFrame()
        }
    }

    @Synchronized fun syncExecution(x: () -> Unit) {
        x()
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)

        storeGameIfNeeded()
    }

    private fun storeGameIfNeeded() {
        if (!gameLogic.gameLogicStateHelper.isGameInProgress()) {
            return
        }

        syncExecution {
            gameLogic.gameLogicStateHelper.pauseIfNeeded()

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
}