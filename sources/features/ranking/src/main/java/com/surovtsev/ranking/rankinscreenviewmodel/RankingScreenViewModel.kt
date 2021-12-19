package com.surovtsev.ranking.rankinscreenviewmodel

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.helpers.sorting.DefaultRankingTableSortParameters
import com.surovtsev.core.helpers.sorting.DefaultSortDirectionForSortableColumns
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.viewmodel.CommandProcessor
import com.surovtsev.core.viewmodel.ScreenCommandHandler
import com.surovtsev.core.viewmodel.TemplateScreenViewModel
import com.surovtsev.ranking.dagger.DaggerRankingComponent
import com.surovtsev.utils.timers.TimeSpan
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import logcat.logcat

typealias RankingScreenStateHolder = MutableLiveData<RankingScreenState>
typealias RankingScreenStateValue = LiveData<RankingScreenState>

typealias RankingScreenCommandHandler = ScreenCommandHandler<CommandFromRankingScreen>

class RankingScreenViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
    @Assisted appComponentEntryPoint: AppComponentEntryPoint,
): TemplateScreenViewModel<CommandFromRankingScreen, RankingScreenData>(
        CommandFromRankingScreen.LoadData,
        RankingScreenData.NoData
    ),
    DefaultLifecycleObserver
//    RequestPermissionsResultReceiver,
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<RankingScreenViewModel>

    private val settingsDao: SettingsDao
    private val rankingDao: RankingDao

    private val rankingListHelper: RankingListHelper

    override val stateHolder: RankingScreenStateHolder
    override val stateValue: RankingScreenStateValue

    private val timeSpan: TimeSpan

    private val saveController: SaveController

    companion object {
        const val MINIMAL_UI_ACTION_DELAY = 1000L
    }

    init {
        val rankingComponent = DaggerRankingComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .build()

        settingsDao =
            rankingComponent.settingsDao
        rankingDao =
            rankingComponent.rankingDao
        rankingListHelper =
            rankingComponent.rankingListHelper

        stateHolder =
            rankingComponent.rankingScreenStateHolder
        stateValue =
            rankingComponent.rankingScreenStateValue

        timeSpan =
            rankingComponent.timeSpan

        saveController =
            rankingComponent.saveController

        timeSpan.flush()
    }

    override suspend fun getCommandProcessor(command: CommandFromRankingScreen): CommandProcessor? {
        return when (command) {
            is CommandFromRankingScreen.LoadData            -> ::loadData
            is CommandFromRankingScreen.FilterList          -> suspend { filterList(command.selectedSettingsId) }
            is CommandFromRankingScreen.SortListWithNoDelay -> suspend { sortList(command.rankingTableSortParameters, false) }
            is CommandFromRankingScreen.SortList            -> suspend { sortList(command.rankingTableSortParameters, true) }
            is CommandFromRankingScreen.CloseError          -> ::closeError
            else                                            -> null
        }
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

        publishIdleState(
            settingsListIsLoaded
        )

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

        val rankingScreenData = stateHolder.value?.screenData

        if (rankingScreenData == null || rankingScreenData !is RankingScreenData.SettingsListIsLoaded) {
            publishErrorState(
                "error while filtering ranking list"
            )
        } else {
            // Do not set state to IDLE in order to avoid blinking loading ui attributes.
            publishIdleState(
                RankingScreenData.RankingListIsPrepared(
                    rankingScreenData,
                    selectedSettingsId,
                    rankingListWithPlaces
                )
            )
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
        val rankingScreenData = stateHolder.value?.screenData

        if (rankingScreenData == null || rankingScreenData !is RankingScreenData.RankingListIsPrepared) {
            publishErrorState(
                "error while sorting ranking list"
            )

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

        publishIdleState(
            RankingScreenData.RankingListIsSorted(
                rankingScreenData,
                rankingTableSortParameters,
                sortedData,
                directionOfSortableColumns
            )
        )
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
