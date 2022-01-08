package com.surovtsev.gamelogic.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.GameScreenEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.gamelogic.minesweeper.Minesweeper
import com.surovtsev.gamelogic.minesweeper.gameState.CameraInfoHelperHolder
import com.surovtsev.gamelogic.minesweeper.gameState.GameStateHolder
import com.surovtsev.gamelogic.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.BombsLeftFlow
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.GameStatusHolder
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.GameStatusWithElapsedFlow
import com.surovtsev.gamelogic.minesweeper.helpers.GameConfigFactory
import com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow.GameNotPausedFlow
import com.surovtsev.gamelogic.minesweeper.interaction.opengleventshandler.MinesweeperOpenGLEventsHandler
import com.surovtsev.gamelogic.minesweeper.interaction.screeninteractionhandler.move.MoveHandlerImp
import com.surovtsev.gamelogic.minesweeper.interaction.screeninteractionhandler.touch.TouchHandlerImp
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsMutableFlows
import com.surovtsev.gamelogic.models.game.config.GameConfig
import com.surovtsev.gamelogic.models.game.interaction.GameControls
import com.surovtsev.gamelogic.models.game.interaction.GameControlsImp
import com.surovtsev.gamelogic.models.game.save.Save
import com.surovtsev.gamelogic.utils.gles.model.pointer.Pointer
import com.surovtsev.gamelogic.utils.gles.model.pointer.PointerImp
import com.surovtsev.gamelogic.utils.utils.gles.view.pointer.PointerOpenGLModel.Companion.PointerEnabledName
import com.surovtsev.gamelogic.views.opengl.CubeOpenGLModel
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import com.surovtsev.utils.dagger.components.RestartableCoroutineScopeEntryPoint
import com.surovtsev.utils.dagger.components.SubscriptionsHolderEntryPoint
import com.surovtsev.utils.gles.renderer.OpenGLEventsHandler
import com.surovtsev.utils.math.FloatingAverage
import com.surovtsev.utils.math.camerainfo.CameraInfo
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import com.surovtsev.utils.timers.async.ManuallyUpdatableTimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.async.TimeAfterDeviceStartupFlowHolder
import com.surovtsev.utils.timers.fpscalculator.DelayedFPSFlowHolder
import com.surovtsev.utils.timers.fpscalculator.FPSCalculator
import dagger.*
import javax.inject.Named

@GameScope
@Component(
    dependencies = [
        AppComponentEntryPoint::class,
        GameScreenEntryPoint::class,
        RestartableCoroutineScopeEntryPoint::class,
        SubscriptionsHolderEntryPoint::class,
        TimeSpanComponentEntryPoint::class,
    ],
    modules = [
        GameControlsModule::class,
        GameControllerModule::class,
        SceneSettingsModule::class,
        GameControllerBindModule::class,
        InteractionModule::class,
    ]
)
interface GameComponent {
    val minesweeperOpenGLEventsHandler: MinesweeperOpenGLEventsHandler
    val gameConfig: GameConfig
    val settingsDao: SettingsDao
    val rankingDao: RankingDao
    val rankingListHelper: RankingListHelper
    val minesweeper: Minesweeper

    val touchHandlerImp: TouchHandlerImp
    val moveHandlerImp: MoveHandlerImp
    val manuallyUpdatableTimeAfterDeviceStartupFlowHolder: ManuallyUpdatableTimeAfterDeviceStartupFlowHolder

    val bombsLeftFlow: BombsLeftFlow

    val gameControlsImp: GameControlsImp

    val uiGameControlsMutableFlows: UIGameControlsMutableFlows
    val uiGameControlsFlows: UIGameControlsFlows

    val cameraInfoHelperHolder: CameraInfoHelperHolder

    @Component.Builder
    interface Builder {
        fun appComponentEntryPoint(appComponentEntryPoint: AppComponentEntryPoint): Builder
        fun gameScreenEntryPoint(gameScreenEntryPoint: GameScreenEntryPoint): Builder
        fun restartableCoroutineScopeEntryPoint(restartableCoroutineScopeEntryPoint: RestartableCoroutineScopeEntryPoint): Builder
        fun subscriptionsHolderEntryPoint(subscriptionsHolderEntryPoint: SubscriptionsHolderEntryPoint): Builder
        fun timeSpanComponentEntryPoint(timeSpanComponentEntryPoint: TimeSpanComponentEntryPoint): Builder
        fun loadGame(@BindsInstance loadGame: Boolean): Builder
        fun gameNotPausedFlow(@BindsInstance gameNotPausedFlow: GameNotPausedFlow): Builder
        fun build(): GameComponent
    }
}

