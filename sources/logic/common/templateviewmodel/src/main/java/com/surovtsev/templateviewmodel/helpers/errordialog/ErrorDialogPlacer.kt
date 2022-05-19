package com.surovtsev.templateviewmodel.helpers.errordialog

import com.surovtsev.finitestatemachine.FiniteStateMachine
import com.surovtsev.finitestatemachine.stateholder.FSMStateFlow

typealias ScreenStateFlow = FSMStateFlow


interface ErrorDialogPlacer {
    val screenStateFlow: ScreenStateFlow
    val finiteStateMachine: FiniteStateMachine
}
