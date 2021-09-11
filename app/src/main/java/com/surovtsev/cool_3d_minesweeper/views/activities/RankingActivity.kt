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
import com.surovtsev.cool_3d_minesweeper.views.theme.Test_composeTheme
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.surovtsev.cool_3d_minesweeper.model_views.RankingActivityModelView
import com.surovtsev.cool_3d_minesweeper.models.game.database.DataWithId
import com.surovtsev.cool_3d_minesweeper.models.game.database.RankingData
import com.surovtsev.cool_3d_minesweeper.models.game.database.SettingsData
import com.surovtsev.cool_3d_minesweeper.views.theme.DeepGray
import com.surovtsev.cool_3d_minesweeper.views.theme.GrayBackground
import com.surovtsev.cool_3d_minesweeper.views.theme.LightBlue


class RankingActivity: ComponentActivity() {
    private val modelView = RankingActivityModelView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RankingControls(modelView)
        }

        modelView.loadData()
    }
}

@Composable
fun RankingControls(modelView: RankingActivityModelView) {
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
                SettingsList(modelView)
            }
            Row(
                modifier = Modifier.fillMaxHeight(1f)
            ) {
                RankingList(modelView)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SettingsList(modelView: RankingActivityModelView) {
    val settingsList: List<DataWithId<SettingsData>> by modelView.settingsList.data.observeAsState(
        listOf()
    )
    val selectedSettingsId: Int by modelView.selectedSettingsId.data.observeAsState(-1)

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
                    if (selectedSettingsId == itemId) {
                        Box(
                            modifier = Modifier
                                .background(
                                    LightBlue
                                )

                        ) {
                            SettingsDataItem(modelView, item)
                        }
                    } else {
                        Surface (
                            shape = MaterialTheme.shapes.large,
                            onClick = { modelView.loadRankingForSettingsId(itemId) },
                        ) {
                            SettingsDataItem(modelView, item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsDataItem(
    modelView: RankingActivityModelView,
    settingDataWithId: DataWithId<SettingsData>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val settingsData = settingDataWithId.data
        val counts = settingsData.getCounts()
        val wins = modelView.winsCount?.get(settingDataWithId.id)?:0
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
fun RankingList(modelView: RankingActivityModelView) {
    val filteredRankingList: List<RankingData> by modelView.filteredRankingList.data.observeAsState(
        listOf<RankingData>())
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(DeepGray)
            .border(1.dp, Color.Black)
            .padding(horizontal = 1.dp),
    ) {
        Column {
            Row {
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
                items(filteredRankingList.withIndex().toList()) { item ->
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
                (indexedRankingData.index + 1).toString(),
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

