package com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel

import androidx.lifecycle.ViewModel
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveTypes
import com.surovtsev.cool3dminesweeper.dagger.app.mainscreen.MainScreenComponent
import com.surovtsev.cool3dminesweeper.dagger.app.mainscreen.MainScreenEntryPoint
import com.surovtsev.cool3dminesweeper.presentation.mainscreen.ButtonsInfo
import dagger.hilt.EntryPoints
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Provider


@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val saveController: SaveController,
    mainScreenComponentProvider: Provider<MainScreenComponent.Builder>
): ViewModel() {

    companion object {
        const val NewGame = "new game"
        const val LoadGame = "load game"
        const val Ranking = "ranking"
        const val Settings = "settings"
        const val Help = "help"
    }

    val buttonsInfo: ButtonsInfo

    init {
        val mainScreenComponent = mainScreenComponentProvider
            .get()
            .build()
        val mainScreenEntryPoint = EntryPoints.get(
            mainScreenComponent, MainScreenEntryPoint::class.java
        )

        buttonsInfo = mainScreenEntryPoint.buttonsInfo
    }

    fun hasSave() =
        saveController.hasData(
            SaveTypes.SaveGameJson
        )
}
