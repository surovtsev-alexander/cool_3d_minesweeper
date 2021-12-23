package com.surovtsev.cool3dminesweeper.presentation.mainscreen

import com.surovtsev.core.presentation.Screen

typealias ButtonsInfo = Map<String, MainScreenButtonInfo>

data class MainScreenButtonInfo(
    val screen: Screen,
    val buttonType: MainScreenButtonType = MainScreenButtonType.OrdinaryButton
)
