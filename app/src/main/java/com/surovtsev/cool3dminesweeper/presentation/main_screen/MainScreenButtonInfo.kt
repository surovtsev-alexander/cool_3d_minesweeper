package com.surovtsev.cool3dminesweeper.presentation.main_screen

import com.surovtsev.cool3dminesweeper.presentation.Screen

typealias ButtonsInfo = Map<String, MainScreenButtonInfo>

data class MainScreenButtonInfo(
    val screen: Screen,
    val caption: String,
    val buttonType: MainScreenButtonType = MainScreenButtonType.OrdinaryButton
)
