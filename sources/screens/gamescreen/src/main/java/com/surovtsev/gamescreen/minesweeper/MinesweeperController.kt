package com.surovtsev.gamescreen.minesweeper

import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamescreen.minesweeper.helpers.MinesweeperGameStatusReceiver
import com.surovtsev.gamescreen.minesweeper.scene.Scene
import com.surovtsev.gamescreen.models.game.camerainfo.CameraInfo
import com.surovtsev.gamescreen.models.game.config.GameConfig
import com.surovtsev.gamescreen.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.gamescreen.models.game.save.Save
import com.surovtsev.gamescreen.models.gles.gameviewsholder.GameViewsHolder
import com.surovtsev.gamescreen.utils.utils.gles.interfaces.OpenGLEventsHandler
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import com.surovtsev.utils.timers.async.ManuallyUpdatableTimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.fpscalculator.FPSCalculator
import glm_.vec2.Vec2i
import javax.inject.Inject

@GameScope
class MinesweeperController @Inject constructor(
    private val manuallyUpdatableTimeSpanHelper: ManuallyUpdatableTimeAfterDeviceStartupFlowHolder,
    private val saveController: SaveController,
    private val gameConfig: GameConfig,
    private val cameraInfo: CameraInfo,
    private val cubeInfo: CubeInfo,
    val gameLogic: GameLogic,
    private val gameViewsHolder: GameViewsHolder,
    private val scene: Scene,
    private val asyncTimeSpan: AsyncTimeSpan,
    /* Do not delete this. It is used:
        - to add new record into Ranking table when game is won;
        - to notify view about game status change.
    */
    private val minesweeperGameStatusReceiver: MinesweeperGameStatusReceiver,
    private val fpsCalculator: FPSCalculator,
):
    OpenGLEventsHandler
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

        manuallyUpdatableTimeSpanHelper.tick()
    }

    override fun onDrawFrame() {
        manuallyUpdatableTimeSpanHelper.tick()

        syncExecution {
            scene.onDrawFrame()
        }

        fpsCalculator.onNextFrame()
    }

    @Synchronized fun syncExecution(x: () -> Unit) {
        x()
    }

    fun storeGameIfNeeded() {
        if (!gameLogic.gameLogicStateHelper.isGameInProgress()) {
            return
        }

        syncExecution {
            val save = Save.createObject(
                gameConfig,
                cameraInfo,
                gameLogic,
                cubeInfo.cubeSkin,
                asyncTimeSpan
            )
            saveController.save(
                SaveTypes.SaveGameJson,
                save
            )
        }
    }
}