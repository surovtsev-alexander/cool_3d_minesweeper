package com.surovtsev.cool_3d_minesweeper.presentation.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.surovtsev.cool_3d_minesweeper.presentation.Screen
import com.surovtsev.cool_3d_minesweeper.presentation.ui.theme.GrayBackground
import com.surovtsev.cool_3d_minesweeper.views.activities.MainMenuButton

@Composable
fun MainScreen(
    navController: NavController
) {
    data class ButtonInfo(
        val route: String,
        val caption: String,
        val disabledIfNoStoredGame: Boolean = false
    )

    val buttonsInfo = listOf(
        ButtonInfo(Screen.GameScreen.route, "new game"),
        ButtonInfo(Screen.GameScreen.route,"load game", true),
        ButtonInfo(Screen.RankingScreen.route, "ranking"),
        ButtonInfo(Screen.SettingsScreen.route, "settings")
    )

    Text(text = "MainScreen")
    Box(
        Modifier.background(GrayBackground)//Color(0xFF48cae4))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                buttonsInfo.forEach { bI ->
                    Button(
                        onClick = {
                            navController.navigate(bI.route) },
                        Modifier
                            .fillMaxWidth(fraction = 0.75f)
                            .border(1.dp, Color.Black)
                    ) {
                        Text(text = bI.caption)
                    }
                }
            }
        }
    }
}
