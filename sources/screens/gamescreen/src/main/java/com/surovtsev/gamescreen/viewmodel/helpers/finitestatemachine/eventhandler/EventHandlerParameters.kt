package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.eventhandler

import com.surovtsev.gamelogic.dagger.GameComponent
import com.surovtsev.gamescreen.dagger.GameScreenComponent
import com.surovtsev.gamescreen.dagger.GameScreenScope
import com.surovtsev.gamescreen.viewmodel.helpers.typealiases.GameScreenStateHolder
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.utils.gles.renderer.GLESRenderer
import javax.inject.Inject


@GameScreenScope
class EventHandlerParameters @Inject constructor(
    val stateHolder: GameScreenStateHolder,
    val restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
    val gLESRenderer: GLESRenderer,
    val gameComponent: GameComponent,
    val gameScreenComponent: GameScreenComponent,
)
