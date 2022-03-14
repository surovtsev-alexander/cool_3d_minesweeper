package com.surovtsev.gamescreen.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.GameScreenEntryPoint
import com.surovtsev.core.viewmodel.FiniteStateMachineFactory
import com.surovtsev.core.viewmodel.ScreenStateFlow
import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.gamelogic.dagger.DaggerGameComponent
import com.surovtsev.gamelogic.dagger.GameComponent
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.eventhandler.EventHandlerImp
import com.surovtsev.gamescreen.viewmodel.helpers.gamenotpausedflowholder.GameNotPausedFlowHolder
import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.timespan.dagger.DaggerTimeSpanComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent
import com.surovtsev.touchlistener.dagger.DaggerTouchListenerComponent
import com.surovtsev.touchlistener.dagger.TouchListenerComponent
import com.surovtsev.utils.gles.renderer.GLESRenderer
import com.surovtsev.utils.gles.renderer.ScreenResolutionFlow
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

@GameScreenScope
@Component(
    dependencies = [
        AppComponentEntryPoint::class,
    ],
    modules = [
        GameScreenModule::class,
        GameScreenComponentsModule::class,
    ]
)
interface GameScreenComponent: GameScreenEntryPoint {
    val gLESRenderer: GLESRenderer
    val restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent
    val gameComponent: GameComponent
    val touchListenerComponent: TouchListenerComponent
    val timeSpanComponent: TimeSpanComponent

    val finiteStateMachine: FiniteStateMachine

    @Component.Builder
    interface Builder {
        fun appComponentEntryPoint(
            appComponentEntryPoint: AppComponentEntryPoint
        ): Builder

        fun screenStateFlow(
            @BindsInstance
            screenStateFlow: ScreenStateFlow,
        ): Builder

        fun stateHolder(
            @BindsInstance
            stateHolder: StateHolder
        ): Builder

        fun finiteStateMachineFactory(
            @BindsInstance
            finiteStateMachineFactory: FiniteStateMachineFactory
        ): Builder

        fun build(
        ): GameScreenComponent
    }
}

@Module
object GameScreenModule {
    @GameScreenScope
    @Provides
    fun provideGameScreenFiniteStateMachine(
        finiteStateMachineFactory: FiniteStateMachineFactory,
        eventHandler: EventHandlerImp,
        restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
    ): FiniteStateMachine {
        return finiteStateMachineFactory(
            eventHandler,
            SubscriptionsHolderComponentFactoryHolderImp
                .createAndSubscribe(
                    restartableCoroutineScopeComponent,
                    "GameScreen:FiniteStateMachine"
                )
                .subscriptionsHolder
        )
    }

    @GameScreenScope
    @Provides
    fun provideGLESRenderer(
    ): GLESRenderer {
        return GLESRenderer()
    }

    @GameScreenScope
    @Provides
    fun provideScreenResolutionFlow(
        gLESRenderer: GLESRenderer,
    ): ScreenResolutionFlow {
        return gLESRenderer.screenResolutionFlow
    }
}

@Module
object GameScreenComponentsModule {
    @GameScreenScope
    @Provides
    fun provideRestartableCoroutineScopeComponent(
    ): RestartableCoroutineScopeComponent {
        return DaggerRestartableCoroutineScopeComponent
            .create()
    }

    @GameScreenScope
    @Provides
    fun provideTimeSpanComponent(
        restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
    ): TimeSpanComponent {
        return DaggerTimeSpanComponent
            .builder()
            .subscriptionsHolderEntryPoint(
                SubscriptionsHolderComponentFactoryHolderImp.createAndSubscribe(
                    restartableCoroutineScopeComponent,
                    "GameScreen:TimeSpanComponent"
                )
            )
            .build()
    }

    @GameScreenScope
    @Provides
    fun provideTouchListenerComponent(
        timeSpanComponent: TimeSpanComponent,
        restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
    ): TouchListenerComponent {
        return DaggerTouchListenerComponent
            .builder()
            .timeSpanComponentEntryPoint(
                timeSpanComponent,
            )
            .subscriptionsHolderEntryPoint(
                SubscriptionsHolderComponentFactoryHolderImp.createAndSubscribe(
                    restartableCoroutineScopeComponent,
                    "GameScreen:TouchListenerComponent"
                )
            )
            .build()
    }

    @GameScreenScope
    @Provides
    fun provideGameComponent(
        appComponentEntryPoint: AppComponentEntryPoint,
        restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
        timeSpanComponent: TimeSpanComponent,
        screenResolutionFlow: ScreenResolutionFlow,
        gameNotPausedFlowHolder: GameNotPausedFlowHolder,
    ): GameComponent {
        return DaggerGameComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .restartableCoroutineScopeEntryPoint(restartableCoroutineScopeComponent)
            .subscriptionsHolderEntryPoint(
                SubscriptionsHolderComponentFactoryHolderImp.createAndSubscribe(
                    restartableCoroutineScopeComponent,
                    "GameScreen:GameComponent"
                )
            )
            .timeSpanComponentEntryPoint(timeSpanComponent)
            .screenResolutionFlow(screenResolutionFlow)
            .gameNotPausedFlow(gameNotPausedFlowHolder.gameNotPausedFlow)
            .build()
    }
}
