package com.surovtsev.cool3dminesweeper.presentation.helpscreen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.surovtsev.core.theme.MinesweeperTheme
import com.surovtsev.cool3dminesweeper.viewmodels.helpscreenviewmodel.HelpScreenViewModel

@Composable
fun HelpScreen(
    viewModel: HelpScreenViewModel
) {
    HelpScreenControls(viewModel)
}

@Composable
fun HelpScreenControls(
    viewModel: HelpScreenViewModel
) {
    MinesweeperTheme {
        Text(
            "help screen"
        )
    }
}
