package com.surovtsev.cool_3d_minesweeper.views.activities

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.surovtsev.cool_3d_minesweeper.views.theme.Test_composeTheme

import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.database.*
import com.surovtsev.cool_3d_minesweeper.utils.live_data.MyLiveData
import java.lang.StringBuilder

class RankingActivityV2: ComponentActivity() {

    private class ModelView() {
        val settingsList = MyLiveData<List<DataWithId<SettingsData>>>(
            listOf<DataWithId<SettingsData>>()
        )
        val rankingList = MyLiveData<List<RankingData>>(
            listOf<RankingData>()
        )
    }

    private val modelView = ModelView()
    private val dbHelper = DBHelper(this)
    private val settingsDBQueries = SettingsDBQueries(dbHelper)
    private val rankingDBQueries = RankingDBQueries(dbHelper)

    private var rankingList: List<RankingData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RankingControls()
        }

        modelView.settingsList.onDataChanged(
            settingsDBQueries.getSettingsList()
        )

        rankingList = rankingDBQueries.getRankingList()
    }

    @Composable
    fun RankingControls() {
        Test_composeTheme {
            Surface(color = MaterialTheme.colors.background) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                ) {
                    SettingsList()
                    RankingList()
                }
            }
        }
    }

    @Composable
    fun SettingsList() {
        val settingsList: List<DataWithId<SettingsData>> by modelView.settingsList.data.observeAsState(
            listOf<DataWithId<SettingsData>>()
        )
        LazyColumn {
            items(settingsList) { item ->
                SettingsDataItem(item)
            }
        }
    }

    @Composable
    fun SettingsDataItem(settingDataWithId: DataWithId<SettingsData>) {
        Button (onClick = {
            loadRankingForSettingsId(settingDataWithId.id)
        }) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                val settingsData = settingDataWithId.data
                val counts = settingsData.getCounts()
                Text(
                    counts.toString(),
                    Modifier.fillMaxWidth(0.5f),
                    textAlign = TextAlign.Center
                )
                Text(
                    settingsData.bombsPercentage.toString(),
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }

        }
    }

    @Composable
    fun RankingList() {
        val rankingList: List<RankingData> by modelView.rankingList.data.observeAsState(
            listOf<RankingData>())
        Test_composeTheme {
            Surface(color = MaterialTheme.colors.background) {
                LazyColumn {
                    items(rankingList) { item ->
                        RankingDataItem(item)
                    }
                }
            }
        }
    }

    @Composable
    fun RankingDataItem(rankingData: RankingData) {
        Button (onClick = {}) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    rankingData.dateTime.replace('T', ' ').split('.')[0],
                    Modifier.fillMaxWidth(0.5f),
                    textAlign = TextAlign.End
                )
                Text(
                    DateUtils.formatElapsedTime(rankingData.elapsed / 1000),
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End

                )
            }
        }
    }

    private fun loadRankingForSettingsId(
        settingsId: Int
    ) {

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
