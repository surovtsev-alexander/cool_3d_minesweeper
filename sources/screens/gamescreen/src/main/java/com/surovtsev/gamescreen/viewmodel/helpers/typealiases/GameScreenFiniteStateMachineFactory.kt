package com.surovtsev.gamescreen.viewmodel.helpers.typealiases

import com.surovtsev.core.viewmodel.FiniteStateMachineFactory
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.EventToGameScreenViewModel
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.GameScreenData

typealias GameScreenFiniteStateMachineFactory =
    FiniteStateMachineFactory<EventToGameScreenViewModel, GameScreenData>
