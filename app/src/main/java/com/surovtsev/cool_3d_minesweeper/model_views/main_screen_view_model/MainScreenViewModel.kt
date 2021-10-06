package com.surovtsev.cool_3d_minesweeper.model_views.main_screen_view_model

import androidx.lifecycle.ViewModel
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.dagger.app.main_screen.MainScreenComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.main_screen.MainScreenEntryPoint
import com.surovtsev.cool_3d_minesweeper.presentation.main_screen.ButtonsInfo
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
