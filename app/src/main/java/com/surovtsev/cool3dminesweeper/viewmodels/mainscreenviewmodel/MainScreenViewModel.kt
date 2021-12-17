package com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.surovtsev.cool3dminesweeper.controllers.applicationcontroller.appComponent
import com.surovtsev.cool3dminesweeper.dagger.app.mainscreen.DaggerMainScreenComponent
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.ButtonsInfo
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import logcat.logcat
import javax.inject.Inject

interface ViewModelAssistedFactory<T : ViewModel> {
    fun create(handle: SavedStateHandle): T
}

class MainScreenViewModel @Inject constructor(
    context: Context,
//    @Assisted
//    savedStateHandle: SavedStateHandle,
): ViewModel() {

//    @AssistedFactory
//    interface Factory: ViewModelAssistedFactory<MainScreenViewModel> {
//    }

    companion object {
        const val NewGame = "new game"
        const val LoadGame = "load game"
        const val Ranking = "ranking"
        const val Settings = "settings"
        const val Help = "help"
    }

    val buttonsInfo: ButtonsInfo

    val saveController: SaveController

    init {
        logcat { "init: ${System.identityHashCode(this)}" }

        val mainScreenComponent = DaggerMainScreenComponent
            .builder()
            .appComponent(context.appComponent)
            .build()

        buttonsInfo = mainScreenComponent.buttonInfo
        saveController = mainScreenComponent.saveController
    }

//    fun load(context: Context) {
//        val mainScreenComponent = DaggerMainScreenComponent
//            .builder()
//            .appComponent(context.appComponent)
//            .build()
//
//        buttonsInfo = mainScreenComponent.buttonInfo
//        saveController = mainScreenComponent.saveController
//
//    }
//
    fun hasSave() =
        saveController.hasData(
            SaveTypes.SaveGameJson
        )
}
