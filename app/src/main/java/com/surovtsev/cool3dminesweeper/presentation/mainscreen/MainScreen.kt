package com.surovtsev.cool3dminesweeper.presentation.mainscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel.MainScreenViewModel
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.GrayBackground
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.Shapes
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.Test_composeTheme

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
            Modifier.background(GrayBackground)
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
                                .fillMaxWidth(fraction = 0.75f),
                            border = BorderStroke(1.dp, Color.Black),
                            shape = Shapes.small,
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
