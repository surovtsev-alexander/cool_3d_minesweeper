package com.surovtsev.cool_3d_minesweeper.model_views.main_activity_view_model

import androidx.lifecycle.ViewModel
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.presentation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

typealias ButtonsInfo = List<MainActivityViewModel.ButtonInfo>

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val saveController: SaveController
): ViewModel() {
    enum class ButtonType {
        OrdinaryButton,
        NewGameButton,
        LoadGameButton
    }

    /* TODO: move to hilt */
    data class ButtonInfo(
        val screen: Screen,
        val caption: String,
        val buttonType: ButtonType = ButtonType.OrdinaryButton
    )

    val buttonsInfo: ButtonsInfo = listOf(
        ButtonInfo(Screen.GameScreen, "new game", ButtonType.NewGameButton),
        ButtonInfo(Screen.GameScreen,"load game", ButtonType.LoadGameButton),
        ButtonInfo(Screen.RankingScreen, "ranking"),
        ButtonInfo(Screen.SettingsScreen, "settings")
    )

    fun hasSave() =
        saveController.hasData(
            SaveTypes.SaveGameJson
        )
}
