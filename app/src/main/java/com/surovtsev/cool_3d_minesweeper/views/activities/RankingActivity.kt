package com.surovtsev.cool_3d_minesweeper.views.activities

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.surovtsev.cool_3d_minesweeper.views.theme.Test_composeTheme
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.*
import com.surovtsev.cool_3d_minesweeper.utils.live_data.MyLiveData


class RankingActivity: ComponentActivity() {
    private class ModelView() {
        val settingsList = MyLiveData<List<DataWithId<SettingsData>>>(
            listOf<DataWithId<SettingsData>>()
        )
        val rankingList = MyLiveData<List<RankingData>>(
            listOf<RankingData>()
        )
        val selectedSettingsId = MyLiveData<Int>(-1)
    }

    private val modelView = ModelView()
    private val dbHelper = DBHelper(this)
    private val settingsDBQueries = SettingsDBQueries(dbHelper)
    private val rankingDBQueries = RankingDBQueries(dbHelper)

    private var rankingList: List<RankingData>? = null
    private var winsCount: Map<Int, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RankingControls()
        }

        modelView.settingsList.onDataChanged(
            settingsDBQueries.getSettingsList()
        )

        rankingList = rankingDBQueries.getRankingList()
        winsCount = rankingList?.map{ it.settingId }?.groupingBy { it }?.eachCount()
    }

    @Composable
    fun RankingControls() {
        Test_composeTheme {
            Surface(color = MaterialTheme.colors.background) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    //verticalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxHeight(.3f)
                    ) {
                        SettingsList()
                    }
                    Row(
                        modifier = Modifier.fillMaxHeight(1f)
                    ) {
                        RankingList()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun SettingsList() {
        val settingsList: List<DataWithId<SettingsData>> by modelView.settingsList.data.observeAsState(
            listOf<DataWithId<SettingsData>>()
        )
        val selectedSettingsId: Int by modelView.selectedSettingsId.data.observeAsState(-1)

        Box(
            modifier = Modifier
                .background(Color(0xFFd8e2dc))
                .border(1.dp, Color.Black),
        ) {
            Column(
                Modifier.fillMaxSize()
            ) {
                Row() {
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
                        if (selectedSettingsId == itemId) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color(0xffa8dadc)
                                    )

                            ) {
                                SettingsDataItem(item)
                            }
                        } else {
                            Surface (
                                modifier = Modifier
                                    .border(1.dp, Color.Black),
                                shape = MaterialTheme.shapes.large,
                                onClick = { loadRankingForSettingsId(itemId) },
                            ) {
                                SettingsDataItem(item)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SettingsDataItem(settingDataWithId: DataWithId<SettingsData>) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            val settingsData = settingDataWithId.data
            val counts = settingsData.getCounts()
            val wins = winsCount?.get(settingDataWithId.id)?:0
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
                wins.toString(),
                Modifier.fillMaxWidth(1f),
                textAlign = TextAlign.End
            )
        }
    }

    @Composable
    fun RankingList() {
        val rankingList: List<RankingData> by modelView.rankingList.data.observeAsState(
            listOf<RankingData>())
        Box (
            modifier = Modifier.fillMaxSize().background(Color(0xff9a8c98)),
        ) {
            Column() {
                Row() {
                    Text(
                        "#",
                        Modifier.fillMaxWidth(0.2f),
                        textAlign = TextAlign.Start
                    )
                    Text(
                        "date",
                        Modifier.fillMaxWidth(0.5f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "seconds",
                        Modifier.fillMaxWidth(1f),
                        textAlign = TextAlign.End
                    )
                }
                LazyColumn {
                    items(rankingList.withIndex().toList()) { item ->
                        RankingDataItem(item)
                    }
                }
            }
        }
    }

    @Composable
    fun RankingDataItem(indexedRankingData: IndexedValue<RankingData>) {
        Box ()
        {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    indexedRankingData.index.toString(),
                    Modifier.fillMaxWidth(0.2f),
                    textAlign = TextAlign.Start
                )
                Text(
                    indexedRankingData.value.dateTime.replace('T', ' ').split('.')[0],
                    Modifier.fillMaxWidth(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    DateUtils.formatElapsedTime(indexedRankingData.value.elapsed / 1000),
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End

                )
            }
        }
    }

    private fun loadRankingForSettingsId(
        settingsId: Int
    ) {
        this.modelView.selectedSettingsId.onDataChanged(settingsId)
        rankingList?.let {
            val filteredRankingList = it.filter {
                it.settingId == settingsId
            }

            modelView.rankingList.onDataChanged(
                filteredRankingList
            )
        }
    }
}
