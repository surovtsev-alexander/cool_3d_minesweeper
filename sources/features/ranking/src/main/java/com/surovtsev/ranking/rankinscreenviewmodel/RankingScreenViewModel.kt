package com.surovtsev.ranking.rankinscreenviewmodel

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.helpers.sorting.DefaultRankingTableSortParameters
import com.surovtsev.core.helpers.sorting.DefaultSortDirectionForSortableColumns
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.viewmodel.ScreenCommandsHandler
import com.surovtsev.ranking.dagger.RankingComponent
import com.surovtsev.ranking.dagger.RankingComponentEntryPoint
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelper
import com.surovtsev.utils.coroutines.ViewModelCoroutineScopeHelperImpl
import com.surovtsev.utils.timers.TimeSpan
import com.surovtsev.utils.viewmodel.ScreenState
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import logcat.logcat
import javax.inject.Inject
import javax.inject.Provider

typealias RankingScreenStateHolder = MutableLiveData<RankingScreenState>
typealias RankingScreenStateValue = LiveData<RankingScreenState>

typealias RankingScreenCommandsHandler = ScreenCommandsHandler<CommandFromRankingScreen>

@HiltViewModel
class RankingScreenViewModel @Inject constructor(
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

    private val rankingListHelper: RankingListHelper

    private val rankingScreenStateHolder: RankingScreenStateHolder
    val rankingScreenStateValue: RankingScreenStateValue

    private val timeSpan: TimeSpan

    companion object {
        const val MINIMAL_UI_ACTION_DELAY = 1000L
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
        rankingListHelper =
            rankingComponentEntryPoint.rankingListHelper

        rankingScreenStateHolder =
            rankingComponentEntryPoint.rankingScreenStateHolder
        rankingScreenStateValue =
            rankingComponentEntryPoint.rankingScreenStateValue

        timeSpan =
            rankingComponentEntryPoint.timeSpan

        timeSpan.flush()
    }

    override fun handleCommand(event: CommandFromRankingScreen) {
        launchOnIOThread {
            setLoadingState()

            when (event) {
                is CommandFromRankingScreen.LoadData            -> loadData()
                is CommandFromRankingScreen.FilterList          -> filterList(event.selectedSettingsId)
                is CommandFromRankingScreen.SortListWithNoDelay -> sortList(event.rankingTableSortParameters, false)
                is CommandFromRankingScreen.SortList            -> sortList(event.rankingTableSortParameters, true)
                is CommandFromRankingScreen.CloseError          -> closeError()
            }
        }
    }

    private suspend fun setLoadingState() {
        withUIContext {
            rankingScreenStateHolder.value = ScreenState.Loading(
                getCurrentRankingScreenDataOrDefault()
            )
        }
    }

    private fun getCurrentRankingScreenDataOrDefault(): RankingScreenData {
        return rankingScreenStateHolder.value?.rankingScreenData ?: RankingScreenData.NoData
    }

    private suspend fun loadData() {
        val settingsListIsLoaded = doActionWithDelayUpToDefaultMinimal {
            val settingsList = settingsDao.getAll()
            val winsCountMap = rankingDao.getWinsCountMap()
            RankingScreenData.SettingsListIsLoaded(
                settingsList,
                winsCountMap
            )
        }

        withUIContext {
            rankingScreenStateHolder.value = ScreenState.Idle(
                settingsListIsLoaded
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
        val rankingListWithPlaces =
            rankingListHelper
                .createRankingListWithPlaces(
                    selectedSettingsId
                )

        withUIContext {
            val rankingScreenData = rankingScreenStateHolder.value?.rankingScreenData
            rankingScreenStateHolder.value = if (rankingScreenData == null || rankingScreenData !is RankingScreenData.SettingsListIsLoaded) {
                ScreenState.Error(
                    getCurrentRankingScreenDataOrDefault(),
                    "error while filtering ranking list"
                )
            } else {
                // Do not set state to IDLE in order to avoid blinking loading ui attributes.
                ScreenState.Loading(
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
                DefaultRankingTableSortParameters
            )
        )
    }

    private suspend fun sortList(
        rankingTableSortParameters: RankingTableSortParameters,
        doDelay: Boolean
    ) {
        val rankingScreenData = rankingScreenStateHolder.value?.rankingScreenData

        if (rankingScreenData == null || rankingScreenData !is RankingScreenData.RankingListIsPrepared) {
            withUIContext {
                rankingScreenStateHolder.value = ScreenState.Error(
                    getCurrentRankingScreenDataOrDefault(),
                    "error while sorting ranking list"
                )
            }

            return
        }

        val filteredRankingList = rankingScreenData.rankingListWithPlaces

        logcat { "rankingTableSortType: $rankingTableSortParameters" }

        val sortingAction = {
            rankingListHelper.sortData(
                filteredRankingList,
                rankingTableSortParameters
            )
        }

        val sortedData = if (doDelay) {
            doActionWithDelayUpToDefaultMinimal(sortingAction)
        } else {
            sortingAction.invoke()
        }

        val directionOfSortableColumns =
            (if (rankingScreenData is RankingScreenData.RankingListIsSorted) {
                rankingScreenData.directionOfSortableColumns
            } else {
                DefaultSortDirectionForSortableColumns
            }).map { (k, v) ->
                k to if (k == rankingTableSortParameters.rankingTableColumn) {
                    rankingTableSortParameters.sortDirection
                } else {
                    v
                }
            }.toMap()

        withUIContext {
            rankingScreenStateHolder.value = ScreenState.Idle(
                RankingScreenData.RankingListIsSorted(
                    rankingScreenData,
                    rankingTableSortParameters,
                    sortedData,
                    directionOfSortableColumns
                )
            )
        }
    }

    private suspend fun closeError() {
        withUIContext {
            rankingScreenStateHolder.value =
                ScreenState.Idle(
                    getCurrentRankingScreenDataOrDefault()
                )
        }
    }

    private suspend fun<T> doActionWithDelayUpToDefaultMinimal(
        block: () -> T
    ): T {
        timeSpan.turnOn()

        val res = block.invoke()

        timeSpan.turnOff()
        val timeToDelay = MINIMAL_UI_ACTION_DELAY - timeSpan.getElapsed()

        if (timeToDelay > 0) {
            delay(timeToDelay)
        }

        return res
    }


//    companion object {
//        const val requestWriteExternalStorageCode = 100
//    }
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
