/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.surovtsev.finitestatemachine.eventreceiver.EventReceiver
import com.surovtsev.finitestatemachine.state.description.Description
import com.surovtsev.rankingscreen.rankinscreenviewmodel.RankingScreenViewModel
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.EventToRankingScreenViewModel
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.finitestatemachine.RankingScreenData
import com.surovtsev.templateviewmodel.helpers.errordialog.ErrorDialogPlacer
import com.surovtsev.templateviewmodel.helpers.errordialog.PlaceErrorDialog
import com.surovtsev.templateviewmodel.helpers.errordialog.ScreenStateFlow
import com.surovtsev.utils.compose.components.scrollbar.LazyListScrollbarContext
import com.surovtsev.utils.compose.components.scrollbar.ScrollBar
import com.surovtsev.utils.time.localdatetimehelper.LocalDateTimeHelper

@Composable
fun RankingScreen(
    viewModel: RankingScreenViewModel,
    navController: NavController,
    dipCoefficient: Float,
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.finishActionHolder.finishAction =
        {
            navController.navigateUp()
        }
        viewModel.restartFSM()
    }

    RankingControls(
        viewModel.screenStateFlow,
        viewModel.finiteStateMachine.eventReceiver,
        viewModel as ErrorDialogPlacer,
        dipCoefficient,
    )
}

@Composable
fun RankingControls(
    screenStateFlow: ScreenStateFlow,
    eventReceiver: EventReceiver,
    errorDialogPlacer: ErrorDialogPlacer,
    dipCoefficient: Float,
) {
    MinesweeperTheme {
        errorDialogPlacer.PlaceErrorDialog(GrayBackground)

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
                        screenStateFlow,
                        eventReceiver,
                        dipCoefficient,
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
                        screenStateFlow,
                        eventReceiver,
                        dipCoefficient,
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
                screenStateFlow,
                this
            )

        }
    }
}

@Composable
fun SettingsList(
    screenStateFlow: ScreenStateFlow,
    eventReceiver: EventReceiver,
    dipCoefficient: Float,
) {
    val rankingScreenState = screenStateFlow.collectAsState().value

    val rankingScreenData = rankingScreenState.data

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
            val lazyListScrollbarContext = LazyListScrollbarContext(
                rememberLazyListState(),
                dipCoefficient,
            ).apply {
                updateElementsCount(settingsList.count())
            }
            Row {
                Text(
                    "counts",
                    Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                )
                Text(
                    "bombs %",
                    Modifier.weight(1f),
                    textAlign = TextAlign.End,
                )
                Text(
                    "wins",
                    Modifier.weight(1f),
                    textAlign = TextAlign.End,
                )
                Spacer(modifier = Modifier.width(lazyListScrollbarContext.widthDp))
            }
            Row {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    state = lazyListScrollbarContext.lazyListState,
                ) {
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
                            Box(
                                modifier = Modifier.clickable {
                                    eventReceiver.receiveEvent(
                                        EventToRankingScreenViewModel.FilterList(itemId)
                                    )
                                }
                            ) {
                                SettingsDataItem(item.settingsData, winsCount)
                            }
                        }
                    }
                }
                ScrollBar(
                    modifier = Modifier,
                    lazyListScrollbarContext
                )
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
            Modifier.weight(1f),
            textAlign = TextAlign.Start,
        )
        Text(
            settingsData.bombsPercentage.toString(),
            Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
        Text(
            winsCount.toString(),
            Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
fun RankingList(
    screenStateFlow: ScreenStateFlow,
    eventReceiver: EventReceiver,
    dipCoefficient: Float,
) {
    val rankingScreenState = screenStateFlow.collectAsState().value

    val rankingScreenData = rankingScreenState.data

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

    data class TitleColumnInfo(
        val columnType: RankingTableColumn,
        val widthWeight: Float,
        val textAlign: TextAlign = TextAlign.End,
    )
    val titleColumnsInfo = listOf(
        TitleColumnInfo(RankingTableColumn.IdTableColumn, 1f, TextAlign.Start),
        TitleColumnInfo(RankingTableColumn.SortableTableColumn.DateTableColumn, 3f),
        TitleColumnInfo(RankingTableColumn.SortableTableColumn.SolvingTimeTableColumn, 2f)
    )

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(DeepGray)
            .padding(horizontal = 1.dp),
    ) {
        Column {
            val lazyListScrollbarContext = LazyListScrollbarContext(
                rememberLazyListState(),
                dipCoefficient,
            ).apply {
                updateElementsCount(listWithPlaces.count())
            }
            Row {
                for (tCI in titleColumnsInfo) {
                    RankingListColumnTitle(
                        Modifier.weight(tCI.widthWeight),
                        eventReceiver,
                        tCI.columnType,
                        tableSortParameters,
                        directionOfSortableColumns,
                        tCI.textAlign,
                    )
                }
                Spacer(modifier = Modifier.width(lazyListScrollbarContext.widthDp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                LazyColumn(
                    modifier =
                    Modifier
                        .fillMaxHeight()
                        .weight(1f),
                    state = lazyListScrollbarContext.lazyListState,
                ) {
                    items(listWithPlaces.withIndex().toList()) { item ->
                        RankingDataItem(item)
                    }
                }

                ScrollBar(
                    modifier = Modifier,
                    lazyListScrollbarContext,
                )
            }
        }
    }
}

@Composable
fun RankingListColumnTitle(
    modifier: Modifier,
    eventReceiver: EventReceiver,
    tableColumnType: RankingTableColumn,
    rankingTableSortParameters: RankingTableSortParameters,
    directionOfSortableColumns: DirectionOfSortableColumns,
    textAlign: TextAlign,
) {
    Row (
        modifier,
    ) {
        Text(
            tableColumnType.columnName,
            modifier = Modifier.weight(1f),
            textAlign = textAlign,
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
                        eventReceiver.receiveEvent(
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
                Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            Text(
                LocalDateTimeHelper
                    .restoreLocalDateTimeFromEpochMilli(rankingData.dateTime)
                    .toString()
                    .replace('T', ' ')
                    .split('.')[0],
                Modifier.weight(3f),
                textAlign = TextAlign.End
            )
            Text(
                elapsedAndPlaceString,
                Modifier.weight(2f),
                textAlign = TextAlign.End
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun DisplayCircularIndicatorIfNeeded(
    screenStateFlow: ScreenStateFlow,
    boxScope: BoxScope,
) {
    val state = screenStateFlow.collectAsState().value

    var showLoadingElements by remember { mutableStateOf(false) }

    showLoadingElements = state.description is Description.Loading

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

    if (state.description is Description.Loading) {

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
