package com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.surovtsev.cool3dminesweeper.controllers.applicationcontroller.appComponent
import com.surovtsev.cool3dminesweeper.dagger.app.mainscreen.DaggerMainScreenComponent
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.ButtonsInfo
import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import logcat.logcat


class MainScreenViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
    @Assisted appComponentEntryPoint: AppComponentEntryPoint,
): ViewModel() {

    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<MainScreenViewModel>

    object ButtonNames {
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

    fun hasSave() =
        saveController.hasData(
            SaveTypes.SaveGameJson
        )
}
