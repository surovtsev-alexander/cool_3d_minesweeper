package com.surovtsev.cool_3d_minesweeper.presentation.game_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.surovtsev.cool_3d_minesweeper.dagger.componentsHolder.DaggerComponentsHolder

const val LoadGameParameterName = "load_game"

@Composable
fun GameScreen(
    daggerComponentsHolder: DaggerComponentsHolder,
    loadGame: Boolean
) {
    Column {
        Text(text = "GameScreen")
        Text(text = "load_game: $loadGame")
    }
}
