package com.surovtsev.gamescreen.viewmodel.helpers.typealiases

import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.GameScreenData

typealias GameScreenFiniteStateMachine =
    FiniteStateMachine<GameScreenData>
