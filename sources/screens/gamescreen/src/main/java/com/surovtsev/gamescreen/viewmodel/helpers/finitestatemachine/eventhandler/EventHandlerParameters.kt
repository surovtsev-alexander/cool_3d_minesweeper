package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.eventhandler

import com.surovtsev.finitestatemachine.stateholder.StateHolder
import com.surovtsev.gamelogic.dagger.GameComponent
import com.surovtsev.gamescreen.dagger.GameScreenScope
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent
import com.surovtsev.touchlistener.dagger.TouchListenerComponent
import com.surovtsev.utils.gles.renderer.GLESRenderer
import javax.inject.Inject


@GameScreenScope
class EventHandlerParameters @Inject constructor(
    val stateHolder: StateHolder,
    val restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
    val gLESRenderer: GLESRenderer,
    val gameComponent: GameComponent,
    val timeSpanComponent: TimeSpanComponent,
    val touchListenerComponent: TouchListenerComponent,
)
