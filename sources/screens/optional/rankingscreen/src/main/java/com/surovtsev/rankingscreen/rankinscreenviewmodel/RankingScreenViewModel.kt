package com.surovtsev.rankingscreen.rankinscreenviewmodel

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.viewmodel.templatescreenviewmodel.TemplateScreenViewModel
import com.surovtsev.rankingscreen.dagger.DaggerRankingScreenComponent
import com.surovtsev.rankingscreen.dagger.RankingScreenComponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class RankingScreenViewModel @AssistedInject constructor(
    @Suppress("UNUSED_PARAMETER") @Assisted savedStateHandle: SavedStateHandle,
    @Suppress("UNUSED_PARAMETER") @Assisted context: Context,
    @Assisted private val appComponentEntryPoint: AppComponentEntryPoint,
):
    TemplateScreenViewModel()
//    RequestPermissionsResultReceiver,
{
    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<RankingScreenViewModel>

    private val rankingScreenComponent: RankingScreenComponent =
        DaggerRankingScreenComponent
            .builder()
            .appComponentEntryPoint(appComponentEntryPoint)
            .stateHolder(stateHolder)
            .finiteStateMachineFactory(::createFiniteStateMachine)
            .build()

    override val finiteStateMachine = rankingScreenComponent
        .finiteStateMachine

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        rankingScreenComponent
            .restartableCoroutineScopeComponent
            .subscriberImp
            .restart()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)

        rankingScreenComponent.restartableCoroutineScopeComponent.subscriberImp.stop()
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
