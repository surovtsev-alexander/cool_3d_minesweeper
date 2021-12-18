package com.surovtsev.cool3dminesweeper.presentation.mainscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel.MainScreenViewModel
import com.surovtsev.core.ui.theme.GrayBackground
import com.surovtsev.core.ui.theme.Shapes
import com.surovtsev.core.ui.theme.MinesweeperTheme

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    navController: NavController
) {
    val mainScreenUI = remember {
        MainScreenUI(
            viewModel = viewModel,
            navController = navController
        )
    }

    mainScreenUI.MainScreeControls()
}

class MainScreenUI(
    private val viewModel: MainScreenViewModel,
    private val navController: NavController,
) {
    @Composable
    fun MainScreeControls() {
        MinesweeperTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GrayBackground),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TwoButtonsInRow(
                            firstButtonName = MainScreenViewModel.ButtonNames.NewGame,
                            secondButtonName = MainScreenViewModel.ButtonNames.LoadGame,
                            rowScope = this,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TwoButtonsInRow(
                            firstButtonName = MainScreenViewModel.ButtonNames.Ranking,
                            secondButtonName = MainScreenViewModel.ButtonNames.Settings,
                            rowScope = this,
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                        ) {
                            MainScreenButton(
                                MainScreenViewModel.ButtonNames.Help,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TwoButtonsInRow(
        firstButtonName: String,
        secondButtonName: String,
        rowScope: RowScope,
    ) {
        rowScope.apply {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                MainScreenButton(
                    firstButtonName,
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                MainScreenButton(
                    secondButtonName,
                )
            }
        }
    }

    @Composable
    fun MainScreenButton(
        buttonName: String,
    ) {
        val buttonInfo = viewModel.buttonsInfo[buttonName]!!

        val buttonType = buttonInfo.buttonType
        val isNewGameButton =
            buttonType == MainScreenButtonType.NewGameButton
        val isLoadGameButton =
            buttonType == MainScreenButtonType.LoadGameButton

        val onClickAction = if (isNewGameButton || isLoadGameButton) {
            {
                navController.navigate(
                    buttonInfo.screen.withArgs(
                        isLoadGameButton.toString()
                    )
                )
            }
        } else {
            {
                navController.navigate(buttonInfo.screen.route)
            }
        }

        val enabled = !isLoadGameButton || viewModel.hasSave()

        Button(
            onClick = onClickAction,
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp, 10.dp),
            border = BorderStroke(1.dp, Color.Black),
            shape = Shapes.small,
            enabled = enabled
        ) {
            Text(
                text = buttonName
            )
        }
    }
}
