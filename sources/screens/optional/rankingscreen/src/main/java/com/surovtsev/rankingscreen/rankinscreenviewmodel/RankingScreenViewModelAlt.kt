package com.surovtsev.rankingscreen.rankinscreenviewmodel

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.finitestatemachine.stateholder.StateHolderImp
import com.surovtsev.finitestatemachine.state.StateDescription
import com.surovtsev.finitestatemachine.state.StateDescriptionWithData
import com.surovtsev.rankingscreen.rankinscreenviewmodel.alt.EventToRankingScreenViewModelAlt
import com.surovtsev.rankingscreen.rankinscreenviewmodel.alt.FinishActionHolder
import com.surovtsev.rankingscreen.rankinscreenviewmodel.alt.FinishActionHolderImp
import com.surovtsev.rankingscreen.rankinscreenviewmodel.alt.TemplateScreenViewModelAlt
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.RankingScreenDaggerComponentsHolder
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.RankingScreenEventChecker
import com.surovtsev.rankingscreen.rankinscreenviewmodel.helpers.RankingScreenEventProcessor
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.StateFlow


typealias RankingScreenStateFlowAlt = StateFlow<StateDescriptionWithData<out RankingScreenDataAlt>>

class RankingScreenViewModelAlt constructor(
    savedStateHandle: SavedStateHandle,
    context: Context,
    private val appComponentEntryPoint: AppComponentEntryPoint,
    finishActionHolder: FinishActionHolder,
    private val rankingScreenDaggerComponentsHolder: RankingScreenDaggerComponentsHolder,
):
    TemplateScreenViewModelAlt<EventToRankingScreenViewModelAlt, RankingScreenDataAlt>(
        RankingScreenEventChecker(),
        RankingScreenEventProcessor(rankingScreenDaggerComponentsHolder, StateHolderImp<RankingScreenDataAlt>(StateDescriptionWithData(StateDescription.Idle, RankingScreenDataAlt.NoData))),
        finishActionHolder,
        StateDescriptionWithData(StateDescription.Idle, RankingScreenDataAlt.NoData),
        EventToRankingScreenViewModelAlt.MandatoryEvents,
    )
//    RequestPermissionsResultReceiver,
{
    @AssistedInject constructor(
        @Assisted savedStateHandle: SavedStateHandle,
        @Assisted context: Context,
        @Assisted appComponentEntryPoint: AppComponentEntryPoint,
    ): this(
        savedStateHandle,
        context,
        appComponentEntryPoint,
        FinishActionHolderImp(),
        RankingScreenDaggerComponentsHolder(appComponentEntryPoint),
    )

    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<RankingScreenViewModel>


    object ErrorMessages {
        val errorWhileFilteringRankingListFactory = { code: Int -> "error (code: $code) while filtering ranking list" }
        val errorWhileSortingListFactory = { code: Int -> "error (code: $code) while sorting list" }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

        rankingScreenDaggerComponentsHolder.restartableCoroutineScopeComponent?.subscriberImp?.onStop()
    }

//    override suspend fun getEventProcessor(event: EventToRankingScreenViewModel): EventProcessor? {
//        return when (event) {
//            is EventToRankingScreenViewModel.HandleScreenLeaving     -> suspend { handleScreenLeaving(event.owner) }
//            is EventToRankingScreenViewModel.LoadData                -> ::loadData
//            is EventToRankingScreenViewModel.FilterList              -> suspend { filterList(event.selectedSettingsId) }
//            is EventToRankingScreenViewModel.SortListWithNoDelay     -> suspend { sortList(event.rankingTableSortParameters, false) }
//            is EventToRankingScreenViewModel.SortList                -> suspend { sortList(event.rankingTableSortParameters, true) }
//            is EventToRankingScreenViewModel.CloseError              -> ::closeError
//            else                                                -> null
//        }
//    }
//


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
