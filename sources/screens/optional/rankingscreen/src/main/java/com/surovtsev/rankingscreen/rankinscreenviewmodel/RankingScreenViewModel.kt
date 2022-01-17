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
import com.surovtsev.finitestatemachine.eventhandler.eventprocessor.EventProcessingResult
import com.surovtsev.rankingscreen.dagger.DaggerRankingScreenComponent
import com.surovtsev.rankingscreen.dagger.RankingScreenComponent
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.eventhandlerhelpers.EventCheckerImp
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.eventhandlerhelpers.EventProcessorImp
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

    private val rankingScreenComponent: RankingScreenComponent =
        DaggerRankingScreenComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .build()

    override val eventHandler: com.surovtsev.finitestatemachine.eventhandler.EventHandler<EventToRankingScreenViewModel, RankingScreenData> =
        com.surovtsev.finitestatemachine.eventhandler.EventHandler(
            EventCheckerImp(),
            EventProcessorImp(
                stateHolder,
                rankingScreenComponent,
            ),
        )

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

        rankingScreenComponent.restartableCoroutineScopeComponent.subscriberImp.onStop()
    }

    override suspend fun processEvent(event: EventToRankingScreenViewModel): EventProcessingResult<EventToRankingScreenViewModel> {
        return eventHandler.eventProcessor.processEvent(event)
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
