package com.surovtsev.gamelogic.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.GameScreenEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.core.dagger.dependencies.GameStateDependencies
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.interaction.BombsLeftFlow
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.gamelogic.minesweeper.Minesweeper
import com.surovtsev.gamelogic.minesweeper.gameState.CameraInfoHelperHolder
import com.surovtsev.gamelogic.minesweeper.gameState.GameStateHolder
import com.surovtsev.gamelogic.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.GameStatusHolderBridge
import com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow.GameNotPausedFlow
import com.surovtsev.gamelogic.minesweeper.interaction.opengleventshandler.MinesweeperOpenGLEventsHandler
import com.surovtsev.gamelogic.minesweeper.interaction.screeninteractionhandler.move.MoveHandlerImp
import com.surovtsev.gamelogic.minesweeper.interaction.screeninteractionhandler.touch.TouchHandlerImp
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsFlows
import com.surovtsev.gamelogic.minesweeper.interaction.ui.UIGameControlsMutableFlows
import com.surovtsev.gamelogic.models.game.interaction.GameControls
import com.surovtsev.gamelogic.models.game.interaction.GameControlsImp
import com.surovtsev.core.models.gles.pointer.Pointer
import com.surovtsev.core.models.gles.pointer.PointerImp
import com.surovtsev.gamelogic.utils.utils.gles.view.pointer.PointerOpenGLModel.Companion.PointerEnabledName
import com.surovtsev.gamelogic.views.opengl.CubeOpenGLModel
import com.surovtsev.gamestate.dagger.GameStateScope
import com.surovtsev.gamestate.models.game.gamestatus.GameStatusHolder
import com.surovtsev.gamestate.models.game.gamestatus.GameStatusWithElapsedFlow
import com.surovtsev.gamestate.models.game.save.Save
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import com.surovtsev.utils.dagger.components.RestartableCoroutineScopeEntryPoint
import com.surovtsev.utils.dagger.components.SubscriptionsHolderEntryPoint
import com.surovtsev.utils.gles.renderer.OpenGLEventsHandler
import com.surovtsev.utils.math.FloatingAverage
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
    val settingsDao: SettingsDao
    val rankingDao: RankingDao
    val rankingListHelper: RankingListHelper
    val minesweeper: Minesweeper

    val touchHandlerImp: TouchHandlerImp
    val moveHandlerImp: MoveHandlerImp
    val manuallyUpdatableTimeAfterDeviceStartupFlowHolder: ManuallyUpdatableTimeAfterDeviceStartupFlowHolder

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
        fun gameStateDependencies(@BindsInstance gameStateDependencies: GameStateDependencies): Builder
        fun gameNotPausedFlow(@BindsInstance gameNotPausedFlow: GameNotPausedFlow): Builder
        fun build(): GameComponent
    }
}

@Module
object GameControlsModule {
    @GameScope
    @Provides
    fun providePointerImp(
    ): PointerImp {
        return PointerImp()
    }

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
        gameStatusHolderBridge: GameStatusHolderBridge,
        asyncTimeSpan: AsyncTimeSpan,
        delayedFPSFlowHolder: DelayedFPSFlowHolder,
    ): UIGameControlsFlows {
        return UIGameControlsFlows(
            uiGameControlsMutableFlows.flagging,
            uiGameControlsMutableFlows.uiGameStatus,
            gameStatusHolderBridge.bombsLeftFlow,
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
    fun provideGameLogic(
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
        return res
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
//    @GameScope
//    @Provides
//    fun provideGameStatusWithElapsedFlow(
//        gameStatusHolder: GameStatusHolder,
//    ): GameStatusWithElapsedFlow {
//        return gameStatusHolder.gameStatusWithElapsedFlow
//    }
}
