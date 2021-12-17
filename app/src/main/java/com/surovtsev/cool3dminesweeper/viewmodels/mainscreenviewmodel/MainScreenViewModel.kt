package com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import logcat.logcat
import javax.inject.Inject

class MainScreenViewModel @Inject constructor(
//    @Assisted
//    savedStateHandle: SavedStateHandle,
): ViewModel() {

//    @AssistedFactory
//    interface Factory {
//        fun create(
//            savedStateHandle: SavedStateHandle,
////            context: Context,
//        ): MainScreenViewModel
//    }

    companion object {
        const val NewGame = "new game"
        const val LoadGame = "load game"
        const val Ranking = "ranking"
        const val Settings = "settings"
        const val Help = "help"
    }

//    val buttonsInfo: ButtonsInfo
//
//    val saveController: SaveController

    init {
        logcat { "init: ${System.identityHashCode(this)}" }

//        val mainScreenComponent = DaggerMainScreenComponent
//            .builder()
//            .appComponent(context.appComponent)
//            .build()
//
//        buttonsInfo = mainScreenComponent.buttonInfo
//        saveController = mainScreenComponent.saveController
    }

//    fun hasSave() =
//        saveController.hasData(
//            SaveTypes.SaveGameJson
//        )
}
