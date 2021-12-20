package com.surovtsev.cool3dminesweeper.presentation.mainscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
    MainScreeControls(
        viewModel,
        navController
    )
}

@Composable
fun MainScreeControls(
    viewModel: MainScreenViewModel,
    navController: NavController,
) {
    val hasSave by viewModel.hasSave.observeAsState(false)
    val buttonsInfo = viewModel.buttonsInfo

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
                        hasSave,
                        firstButtonName = MainScreenViewModel.ButtonNames.NewGame,
                        secondButtonName = MainScreenViewModel.ButtonNames.LoadGame,
                        rowScope = this,
                        navController,
                        buttonsInfo,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TwoButtonsInRow(
                        hasSave,
                        firstButtonName = MainScreenViewModel.ButtonNames.Ranking,
                        secondButtonName = MainScreenViewModel.ButtonNames.Settings,
                        rowScope = this,
                        navController,
                        buttonsInfo,
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
                            hasSave,
                            MainScreenViewModel.ButtonNames.Help,
                            navController,
                            buttonsInfo,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TwoButtonsInRow(
    hasSave: Boolean,
    firstButtonName: String,
    secondButtonName: String,
    rowScope: RowScope,
    navController: NavController,
    buttonsInfo: ButtonsInfo,
) {
    rowScope.apply {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
        ) {
            MainScreenButton(
                hasSave,
                firstButtonName,
                navController,
                buttonsInfo,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
        ) {
            MainScreenButton(
                hasSave,
                secondButtonName,
                navController,
                buttonsInfo,
            )
        }
    }
}

@Composable
fun MainScreenButton(
    hasSave: Boolean,
    buttonName: String,
    navController: NavController,
    buttonsInfo: ButtonsInfo,
) {
    val buttonInfo = buttonsInfo[buttonName]!!

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

    val enabled = !isLoadGameButton || hasSave

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
