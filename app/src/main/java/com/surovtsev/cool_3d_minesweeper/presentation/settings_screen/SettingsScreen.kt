package com.surovtsev.cool_3d_minesweeper.presentation.settings_screen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.surovtsev.cool_3d_minesweeper.dagger.componentsHolder.DaggerComponentsHolder

@Composable
fun SettingsScreen(
    daggerComponentsHolder: DaggerComponentsHolder
) {
    Text(text = "SettingsScreen")
}
