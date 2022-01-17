package com.surovtsev.gamescreen.viewmodel.helpers.typealiases

import com.surovtsev.core.viewmodel.EventReceiver
import com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.EventToGameScreenViewModel

typealias GameScreenEventReceiver = EventReceiver<EventToGameScreenViewModel>
