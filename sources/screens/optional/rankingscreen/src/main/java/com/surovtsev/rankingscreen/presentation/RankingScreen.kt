package com.surovtsev.rankingscreen.presentation

import android.text.format.DateUtils
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.surovtsev.core.helpers.RankingDataWithPlaces
import com.surovtsev.core.helpers.RankingListWithPlaces
import com.surovtsev.core.helpers.sorting.*
import com.surovtsev.core.room.entities.Settings
import com.surovtsev.core.ui.theme.*
import com.surovtsev.core.viewmodel.PlaceErrorDialog
import com.surovtsev.core.viewmodel.ScreenState
import com.surovtsev.rankingscreen.rankinscreenviewmodel.*
import com.surovtsev.utils.time.localdatetimehelper.LocalDateTimeHelper

@Composable
fun RankingScreen(
    viewModel: RankingScreenViewModel,
    navController: NavController,
) {
    val commandHandler: RankingScreenCommandHandler = viewModel
    LaunchedEffect(key1 = Unit) {
        viewModel.finishAction = { navController.navigateUp() }
        commandHandler.handleCommand(
            EventToRankingScreenViewModel
                .LoadData
        )
    }

    RankingControls(
        viewModel.state,
        commandHandler,
        viewModel as RankingScreenErrorDialogPlacer,
    )
}

@Composable
fun RankingControls(
    stateFlow: RankingScreenStateFlow,
    commandHandler: RankingScreenCommandHandler,
    errorDialogPlacer: RankingScreenErrorDialogPlacer,
) {
    MinesweeperTheme {
        errorDialogPlacer.PlaceErrorDialog()

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(GrayBackground),
                //verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                Row(
                    modifier = Modifier.weight(3f)
                ) {
                    SettingsList(
                        stateFlow,
                        commandHandler
                    )
                }
                Divider(
                    color = Color.Black,
                    thickness = 1.dp
                )
                Row(
                    modifier = Modifier.weight(10f)
                ) {
                    RankingList(
                        stateFlow,
                        commandHandler
                    )
                }
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//            ) {
//                ExportDataButton(mainActivity, viewModel)
//            }
//
//            Toast(mainActivity, viewModel)
            }

            DisplayCircularIndicatorIfNeeded(
                stateFlow,
                this
            )

        }
    }
}

