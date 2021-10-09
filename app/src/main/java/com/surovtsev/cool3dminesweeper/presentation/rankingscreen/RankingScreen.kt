package com.surovtsev.cool3dminesweeper.presentation.rankingscreen

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.RankingScreenViewModel
import com.surovtsev.cool3dminesweeper.models.game.database.DataWithId
import com.surovtsev.cool3dminesweeper.models.game.database.RankingData
import com.surovtsev.cool3dminesweeper.models.game.database.SettingsData
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.DeepGray
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.GrayBackground
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.LightBlue
import com.surovtsev.cool3dminesweeper.presentation.ui.theme.Test_composeTheme
import com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers.*

@Composable
fun RankingScreen(
    viewModel: RankingScreenViewModel
) {
    RankingControls(
        viewModel
    )
}

@Composable
fun RankingControls(
    viewModel: RankingScreenViewModel,
) {
    val rankingScreenEvents = viewModel.rankingScreenEvents

    Test_composeTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Black),
            //verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxHeight(.3f)
            ) {
                SettingsList(viewModel, rankingScreenEvents)
            }
            Row(
                modifier = Modifier.fillMaxHeight(1f)
            ) {
                RankingList(viewModel)
            }
        }
    }
}

@Composable
fun SettingsList(
    viewModel: RankingScreenViewModel,
    rankingScreenEvents: RankingScreenEvents
) {
    val settingsList: List<DataWithId<SettingsData>> by rankingScreenEvents.settingsListWithIds.run {
        data.observeAsState(defaultValue)
    }
    val selectedSettingsId: Int by rankingScreenEvents.selectedSettingsId.run {
        data.observeAsState(defaultValue)
    }
    val winsCountMap: Map<Int, Int> by rankingScreenEvents.winsCount.run {
        data.observeAsState(defaultValue)
    }

    Box(
        modifier = Modifier
            .background(GrayBackground)
            .border(1.dp, Color.Black)
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
                    if (winsCountMap.containsKey(itemId)) {
                        val winsCount = winsCountMap[itemId]!!
                        if (selectedSettingsId == itemId) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        LightBlue
                                    )
                            ) {
                                SettingsDataItem(item, winsCount)
                            }
                        } else {
                            Box (
                                modifier = Modifier.clickable { viewModel.loadRankingForSettingsId(itemId) }
                            ) {
                                SettingsDataItem(item, winsCount)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDataItem(
    settingDataWithId: DataWithId<SettingsData>,
    winsCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val settingsData = settingDataWithId.data
        val counts = settingsData.getCounts()
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
    viewModel: RankingScreenViewModel,
) {
    val rankingListToDisplay: List<RankingData> by viewModel.rankingScreenEvents.rankingListToDisplay.run {
        data.observeAsState(defaultValue)
    }
    val rankingTableSortType: RankingTableSortType by viewModel.rankingTableSortTypeData.run {
        data.observeAsState(defaultValue)
    }

    val columnsWidth = mapOf(
        RankingColumn.IdColumn to 0.2f,
        RankingColumn.SortableColumn.DateColumn to 0.5f,
        RankingColumn.SortableColumn.SolvingTimeColumn to 1f
    )

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(DeepGray)
            .border(1.dp, Color.Black)
            .padding(horizontal = 1.dp),
    ) {
        Column {
            Row {
                for ((columnType, modifierWidth) in columnsWidth) {
                    RankingListColumnTitle(
                        viewModel, columnType, modifierWidth, rankingTableSortType
                    )
                }
            }
            LazyColumn {
                items(rankingListToDisplay.withIndex().toList()) { item ->
                    RankingDataItem(item)
                }
            }
        }
    }
}

@Composable
fun RankingListColumnTitle(
    viewModel: RankingScreenViewModel,
    columnType: RankingColumn,
    modifierWidth: Float,
    rankingTableSortType: RankingTableSortType
) {
    Row (
        Modifier.fillMaxWidth(modifierWidth),
    ) {
        Text(
            columnType.columnName,
            modifier = Modifier.fillMaxWidth(0.5f),
        )
        if (columnType is RankingColumn.SortableColumn) {
            val isColumnSelected = rankingTableSortType.rankingColumn == columnType
            val buttonColor = if (isColumnSelected) Color.Green else Color.Gray
            val buttonText =
                if (!(isColumnSelected && rankingTableSortType.sortDirection == SortDirection.Descending)) "u" else "d"
            Box(
                modifier = Modifier
                    .width(30.dp)
                    .background(buttonColor)
                    .clickable { viewModel.selectSortColumn(columnType) }
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
    indexedRankingData: IndexedValue<RankingData>
) {
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
                indexedRankingData.value.dateTime.replace('T', ' ').split('.')[0],
                Modifier.fillMaxWidth(0.5f),
                textAlign = TextAlign.Left
            )
            Text(
                DateUtils.formatElapsedTime(indexedRankingData.value.elapsed / 1000),
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
    }
}
