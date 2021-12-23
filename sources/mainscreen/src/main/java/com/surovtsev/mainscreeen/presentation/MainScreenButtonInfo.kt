package com.surovtsev.mainscreeen.presentation

import com.surovtsev.core.presentation.Screen

typealias ButtonsInfo = Map<String, MainScreenButtonInfo>

data class MainScreenButtonInfo(
    val screen: Screen,
    val buttonType: MainScreenButtonType = MainScreenButtonType.OrdinaryButton
)
