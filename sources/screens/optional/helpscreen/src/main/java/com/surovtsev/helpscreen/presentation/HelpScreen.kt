package com.surovtsev.helpscreen.presentation

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.surovtsev.core.ui.theme.MinesweeperTheme
import com.surovtsev.helpscreen.viewmodel.HelpScreenViewModel

@Composable
fun HelpScreen(
    viewModel: HelpScreenViewModel
) {
    HelpScreenControls(viewModel)
}

@Composable
fun HelpScreenControls(
    @Suppress("UNUSED_PARAMETER") viewModel: HelpScreenViewModel
) {
    MinesweeperTheme {
        Text(
            "help screen"
        )
    }
}
