package com.surovtsev.rankingscreen.rankinscreenviewmodel

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.helpers.sorting.DefaultRankingTableSortParameters
import com.surovtsev.core.helpers.sorting.DefaultSortDirectionForSortableColumns
import com.surovtsev.core.helpers.sorting.RankingTableSortParameters
import com.surovtsev.core.viewmodel.*
import com.surovtsev.rankingscreen.dagger.DaggerRankingComponent
import com.surovtsev.rankingscreen.dagger.RankingComponent
import com.surovtsev.restartablecoroutinescope.dagger.DaggerRestartableCoroutineScopeComponent
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.subscriptionsholder.helpers.factory.SubscriptionsHolderComponentFactoryHolderImp
import com.surovtsev.timespan.dagger.DaggerTimeSpanComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent
import com.surovtsev.utils.timers.async.AsyncTimeSpan
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import logcat.logcat

typealias RankingScreenStateFlow = ScreenStateFlow<RankingScreenData>

typealias RankingScreenEventHandler = EventHandler<EventToRankingScreenViewModel>

typealias RankingScreenErrorDialogPlacer = ErrorDialogPlacer<
        EventToRankingScreenViewModel, RankingScreenData>

class RankingScreenViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
    @Assisted private val appComponentEntryPoint: AppComponentEntryPoint,
):
    TemplateScreenViewModel<EventToRankingScreenViewModel, RankingScreenData>(
        EventToRankingScreenViewModel.MandatoryEvents,
        RankingScreenData.NoData,
        RankingScreenInitialState,
    )
