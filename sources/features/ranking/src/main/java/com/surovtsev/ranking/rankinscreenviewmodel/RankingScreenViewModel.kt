package com.surovtsev.ranking.rankinscreenviewmodel

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surovtsev.core.ranking.*
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.viewmodel.ScreenCommandsHandler
import com.surovtsev.core.viewmodel.ViewModelCoroutineScopeHelper
import com.surovtsev.core.viewmodel.ViewModelCoroutineScopeHelperImpl
import com.surovtsev.ranking.dagger.RankingComponent
import com.surovtsev.ranking.dagger.RankingComponentEntryPoint
import com.surovtsev.ranking.dagger.ToastMessageData
import com.surovtsev.ranking.rankinscreenviewmodel.helpers.RankingScreenEvents
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import logcat.logcat
import javax.inject.Inject
import javax.inject.Provider

typealias RankingScreenStateHolder = MutableLiveData<RankingScreenState>
typealias RankingScreenStateValue = LiveData<RankingScreenState>

typealias RankingScreenCommandsHandler = ScreenCommandsHandler<CommandFromRankingScreen>

@HiltViewModel
class RankingScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    rankingComponentProvider: Provider<RankingComponent.Builder>,
    private val saveController: SaveController,
): ViewModel(),
    RankingScreenCommandsHandler,
    DefaultLifecycleObserver,
