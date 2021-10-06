package com.surovtsev.cool_3d_minesweeper.presentation.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.surovtsev.cool_3d_minesweeper.model_views.main_screen_view_model.MainScreenViewModel
import com.surovtsev.cool_3d_minesweeper.presentation.ui.theme.GrayBackground
import com.surovtsev.cool_3d_minesweeper.presentation.ui.theme.Test_composeTheme

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    navController: NavController
) {
    MainScreeControls(
        viewModel,
        navController
    )
}

@Composable
fun MainScreeControls(
    viewModel: MainScreenViewModel,
    navController: NavController
) {
    val buttonsInfo = viewModel.buttonsInfo
    val hasSave = viewModel.hasSave()

    Test_composeTheme {
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
                    buttonsInfo.forEach { (_, bI ) ->
                        val buttonType = bI.buttonType
                        val isNewGameButton =
                            buttonType == MainScreenButtonType.NewGameButton
                        val isLoadGameButton =
                            buttonType == MainScreenButtonType.LoadGameButton
                        Button(
                            onClick = {
                                if (isNewGameButton || isLoadGameButton) {
                                    navController.navigate(
                                        bI.screen.withArgs(
                                            if (isNewGameButton) false.toString() else hasSave.toString()
                                        )
                                    )
                                } else {
                                    navController.navigate(bI.screen.route)
                                }
                            },
                            Modifier
                                .fillMaxWidth(fraction = 0.75f)
                                .border(1.dp, Color.Black),
                            enabled = !isLoadGameButton || hasSave
                        ) {
                            Text(text = bI.caption)
                        }
                    }
                }
            }
        }
    }
}