//    RequestPermissionsResultReceiver,
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<RankingScreenViewModel>

    var timeSpanComponent: TimeSpanComponent? = null
    var rankingComponent: RankingComponent? = null

    companion object {
        const val MINIMAL_UI_ACTION_DELAY = 3000L
    }

    private var restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent? = null

    object ErrorMessages {
        val errorWhileFilteringRankingListFactory = { code: Int -> "error (code: $code) while filtering ranking list" }
        val errorWhileSortingListFactory = { code: Int -> "error (code: $code) while sorting list" }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

        restartableCoroutineScopeComponent?.subscriberImp?.onStop()
    }

    override suspend fun getEventProcessor(event: EventToRankingScreenViewModel): EventProcessor? {
        return when (event) {
            is EventToRankingScreenViewModel.HandleScreenLeaving     -> suspend { handleScreenLeaving(event.owner) }
            is EventToRankingScreenViewModel.LoadData                -> ::loadData
            is EventToRankingScreenViewModel.FilterList              -> suspend { filterList(event.selectedSettingsId) }
            is EventToRankingScreenViewModel.SortListWithNoDelay     -> suspend { sortList(event.rankingTableSortParameters, false) }
            is EventToRankingScreenViewModel.SortList                -> suspend { sortList(event.rankingTableSortParameters, true) }
            is EventToRankingScreenViewModel.CloseError              -> ::closeError
            else                                                -> null
        }
    }

    private suspend fun loadData() {
        val currRestartableCoroutineScopeComponent: RestartableCoroutineScopeComponent
        restartableCoroutineScopeComponent.let {
            currRestartableCoroutineScopeComponent =
                it?.also {
                    it.subscriberImp.restart()
                } ?: DaggerRestartableCoroutineScopeComponent.create()
                    .also {
                        restartableCoroutineScopeComponent = it
                    }
        }

        val currTimeSpanComponent: TimeSpanComponent

        timeSpanComponent.let {
            currTimeSpanComponent =
                it
                    ?: DaggerTimeSpanComponent
                        .builder()
                        .subscriptionsHolderEntryPoint(
                            SubscriptionsHolderComponentFactoryHolderImp.create(
                                currRestartableCoroutineScopeComponent,
                                "RankingScreenViewModel:TimeSpanComponent"
                            )
                        )
                        .build()
                        .apply {
                            timeSpanComponent = this
                        }
        }

        val currRankingComponent: RankingComponent

        rankingComponent.let {
            currRankingComponent =
                it ?: DaggerRankingComponent
                    .builder()
                    .appComponentEntryPoint(appComponentEntryPoint)
                    .timeSpanComponentEntryPoint(currTimeSpanComponent)
                    .build()
                    .apply {
                        rankingComponent = this
                    }
        }

        val settingsDao = currRankingComponent.settingsDao
        val rankingDao = currRankingComponent.rankingDao
        val saveController = currRankingComponent.saveController

        val settingsListIsLoaded = doActionWithDelayUpToDefaultMinimal(currTimeSpanComponent.asyncTimeSpan) {
            val settingsList = settingsDao.getAll()
            val winsCountMap = rankingDao.getWinsCountMap()
            RankingScreenData.SettingsListIsLoaded(
                settingsList,
                winsCountMap
            )
        }

        currTimeSpanComponent
            .asyncTimeSpan
            .flush()

        fsmStateHolder.publishIdleState(
            settingsListIsLoaded
        )

        settingsDao.getBySettingsData(
            saveController.loadSettingDataOrDefault()
        )?.let {
            return handleEvent(
                EventToRankingScreenViewModel.FilterList(it.id)
            )
        }
    }

    private suspend fun filterList(
        selectedSettingsId: Long
    ) {
        val currRankingComponent = rankingComponent

        if (currRankingComponent == null) {
            fsmStateHolder.publishErrorState(
                ErrorMessages.errorWhileFilteringRankingListFactory(1)
            )
            return
        }

        val rankingListWithPlaces =
            currRankingComponent.rankingListHelper
                .createRankingListWithPlaces(
                    selectedSettingsId
                )

        val rankingScreenData = fsmStateHolder.getCurrentData()

        if (rankingScreenData !is RankingScreenData.SettingsListIsLoaded) {
            fsmStateHolder.publishErrorState(
                ErrorMessages.errorWhileFilteringRankingListFactory(2)
            )
        } else {
            // Do not set state to IDLE in order to avoid blinking loading ui attributes.
            fsmStateHolder.publishIdleState(
                RankingScreenData.RankingListIsPrepared(
                    rankingScreenData,
                    selectedSettingsId,
                    rankingListWithPlaces
                )
            )
        }


        return handleEvent(
            EventToRankingScreenViewModel.SortList(
                DefaultRankingTableSortParameters
            )
        )
    }

    private suspend fun sortList(
        rankingTableSortParameters: RankingTableSortParameters,
        doDelay: Boolean
    ) {
        val currTimeSpanComponent = timeSpanComponent

        if (currTimeSpanComponent == null) {
            fsmStateHolder.publishErrorState(
                ErrorMessages.errorWhileSortingListFactory(1)
            )

            return
        }

        val currRankingComponent = rankingComponent

        if (currRankingComponent == null) {
            fsmStateHolder.publishErrorState(
                ErrorMessages.errorWhileSortingListFactory(2)
            )

            return
        }

        val rankingScreenData = fsmStateHolder.state.value.data

        if (rankingScreenData !is RankingScreenData.RankingListIsPrepared) {
            fsmStateHolder.publishErrorState(
                ErrorMessages.errorWhileSortingListFactory(3)
            )

            return
        }

        val filteredRankingList = rankingScreenData.rankingListWithPlaces

        logcat { "rankingTableSortType: $rankingTableSortParameters" }

        val sortingAction = {
            currRankingComponent.rankingListHelper.sortData(
                filteredRankingList,
                rankingTableSortParameters
            )
        }

        val sortedData = if (doDelay) {
            doActionWithDelayUpToDefaultMinimal(currTimeSpanComponent.asyncTimeSpan, sortingAction)
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

        fsmStateHolder.publishIdleState(
            RankingScreenData.RankingListIsSorted(
                rankingScreenData,
                rankingTableSortParameters,
                sortedData,
                directionOfSortableColumns
            )
        )
    }

    private suspend fun<T> doActionWithDelayUpToDefaultMinimal(
        asyncTimeSpan: AsyncTimeSpan,
        block: () -> T
    ): T {
        asyncTimeSpan.turnOn()

        val res = block.invoke()

        asyncTimeSpan.turnOff()
        val timeToDelay = MINIMAL_UI_ACTION_DELAY - asyncTimeSpan.getElapsed()

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
