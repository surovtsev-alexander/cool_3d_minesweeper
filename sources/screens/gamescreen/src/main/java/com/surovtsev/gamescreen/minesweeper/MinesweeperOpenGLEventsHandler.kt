package com.surovtsev.gamescreen.minesweeper

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.commandhandler.CommandHandler
import com.surovtsev.gamescreen.minesweeper.commandhandler.CommandToMinesweeper
import com.surovtsev.gamescreen.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamescreen.minesweeper.helpers.MinesweeperGameStatusReceiver
import com.surovtsev.gamescreen.minesweeper.scene.SceneDrawer
import com.surovtsev.gamescreen.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.gamescreen.models.gles.gameviewsholder.GameViewsHolder
import com.surovtsev.gamescreen.utils.utils.gles.interfaces.OpenGLEventsHandler
import com.surovtsev.utils.timers.async.ManuallyUpdatableTimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.fpscalculator.FPSCalculator
import glm_.vec2.Vec2i
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@GameScope
class MinesweeperOpenGLEventsHandler @Inject constructor(
    private val manuallyUpdatableTimeSpanHelper: ManuallyUpdatableTimeAfterDeviceStartupFlowHolder,
    private val cubeInfo: CubeInfo,
    val gameLogic: GameLogic,
    private val gameViewsHolder: GameViewsHolder,
    private val sceneDrawer: SceneDrawer,
    /* Do not delete this. It is used:
        - to add new record into Ranking table when game is won;
        - to notify view about game status change.
    */
    private val minesweeperGameStatusReceiver: MinesweeperGameStatusReceiver,
    private val fpsCalculator: FPSCalculator,
    private val commandHandler: CommandHandler,
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

        sceneDrawer.onSurfaceChanged(displaySize)

        gameViewsHolder.cubeOpenGLModel.updateTexture(cubeInfo.cubeSkin)

        manuallyUpdatableTimeSpanHelper.tick()
    }

    override fun onDrawFrame() {
        manuallyUpdatableTimeSpanHelper.tick()
        fpsCalculator.onNextFrame()

        commandHandler.handleCommandWithBlocking(
            CommandToMinesweeper.Tick
        )
        sceneDrawer.onDrawFrame()
    }
}