@Module
object GameControlsModule {
    @GameScope
    @Provides
    fun provideFPSCalculator(
        timeAfterDeviceStartupFlowHolder: TimeAfterDeviceStartupFlowHolder,
    ): FPSCalculator {
        return FPSCalculator(
            timeAfterDeviceStartupFlowHolder,
            FloatingAverage(10),
        )
    }

    @GameScope
    @Provides
    fun provideDelayedFPSFlowHolder(
        timeAfterDeviceStartupFlowHolder: TimeAfterDeviceStartupFlowHolder,
        fpsCalculator: FPSCalculator,
        subscriptionsHolder: SubscriptionsHolder,
    ): DelayedFPSFlowHolder {
        return DelayedFPSFlowHolder(
            timeAfterDeviceStartupFlowHolder,
            300,
            fpsCalculator,
            subscriptionsHolder
        )
    }

    @GameScope
    @Provides
    fun provideUIGameControlsMutableFlows(
    ): UIGameControlsMutableFlows {
        return UIGameControlsMutableFlows()
    }

    @GameScope
    @Provides
    fun provideUIGameControlsFlows(
        uiGameControlsMutableFlows: UIGameControlsMutableFlows,
        bombsLeftFlow: BombsLeftFlow,
        asyncTimeSpan: AsyncTimeSpan,
        delayedFPSFlowHolder: DelayedFPSFlowHolder,
    ): UIGameControlsFlows {
        return UIGameControlsFlows(
            uiGameControlsMutableFlows.flagging,
            uiGameControlsMutableFlows.uiGameStatus,
            bombsLeftFlow,
            asyncTimeSpan.timeSpanFlow,
            delayedFPSFlowHolder.flow
        )
    }

    @GameScope
    @Provides
    fun provideGameControlsImp(
    ): GameControlsImp {
        return GameControlsImp()
    }
}

@Module
object GameControllerModule {
    @GameScope
    @Provides
    fun provideSave(
        saveController: SaveController,
        loadGame: Boolean
    ): Save? {
        val save = if (loadGame) {
            val res = saveController.tryToLoad<Save>(
                SaveTypes.SaveGameJson
            )
            saveController.emptyData(
                SaveTypes.SaveGameJson
            )
            res
        } else {
            null
        }
        return save
    }

    @GameScope
    @Provides
    fun provideGameConfig(
        save: Save?,
        saveController: SaveController
    ): GameConfig {
        return save?.gameConfig
            ?: GameConfigFactory.createGameConfig(
                saveController.loadSettingDataOrDefault()
            )
    }

    @GameScope
    @Provides
    fun provideCameraInfo(
        save: Save?
    ): CameraInfo {
        return save?.cameraInfoToSave?.getCameraInfo()
            ?: CameraInfo()
    }

    @GameScope
    @Provides
    fun provideGameLogic(
        save: Save?,
        gameStateHolder: GameStateHolder,
        cubeOpenGLModel: CubeOpenGLModel,
        gameControls: GameControls,
        subscriptionsHolder: SubscriptionsHolder,
    ): GameLogic {
        val res  =
            GameLogic(
                gameStateHolder,
                cubeOpenGLModel,
                gameControls,
                subscriptionsHolder,
            )
        if (save != null) {
            val gameState = gameStateHolder.gameStateFlow.value

            save.gameLogicToSave.applySavedData(
                gameState,
            )

            save.cubeSkinToSave.applySavedData(
                gameState.cubeInfo.cubeSkin,
                gameState.gameStatusHolder
            )
        }

        return res
    }

    @GameScope
    @Provides
    fun provideCubeCoordinates(
        gameConfig: GameConfig
    ): CubeCoordinates {
        return CubeCoordinates.createObject(gameConfig)
    }
}

@Module
interface GameControllerBindModule {

    @GameScope
    @Binds
    fun bindGameControls(
        gameControlsImp: GameControlsImp
    ): GameControls

    @GameScope
    @Binds
    fun bindPointer(pointer: PointerImp): Pointer

    @GameScope
    @Binds
    fun bindOpenGLEventsHandler(
        minesweeperOpenGLEventsHandler: MinesweeperOpenGLEventsHandler
    ): OpenGLEventsHandler
}

@Module
object SceneSettingsModule {
    @Provides
    @Named(PointerEnabledName)
    fun providePointerEnabled() = false
}

@Module
object InteractionModule {
    @GameScope
    @Provides
    fun provideBombsLeftValue(
        gameStatusHolder: GameStatusHolder,
    ): BombsLeftFlow {
        return gameStatusHolder.bombsLeftFlow
    }

    @GameScope
    @Provides
    fun provideGameStatusWithElapsedFlow(
        gameStatusHolder: GameStatusHolder,
    ): GameStatusWithElapsedFlow {
        return gameStatusHolder.gameStatusWithElapsedFlow
    }
}
