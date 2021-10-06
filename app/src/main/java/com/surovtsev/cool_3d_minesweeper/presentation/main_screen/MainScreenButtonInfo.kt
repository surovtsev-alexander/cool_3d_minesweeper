package com.surovtsev.cool_3d_minesweeper.presentation.main_screen

import com.surovtsev.cool_3d_minesweeper.presentation.Screen

typealias ButtonsInfo = Map<String, MainScreenButtonInfo>

data class MainScreenButtonInfo(
    val screen: Screen,
    val caption: String,
    val buttonType: MainScreenButtonType = MainScreenButtonType.OrdinaryButton
)