@Composable
fun SettingsList(
    stateFlow: RankingScreenStateFlow,
    commandHandler: RankingScreenCommandHandler
) {
    val rankingScreenState = stateFlow.collectAsState().value

    val rankingScreenData = rankingScreenState.screenData

    if (rankingScreenData !is RankingScreenData.SettingsListIsLoaded) {
        return
    }

    val settingsList = rankingScreenData.settingsList
    val winsCountMap = rankingScreenData.winsCountMap


    val selectedSettingsId =
        if (rankingScreenData is RankingScreenData.RankingListIsPrepared)
            rankingScreenData.selectedSettingsId
        else
            -1L

    Box(
        modifier = Modifier
            .background(GrayBackground)
            .padding(horizontal = 1.dp),
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            Row {
                Text(
                    "counts",
                    Modifier.fillMaxWidth(0.33f),
                    textAlign = TextAlign.Start
                )
                Text(
                    "bombs %",
                    Modifier.fillMaxWidth(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    "wins",
                    Modifier.fillMaxWidth(1f),
                    textAlign = TextAlign.End
                )
            }
            LazyColumn {
                items(settingsList) { item ->
                    val itemId = item.id
                    val winsCount = winsCountMap[itemId] ?: 0
                    if (selectedSettingsId == itemId) {
                        Box(
                            modifier = Modifier
                                .background(
                                    LightBlue
                                )
                        ) {
                            SettingsDataItem(item.settingsData, winsCount)
                        }
                    } else {
                        Box (
                                modifier = Modifier.clickable { commandHandler.handleCommand(
                                    EventToRankingScreenViewModel.FilterList(itemId))
                                }
                        ) {
                            SettingsDataItem(item.settingsData, winsCount)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDataItem(
    settingsData: Settings.SettingsData,
    winsCount: Long
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val counts = settingsData.dimensions.toVec3i()
        Text(
            counts.toString(),
            Modifier.fillMaxWidth(0.33f),
            textAlign = TextAlign.Start
        )
        Text(
            settingsData.bombsPercentage.toString(),
            Modifier.fillMaxWidth(0.5f),
            textAlign = TextAlign.Center
        )
        Text(
            winsCount.toString(),
            Modifier.fillMaxWidth(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun RankingList(
    stateFlow: RankingScreenStateFlow,
    commandHandler: RankingScreenCommandHandler
) {
    val rankingScreenState = stateFlow.collectAsState().value

    val rankingScreenData = rankingScreenState.screenData

    val listWithPlaces: RankingListWithPlaces
    val tableSortParameters: RankingTableSortParameters
    val directionOfSortableColumns: DirectionOfSortableColumns
    if (rankingScreenData !is RankingScreenData.RankingListIsSorted) {
        listWithPlaces = emptyList()
        tableSortParameters = DefaultRankingTableSortParameters
        directionOfSortableColumns = DefaultSortDirectionForSortableColumns
    } else {
        listWithPlaces = rankingScreenData.sortedRankingList
        tableSortParameters = rankingScreenData.rankingTableSortParameters
        directionOfSortableColumns = rankingScreenData.directionOfSortableColumns
    }

    val columnsWidth = mapOf(
        RankingTableColumn.IdTableColumn to 0.2f,
        RankingTableColumn.SortableTableColumn.DateTableColumn to 0.5f,
        RankingTableColumn.SortableTableColumn.SolvingTimeTableColumn to 1f
    )

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(DeepGray)
            .padding(horizontal = 1.dp),
    ) {
        Column {
            Row {
                for ((columnType, modifierWidth) in columnsWidth) {
                    RankingListColumnTitle(
                        commandHandler,
                        columnType,
                        modifierWidth,
                        tableSortParameters,
                        directionOfSortableColumns
                    )
                }
            }
            LazyColumn {
                items(listWithPlaces.withIndex().toList()) { item ->
                    RankingDataItem(item)
                }
            }
        }
    }
}

@Composable
fun RankingListColumnTitle(
    commandHandler: RankingScreenCommandHandler,
    tableColumnType: RankingTableColumn,
    modifierWidth: Float,
    rankingTableSortParameters: RankingTableSortParameters,
    directionOfSortableColumns: DirectionOfSortableColumns,
) {
    Row (
        Modifier.fillMaxWidth(modifierWidth),
    ) {
        Text(
            tableColumnType.columnName,
            modifier = Modifier.fillMaxWidth(0.5f),
        )
        if (tableColumnType is RankingTableColumn.SortableTableColumn) {
            val isColumnSelected = rankingTableSortParameters.rankingTableColumn == tableColumnType
            val buttonColor = if (isColumnSelected) Color.Green else Color.Gray
            val sortDirection =
                if (isColumnSelected)
                    rankingTableSortParameters.sortDirection
                else
                    directionOfSortableColumns[tableColumnType]!!

            val buttonText = sortDirection.symbol.toString()
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .background(buttonColor)
                    .clickable {
                        commandHandler.handleCommand(
                            EventToRankingScreenViewModel.SortListWithNoDelay(
                                RankingTableSortParameters(
                                    tableColumnType,
                                    if (isColumnSelected) {
                                        sortDirection.nextSortDirection()
                                    } else {
                                        sortDirection
                                    }
                                )
                            )
                        )
                    }
            ) {
                Text(
                    text = buttonText,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun RankingDataItem(
    indexedRankingData: IndexedValue<RankingDataWithPlaces>
) {
    val rankingData = indexedRankingData.value.rankingData
    /* place is counted from 0 */
    val place = indexedRankingData.value.place

    val elapsedAndPlaceString = "${
        DateUtils.formatElapsedTime(indexedRankingData.value.rankingData.elapsed / 1000)
    } (${place + 1})"

    Box ()
    {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                (indexedRankingData.index + 1).toString(),
                Modifier.fillMaxWidth(0.2f),
                textAlign = TextAlign.Start
            )
            Text(
                LocalDateTimeHelper
                    .restoreLocalDateTimeFromEpochMilli(rankingData.dateTime)
                    .toString()
                    .replace('T', ' ')
                    .split('.')[0],
                Modifier.fillMaxWidth(0.5f),
                textAlign = TextAlign.Left
            )
            Text(
                elapsedAndPlaceString,
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DisplayCircularIndicatorIfNeeded(
    stateFlow: RankingScreenStateFlow,
    boxScope: BoxScope,
) {
    val state = stateFlow.collectAsState().value

    var showLoadingElements by remember { mutableStateOf(false) }

    showLoadingElements = state is ScreenState.Loading

    with(boxScope) {
        AnimatedVisibility(
            modifier = Modifier.fillMaxSize(),
            visible = showLoadingElements,
            enter = fadeIn(animationSpec = tween(durationMillis = 150, delayMillis = 50, easing = LinearEasing)),
        ) {
            val transparency by transition.animateFloat(
                transitionSpec = {
                    when {
                        EnterExitState.Visible isTransitioningTo EnterExitState.PostExit ->
                            tween(durationMillis = 100, easing = FastOutLinearInEasing)
                        else ->
                            tween(
                                durationMillis = 250,
                                delayMillis = 250,
                                easing = LinearOutSlowInEasing
                            )
                    }
                },
                label = "transparency"
            ) {
                if (it == EnterExitState.Visible) 0.5f else 0.0f
            }
//            logcat { "scale: $transparency" }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(TransparentGray.copy(alpha = transparency)))
        }
    }

    if (state is ScreenState.Loading) {

        with(boxScope) {

//            AnimatedVisibility(
//                modifier = Modifier.fillMaxSize(),
//                visible = showLoadingElements,
//                enter = fadeIn(animationSpec = tween(durationMillis = 500, delayMillis=140, easing = LinearEasing)),
//                exit = fadeOut(animationSpec = tween(durationMillis = 500, easing = LinearEasing))
//            ) {
//
//                Box(Modifier.background(TransparentGray.copy(alpha = 0.87f)))
//            }
//            AnimatedVisibility(
//                visible = true,
//                enter = fadeIn(
//                    animationSpec = tween(
//                        10000,
//                        easing = LinearOutSlowInEasing
//                    ),
//                    initialAlpha = 0.0f
//                )
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .background(TransparentGray)
//                )
//                Text(
//                    text = "ADFDFDAFDAFDFDSFDASFDFADFDAFD"
//                )
//            }
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(TransparentGray)
//            )
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(100.dp),
                color = PrimaryColor1
            )
        }
    }
}

//@Composable
//fun ExportDataButton(
//    mainActivity: MainActivityImp,
//    viewModel: RankingScreenViewModel
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(GrayBackground),
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Button(
//                onClick = { viewModel.triggerRequestingPermissions(mainActivity) },
//                modifier = Modifier.fillMaxWidth(fraction = 0.75f),
//            ) {
//                Text(text = "exportData")
//            }
//        }
//    }
//}
//
//@Composable
//fun Toast(
//    context: Context,
//    viewModel: RankingScreenViewModel
//) {
//    val toastMessage: String by viewModel.toastMessageData.run {
//        data.observeAsState(defaultValue)
//    }
//
//    if (toastMessage == Constants.emptyString) {
//        return
//    }
//
//    android.widget.Toast.makeText(
//        context, toastMessage, android.widget.Toast.LENGTH_LONG
//    ).show()
//}
