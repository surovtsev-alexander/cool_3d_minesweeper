package com.surovtsev.ranking.rankinscreenviewmodel

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.surovtsev.core.ranking.DefaultRankingTableSortType
import com.surovtsev.core.ranking.DefaultSortDirectionForSortableColumns
import com.surovtsev.core.ranking.RankingListHelper
import com.surovtsev.core.ranking.RankingTableSortType
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.viewmodel.ScreenCommandsHandler
import com.surovtsev.core.viewmodel.ViewModelCoroutineScopeHelper
import com.surovtsev.core.viewmodel.ViewModelCoroutineScopeHelperImpl
import com.surovtsev.ranking.dagger.RankingComponent
import com.surovtsev.ranking.dagger.RankingComponentEntryPoint
import com.surovtsev.utils.timers.TimeSpan
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
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
                is CommandFromRankingScreen.SortListWithNoDelay -> sortList(event.rankingTableSortType, false)
                is CommandFromRankingScreen.SortList            -> sortList(event.rankingTableSortType, true)
                is CommandFromRankingScreen.CloseError          -> closeError()
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
        val settingsListIsLoaded = doActionWithDelayUpToDefaultMinimal {
            val settingsList = settingsDao.getAll()
            val winsCountMap = rankingDao.getWinsCountMap()
            RankingScreenData.SettingsListIsLoaded(
                settingsList,
                winsCountMap
            )
        }

        withUIContext {
            rankingScreenStateHolder.value = RankingScreenState.Idle(
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
                RankingScreenState.Error(
                    getCurrentRankingScreenDataOrDefault(),
                    "error while filtering ranking list"
                )
            } else {
                // Do not set state to IDLE in order to avoid blinking loading ui attributes.
                RankingScreenState.Loading(
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
        rankingTableSortType: RankingTableSortType,
        doDelay: Boolean
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

        val sortingAction = {
            rankingListHelper.sortData(
                filteredRankingList,
                rankingTableSortType
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
