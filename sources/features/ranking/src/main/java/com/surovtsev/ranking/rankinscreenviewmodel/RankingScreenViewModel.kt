package com.surovtsev.ranking.rankinscreenviewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.surovtsev.core.mainactivity.MainActivity
import com.surovtsev.core.ranking.*
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.ranking.dagger.RankingComponent
import com.surovtsev.ranking.dagger.RankingComponentEntryPoint
import com.surovtsev.ranking.dagger.ToastMessageData
import com.surovtsev.ranking.rankinscreenviewmodel.helpers.RankingScreenEvents
import com.surovtsev.core.mainactivity.requestpermissionsresultreceiver.RequestPermissionsResult
import com.surovtsev.core.mainactivity.requestpermissionsresultreceiver.RequestPermissionsResultReceiver
import com.surovtsev.core.viewmodel.ViewModelCoroutineScopeHelper
import com.surovtsev.core.viewmodel.ViewModelCoroutineScopeHelperImpl
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineExceptionHandler
import org.jetbrains.anko.runOnUiThread
import javax.inject.Inject
import javax.inject.Provider


@HiltViewModel
class RankingScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    rankingComponentProvider: Provider<RankingComponent.Builder>,
    private val saveController: SaveController,
): ViewModel(),
    DefaultLifecycleObserver,
    RequestPermissionsResultReceiver,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl()
{
    private val settingsDao: SettingsDao
    private val rankingDao: RankingDao
    val rankingScreenEvents: RankingScreenEvents
    val rankingTableSortTypeData: RankingTableSortTypeData
    private val rankingListHelper: RankingListHelper
    val toastMessageData: ToastMessageData

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

        settingsDao =
            rankingComponentEntryPoint.settingsDao
        rankingDao =
            rankingComponentEntryPoint.rankingDao
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

    private fun loadData() {
        launchOnIOThread {
            val settingsList = settingsDao.getAll()
            val winsCountMap = rankingDao.getWinsCountMap()

            withUIContext {
                rankingScreenEvents.settingsListData.onDataChanged(
                    settingsList
                )
                rankingScreenEvents.winsCountMapData.onDataChanged(
                    winsCountMap
                )
            }

            settingsDao.getBySettingsData(
                saveController.loadSettingDataOrDefault()
            )?.let {
                loadRankingForSettingsId(it.id)
            }
        }
    }

    fun loadRankingForSettingsId(
        settingsId: Long
    ) {
        launchOnIOThread {
            val rankingListWithPlaces = rankingListHelper
                .createRankingListWithPlaces(
                    settingsId
                )

            withUIContext {
                rankingScreenEvents.filteredRankingList.onDataChanged(
                    rankingListWithPlaces
                )
                rankingScreenEvents.selectedSettingsIdData.onDataChanged(
                    settingsId
                )
            }
            prepareRankingListToDisplay()
        }
    }

    private suspend fun prepareRankingListToDisplay() {
        val filteredRankingList = rankingScreenEvents.filteredRankingList.data.value!!
        val currSortType = rankingTableSortTypeData.data.value!!

        val sortedData = rankingListHelper.sortData(
            filteredRankingList,
            currSortType
        )

        withUIContext {
            rankingScreenEvents.rankingListToDisplay.onDataChanged(
                sortedData
            )
        }
    }

    fun selectColumnToSortBy(
        selectedColumn: RankingColumn.SortableColumn
    ) {
        launchOnIOThread {
            val currSortType = rankingTableSortTypeData.data.value!!
            val tableSortType = if (currSortType.rankingColumn != selectedColumn) {
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

            withUIContext {
                rankingTableSortTypeData.onDataChanged(
                    tableSortType
                )
            }

            prepareRankingListToDisplay()
        }
    }

    fun triggerRequestingPermissions(mainActivity: MainActivity) {
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        mainActivity.requestPermissionsResultReceiver = this
        ActivityCompat.requestPermissions(
            mainActivity, permissions, requestWriteExternalStorageCode
        )
    }

    override fun handleRequestPermissionsResult(requestPermissionsResult: RequestPermissionsResult) {
        if (requestPermissionsResult.requestCode == requestWriteExternalStorageCode) {
            if (requestPermissionsResult.grantResults.let { it.isNotEmpty() && it[0] == PackageManager.PERMISSION_GRANTED }) {
                exportDBToCSVFiles()
            } else {
                toastMessageData.onDataChanged("Please, provide permission in order to export files")
            }
        }
    }

    private fun exportDBToCSVFiles() {
        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            val errorMessage = exception.message

            val toastMessage = "Error while exporting data: $errorMessage"

            context.runOnUiThread {
                toastMessageData.onDataChanged(toastMessage)
            }
        }

        launchWithExceptionHandler(
            ViewModelCoroutineScopeHelper.ioDispatcher,
            exceptionHandler
        ) {
//            val tablesInfo = listOf(
//                { rankingDBQueries.getTableStringsData() } to "rankingTable.csv",
//                { settingsDBQueries.getTableStringData() } to "settingsTable.csv"
//            )
//
//            val storeAction = { getTableStringDataAction:() -> String, fileName: String ->
//                val tableStringData = getTableStringDataAction()
//                ExternalFileWriter.writeFile(
//                    fileName,
//                    tableStringData
//                )
//            }
//
//            val jobs = tablesInfo.map { (sA, fN) ->
//                launch {
//                    storeAction(sA, fN)
//                }
//            }
//
//            jobs.forEach { it.join() }
//
//            val toastMessage = "Data is exported successfully"
//            withContext(ViewModelCoroutineScopeHelper.uiDispatcher) {
//                toastMessageData.onDataChanged(toastMessage)
//            }
        }
    }
}