//    RequestPermissionsResultReceiver,
    ViewModelCoroutineScopeHelper by ViewModelCoroutineScopeHelperImpl()
{
    private val settingsDao: SettingsDao
    private val rankingDao: RankingDao
    val rankingScreenEvents: RankingScreenEvents
    val rankingTableSortTypeData: RankingTableSortTypeData
    private val rankingListHelper: RankingListHelper
    val toastMessageData: ToastMessageData

    private val rankingScreenStateHolder: RankingScreenStateHolder
    val rankingScreenStateValue: RankingScreenStateValue

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
        rankingScreenStateHolder =
            rankingComponentEntryPoint.rankingScreenStateHolder
        rankingScreenStateValue =
            rankingComponentEntryPoint.rankingScreenStateValue
    }

    override fun handleCommand(event: CommandFromRankingScreen) {
        launchOnIOThread {

            setLoadingState()

            when (event) {
                is CommandFromRankingScreen.LoadData    -> loadData()
                is CommandFromRankingScreen.FilterList  -> filterList(event.selectedSettingsId)
                is CommandFromRankingScreen.SortList    -> sortList(event.rankingTableSortType)
                is CommandFromRankingScreen.CloseError  -> closeError()
            }
        }
    }

    private suspend fun setLoadingState() {
        withUIContext {
            rankingScreenStateHolder.value = RankingScreenState.Loading(
                getCurrentRankingScreenDataOrDefault()
            )
        }
    }

    private fun getCurrentRankingScreenDataOrDefault(): RankingScreenData {
        return rankingScreenStateHolder.value?.rankingScreenData ?: RankingScreenData.NoData
    }

    private suspend fun loadData() {
        val settingsList = settingsDao.getAll()
        val winsCountMap = rankingDao.getWinsCountMap()

        withUIContext {
            rankingScreenStateHolder.value = RankingScreenState.Idle(
                RankingScreenData.SettingsListIsLoaded(
                    settingsList,
                    winsCountMap
                )
            )
        }

        settingsDao.getBySettingsData(
            saveController.loadSettingDataOrDefault()
        )?.let {
            return handleCommand(
                CommandFromRankingScreen
                    .FilterList(it.id)
            )
        }
    }

    private suspend fun filterList(
        selectedSettingsId: Long
    ) {
        val rankingListWithPlaces = rankingListHelper
            .createRankingListWithPlaces(
                selectedSettingsId
            )

        withUIContext {
            val rankingScreenData = rankingScreenStateHolder.value?.rankingScreenData
            rankingScreenStateHolder.value = if (rankingScreenData == null || rankingScreenData !is RankingScreenData.SettingsListIsLoaded) {
                RankingScreenState.Error(
                    getCurrentRankingScreenDataOrDefault(),
                    "error while filtering ranking list"
                )
            } else {
                RankingScreenState.Idle(
                    RankingScreenData.RankingListIsPrepared(
                        rankingScreenData,
                        selectedSettingsId,
                        rankingListWithPlaces
                    )
                )
            }
        }

        return handleCommand(
            CommandFromRankingScreen.SortList(
                DefaultRankingTableSortType
            )
        )
    }

    private suspend fun sortList(
        rankingTableSortType: RankingTableSortType
    ) {
        val rankingScreenData = rankingScreenStateHolder.value?.rankingScreenData

        if (rankingScreenData == null || rankingScreenData !is RankingScreenData.RankingListIsPrepared) {
            withUIContext {
                rankingScreenStateHolder.value = RankingScreenState.Error(
                    getCurrentRankingScreenDataOrDefault(),
                    "error while sorting ranking list"
                )
            }

            return
        }

        val filteredRankingList = rankingScreenData.rankingListWithPlaces

        logcat { "rankingTableSortType: $rankingTableSortType" }

        val sortedData = rankingListHelper.sortData(
            filteredRankingList,
            rankingTableSortType
        )

        val directionOfSortableColumns =
            (if (rankingScreenData is RankingScreenData.RankingListIsSorted) {
                rankingScreenData.directionOfSortableColumns
            } else {
                DefaultSortDirectionForSortableColumns
            }).map { (k, v) ->
                k to if (k == rankingTableSortType.rankingColumn) {
                    rankingTableSortType.sortDirection
                } else {
                    v
                }
            }.toMap()

        withUIContext {
            rankingScreenStateHolder.value = RankingScreenState.Idle(
                RankingScreenData.RankingListIsSorted(
                    rankingScreenData,
                    rankingTableSortType,
                    sortedData,
                    directionOfSortableColumns
                )
            )
        }
    }

    private suspend fun closeError() {
        withUIContext {
            rankingScreenStateHolder.value =
                RankingScreenState.Idle(
                    getCurrentRankingScreenDataOrDefault()
                )
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

//    fun triggerRequestingPermissions(mainActivity: MainActivity) {
//        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//
//        mainActivity.requestPermissionsResultReceiver = this
//        ActivityCompat.requestPermissions(
//            mainActivity, permissions, requestWriteExternalStorageCode
//        )
//    }

//    override fun handleRequestPermissionsResult(requestPermissionsResult: RequestPermissionsResult) {
//        if (requestPermissionsResult.requestCode == requestWriteExternalStorageCode) {
//            if (requestPermissionsResult.grantResults.let { it.isNotEmpty() && it[0] == PackageManager.PERMISSION_GRANTED }) {
//                exportDBToCSVFiles()
//            } else {
//                toastMessageData.onDataChanged("Please, provide permission in order to export files")
//            }
//        }
//    }

//    private fun exportDBToCSVFiles() {
//        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
//            val errorMessage = exception.message
//
//            val toastMessage = "Error while exporting data: $errorMessage"
//
//            context.runOnUiThread {
//                toastMessageData.onDataChanged(toastMessage)
//            }
//        }
//
//        launchWithExceptionHandler(
//            ViewModelCoroutineScopeHelper.ioDispatcher,
//            exceptionHandler
//        ) {
////            val tablesInfo = listOf(
////                { rankingDBQueries.getTableStringsData() } to "rankingTable.csv",
////                { settingsDBQueries.getTableStringData() } to "settingsTable.csv"
////            )
////
////            val storeAction = { getTableStringDataAction:() -> String, fileName: String ->
////                val tableStringData = getTableStringDataAction()
////                ExternalFileWriter.writeFile(
////                    fileName,
////                    tableStringData
////                )
////            }
////
////            val jobs = tablesInfo.map { (sA, fN) ->
////                launch {
////                    storeAction(sA, fN)
////                }
////            }
////
////            jobs.forEach { it.join() }
////
////            val toastMessage = "Data is exported successfully"
////            withContext(ViewModelCoroutineScopeHelper.uiDispatcher) {
////                toastMessageData.onDataChanged(toastMessage)
////            }
//        }
//    }
}
