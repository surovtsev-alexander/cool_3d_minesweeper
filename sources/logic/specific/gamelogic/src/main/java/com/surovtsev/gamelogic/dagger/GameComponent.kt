/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamelogic.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.models.gles.pointer.Pointer
import com.surovtsev.core.models.gles.pointer.PointerImp
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.gamelogic.minesweeper.Minesweeper
import com.surovtsev.gamelogic.minesweeper.camerainfohelperholder.CameraInfoHelperHolder
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
import com.surovtsev.gamelogic.utils.utils.gles.view.pointer.PointerOpenGLModel.Companion.PointerEnabledName
import com.surovtsev.gamelogic.views.opengl.CubeOpenGLModel
import com.surovtsev.gamestateholder.GameStateHolder
import com.surovtsev.gamestateholder.dagger.DaggerGameStateHolderComponent
import com.surovtsev.utils.coroutines.restartablescope.subscribing.subscriptionsholder.SubscriptionsHolder
import com.surovtsev.utils.dagger.components.RestartableCoroutineScopeEntryPoint
import com.surovtsev.utils.dagger.components.SubscriptionsHolderEntryPoint
import com.surovtsev.utils.gles.renderer.OpenGLEventsHandler
import com.surovtsev.utils.gles.renderer.ScreenResolutionFlow
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
        RestartableCoroutineScopeEntryPoint::class,
        SubscriptionsHolderEntryPoint::class,
        TimeSpanComponentEntryPoint::class,
    ],
    modules = [
        GameControlsModule::class,
        GameControllerModule::class,
        SceneSettingsModule::class,
        GameControllerBindModule::class,
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
        fun restartableCoroutineScopeEntryPoint(restartableCoroutineScopeEntryPoint: RestartableCoroutineScopeEntryPoint): Builder
        fun subscriptionsHolderEntryPoint(subscriptionsHolderEntryPoint: SubscriptionsHolderEntryPoint): Builder
        fun timeSpanComponentEntryPoint(timeSpanComponentEntryPoint: TimeSpanComponentEntryPoint): Builder
        fun screenResolutionFlow(@BindsInstance screenResolutionFlow: ScreenResolutionFlow): Builder
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
    fun provideGameStateHolder(
        appComponentEntryPoint: AppComponentEntryPoint,
        timeSpanComponentEntryPoint: TimeSpanComponentEntryPoint,
    ): GameStateHolder {
        return DaggerGameStateHolderComponent
            .builder()
            .appComponentEntryPoint(
                appComponentEntryPoint
            )
            .timeSpanComponentEntryPoint(
                timeSpanComponentEntryPoint
            )
            .build()
            .gameStateHolder
    }

    @GameScope
    @Provides
    fun provideGameLogic(
        gameStateHolder: GameStateHolder,
        cubeOpenGLModel: CubeOpenGLModel,
        gameControls: GameControls,
        subscriptionsHolder: SubscriptionsHolder,
    ): GameLogic {
        return GameLogic(
            gameStateHolder,
            cubeOpenGLModel,
            gameControls,
            subscriptionsHolder,
        )
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
