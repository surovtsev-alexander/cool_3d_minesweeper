package com.surovtsev.cool_3d_minesweeper.views.activities

import android.os.Bundle
import android.text.format.DateUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.surovtsev.cool_3d_minesweeper.presentation.ui.theme.Test_composeTheme
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.surovtsev.cool_3d_minesweeper.controllers.application_controller.daggerComponentsHolder
import com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_view_model.RankingActivityEvents
import com.surovtsev.cool_3d_minesweeper.model_views.ranking_activity_view_model.RankingActivityViewModel
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.RankingData
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.presentation.ui.theme.DeepGray
import com.surovtsev.cool_3d_minesweeper.presentation.ui.theme.GrayBackground
import com.surovtsev.cool_3d_minesweeper.presentation.ui.theme.LightBlue
import javax.inject.Inject


//class RankingActivity: ComponentActivity() {
//    @Inject
//    lateinit var viewModel: RankingActivityViewModel
//    @Inject
//    lateinit var rankingActivityEvents: RankingActivityEvents
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        daggerComponentsHolder
//            .createAndGetRankingComponent()
//            .inject(this)
//
//        setContent {
//            RankingControls(viewModel, rankingActivityEvents)
//        }
//
//        viewModel.loadData()
//    }
//}
//
//@Composable
//fun RankingControls(
//    viewModel: RankingActivityViewModel,
//    rankingActivityEvents: RankingActivityEvents
//) {
//    Test_composeTheme {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .border(1.dp, Color.Black),
//            //verticalArrangement = Arrangement.spacedBy(15.dp),
//        ) {
//            Row(
//                modifier = Modifier.fillMaxHeight(.3f)
//            ) {
//                SettingsList(viewModel, rankingActivityEvents)
//            }
//            Row(
//                modifier = Modifier.fillMaxHeight(1f)
//            ) {
//                RankingList(rankingActivityEvents)
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun SettingsList(
//    viewModel: RankingActivityViewModel,
//    rankingActivityEvents: RankingActivityEvents
//) {
//    val settingsList: List<DataWithId<SettingsData>> by rankingActivityEvents.settingsListWithIds.run {
//        data.observeAsState(defaultValue)
//    }
//    val selectedSettingsId: Int by rankingActivityEvents.selectedSettingsId.run {
//        data.observeAsState(defaultValue)
//    }
//    val winsCountMap: Map<Int, Int> by rankingActivityEvents.winsCount.run {
//        data.observeAsState(defaultValue)
//    }
//
//    Box(
//        modifier = Modifier
//            .background(GrayBackground)
//            .border(1.dp, Color.Black)
//            .padding(horizontal = 1.dp),
//    ) {
//        Column(
//            Modifier.fillMaxSize()
//        ) {
//            Row {
//                Text(
//                    "counts",
//                    Modifier.fillMaxWidth(0.33f),
//                    textAlign = TextAlign.Start
//                )
//                Text(
//                    "bombs %",
//                    Modifier.fillMaxWidth(0.5f),
//                    textAlign = TextAlign.Center
//                )
//                Text(
//                    "wins",
//                    Modifier.fillMaxWidth(1f),
//                    textAlign = TextAlign.End
//                )
//            }
//            LazyColumn {
//                items(settingsList) { item ->
//                    val itemId = item.id
//                    if (winsCountMap.containsKey(itemId)) {
//                        val winsCount = winsCountMap[itemId]!!
//                        if (selectedSettingsId == itemId) {
//                            Box(
//                                modifier = Modifier
//                                    .background(
//                                        LightBlue
//                                    )
//
//                            ) {
//                                SettingsDataItem(item, winsCount)
//                            }
//                        } else {
//                            Surface(
//                                shape = MaterialTheme.shapes.large,
//                                onClick = { viewModel.loadRankingForSettingsId(itemId) },
//                            ) {
//                                SettingsDataItem(item, winsCount)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun SettingsDataItem(
//    settingDataWithId: DataWithId<SettingsData>,
//    winsCount: Int
//) {
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//    ) {
//        val settingsData = settingDataWithId.data
//        val counts = settingsData.getCounts()
//        Text(
//            counts.toString(),
//            Modifier.fillMaxWidth(0.33f),
//            textAlign = TextAlign.Start
//        )
//        Text(
//            settingsData.bombsPercentage.toString(),
//            Modifier.fillMaxWidth(0.5f),
//            textAlign = TextAlign.Center
//        )
//        Text(
//            winsCount.toString(),
//            Modifier.fillMaxWidth(1f),
//            textAlign = TextAlign.End
//        )
//    }
//}
//
//@Composable
//fun RankingList(
//    rankingActivityEvents: RankingActivityEvents
//) {
//    val filteredRankingList: List<RankingData> by rankingActivityEvents.filteredRankingList.run {
//        data.observeAsState(defaultValue)
//    }
//    Box (
//        modifier = Modifier
//            .fillMaxSize()
//            .background(DeepGray)
//            .border(1.dp, Color.Black)
//            .padding(horizontal = 1.dp),
//    ) {
//        Column {
//            Row {
//                Text(
//                    "#",
//                    Modifier.fillMaxWidth(0.2f),
//                    textAlign = TextAlign.Start
//                )
//                Text(
//                    "date",
//                    Modifier.fillMaxWidth(0.5f),
//                    textAlign = TextAlign.Center
//                )
//                Text(
//                    "seconds",
//                    Modifier.fillMaxWidth(1f),
//                    textAlign = TextAlign.End
//                )
//            }
//            LazyColumn {
//                items(filteredRankingList.withIndex().toList()) { item ->
//                    RankingDataItem(item)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun RankingDataItem(
//    indexedRankingData: IndexedValue<RankingData>
//) {
//    Box ()
//    {
//        Row(
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(
//                (indexedRankingData.index + 1).toString(),
//                Modifier.fillMaxWidth(0.2f),
//                textAlign = TextAlign.Start
//            )
//            Text(
//                indexedRankingData.value.dateTime.replace('T', ' ').split('.')[0],
//                Modifier.fillMaxWidth(0.5f),
//                textAlign = TextAlign.Center
//            )
//            Text(
//                DateUtils.formatElapsedTime(indexedRankingData.value.elapsed / 1000),
//                Modifier.fillMaxWidth(),
//                textAlign = TextAlign.End
//
//            )
//        }
//    }
//}

