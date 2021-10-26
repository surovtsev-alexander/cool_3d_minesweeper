package com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.RankingDBQueries
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.database.queriesHelpers.SettingsDBQueries
import com.surovtsev.cool3dminesweeper.dagger.app.ToastMessageData
import com.surovtsev.cool3dminesweeper.dagger.app.ranking.RankingComponent
import com.surovtsev.cool3dminesweeper.dagger.app.ranking.RankingComponentEntryPoint
import com.surovtsev.cool3dminesweeper.presentation.MainActivity
import com.surovtsev.cool3dminesweeper.utils.androidview.requestpermissionsresultreceiver.RequestPermissionsResult
import com.surovtsev.cool3dminesweeper.utils.androidview.requestpermissionsresultreceiver.RequestPermissionsResultReceiver
import com.surovtsev.cool3dminesweeper.utils.externalfilewriter.ExternalFileWriter
import com.surovtsev.cool3dminesweeper.viewmodels.rankingactivityviewmodel.helpers.*
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Provider


@HiltViewModel
class RankingScreenViewModel @Inject constructor(
    rankingComponentProvider: Provider<RankingComponent.Builder>,
    private val saveController: SaveController,
): ViewModel(), DefaultLifecycleObserver, CoroutineScope, RequestPermissionsResultReceiver {

    private val settingsDBQueries: SettingsDBQueries
    private val rankingDBQueries: RankingDBQueries
    val rankingScreenEvents: RankingScreenEvents
    val rankingTableSortTypeData: RankingTableSortTypeData
    private val rankingListHelper: RankingListHelper
    val toastMessageData: ToastMessageData

    private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val coroutineJob = Job()
    override val coroutineContext = uiDispatcher + coroutineJob

    companion object {
        const val requestWriteExternalStorageCode = 100
    }

    init {
        val rankingComponent = rankingComponentProvider
            .get()
            .build()
        val rankingComponentEntryPoint =
            EntryPoints.get(
                rankingComponent,
                RankingComponentEntryPoint::class.java
            )

        settingsDBQueries =
            rankingComponentEntryPoint.settingsDBQueries
        rankingDBQueries =
            rankingComponentEntryPoint.rankingDBQueries
        rankingScreenEvents =
            rankingComponentEntryPoint.rankingScreenEvents
        rankingTableSortTypeData =
            rankingComponentEntryPoint.rankingTableSortTypeData
        rankingListHelper =
            rankingComponentEntryPoint.rankingListHelper
        toastMessageData =
            rankingComponentEntryPoint.toastMessageData
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)

        loadData()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        cleanup()

        super.onDestroy(owner)
    }

    private fun cleanup() {
        coroutineContext.cancel()
    }

    private fun loadData() {
        rankingScreenEvents.settingsDataWithIdsListData.onDataChanged(
            settingsDBQueries.getSettingsList()
        )

        rankingScreenEvents.rankingDataListData.onDataChanged(
            let {
                val res = rankingListHelper.loadData()
                rankingScreenEvents.winsCount.onDataChanged(
                    res.map{ it.settingId }.groupingBy { it }.eachCount()
                )
                res
            }
        )

        settingsDBQueries.getId(
            saveController.loadSettingDataOrDefault()
        )?.let {
            loadRankingForSettingsId(it)
        }
    }

    fun loadRankingForSettingsId(
        settingsId: Int
    ) {
        rankingScreenEvents.filteredRankingList.onDataChanged(
            rankingListHelper.filterData(
                rankingScreenEvents.rankingDataListData.data.value!!,
                settingsId
            )
        )
        rankingScreenEvents.selectedSettingsId.onDataChanged(settingsId)
        prepareRankingListToDisplay()

    }

    private fun prepareRankingListToDisplay() {
        val currSortType = rankingTableSortTypeData.data.value!!
        val filteredRankingList = rankingScreenEvents.filteredRankingList.data.value!!

        rankingScreenEvents.rankingListToDisplay.onDataChanged(
            rankingListHelper.sortData(
                filteredRankingList,
                currSortType
            )
        )
    }

    fun selectColumnToSortBy(
        selectedColumn: RankingColumn.SortableColumn
    ) {
        val currSortType = rankingTableSortTypeData.data.value!!
        rankingTableSortTypeData.onDataChanged(
            if (currSortType.rankingColumn != selectedColumn) {
                RankingTableSortType(
                    selectedColumn,
                    SortDirection.Descending
                )
            } else {
                RankingTableSortType(
                    selectedColumn,
                    nextSortType(currSortType.sortDirection)
                )
            }
        )

        prepareRankingListToDisplay()
    }

    fun triggerRequestingPermissions(mainActivity: MainActivity) {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        mainActivity.requestPermissionsResultReceiver = this
        ActivityCompat.requestPermissions(
            mainActivity, permissions, requestWriteExternalStorageCode
        )
    }

    override fun handleRequestPermissionsResult(rPR: RequestPermissionsResult) {
        if (rPR.requestCode == requestWriteExternalStorageCode) {
            if (rPR.grantResults.isNotEmpty() && rPR.grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportDBToCSVFiles()
            } else {
                toastMessageData.onDataChanged("Please, provide permission in order to export files")
            }
        }
    }

    private fun exportDBToCSVFiles() {
        launch {
            var errorMessage: String? = null

            val exceptionHandler = CoroutineExceptionHandler { _, exception ->
                errorMessage = exception.message
            }
            launch(ioDispatcher + exceptionHandler) {
                val rankingTableStoringJob = launch {
                    try {
                        val rankingTableStringData = rankingDBQueries.getTableStringsData()
                        ExternalFileWriter.writeFile(
                            "rankingTable.csv",
                            rankingTableStringData
                        )
                    } catch (e: Exception) {
                        errorMessage = e.message
                    }
                }

                val settingsTableStoringJob = launch {
                    try {
                        val settingsTableStringData = settingsDBQueries.getTableStringData()
                        ExternalFileWriter.writeFile(
                            "settingsTable.csv",
                            settingsTableStringData
                        )
                    } catch (e: Exception) {
                        errorMessage = e.message
                    }
                }
                rankingTableStoringJob.join()
                settingsTableStoringJob.join()

            }.join()

            val toastMessage = if (errorMessage == null) {
                "Data is exported successfully"
            } else {
                "Error while exporting data: $errorMessage"
            }

            withContext(uiDispatcher) {
                toastMessageData.onDataChanged(toastMessage)
            }
        }
    }
}
