package com.surovtsev.mainscreeen.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.surovtsev.core.ui.theme.GrayBackground
import com.surovtsev.core.ui.theme.MinesweeperTheme
import com.surovtsev.core.ui.theme.Shapes
import com.surovtsev.mainscreeen.viewmodel.MainScreenViewModel

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
    val hasSave by viewModel.hasSave.collectAsState()
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
                val buttonPairs = listOf(
                    MainScreenViewModel.ButtonNames.NewGame to MainScreenViewModel.ButtonNames.LoadGame,
                    MainScreenViewModel.ButtonNames.Ranking to MainScreenViewModel.ButtonNames.Settings
                )

                buttonPairs.map { (firstButtonName, secondButtonName) ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        TwoButtonsInRow(
                            hasSave,
                            firstButtonName = firstButtonName,
                            secondButtonName = secondButtonName,
                            rowScope = this,
                            navController,
                            buttonsInfo,
                        )
                    }
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
                        SingleButton(
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
        listOf(
            firstButtonName,
            secondButtonName
        ).map { buttonName ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
            ) {
                SingleButton(
                    hasSave,
                    buttonName,
                    navController,
                    buttonsInfo,
                )
            }

        }
    }
}

@Composable
fun SingleButton(
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
