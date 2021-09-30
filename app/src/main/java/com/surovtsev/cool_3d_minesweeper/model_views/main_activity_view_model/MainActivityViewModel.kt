package com.surovtsev.cool_3d_minesweeper.model_views.main_activity_view_model

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.dagger.app.AppScope
import com.surovtsev.cool_3d_minesweeper.presentation.Screen
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import javax.inject.Inject

typealias HasSaveEvent = MyLiveData<Boolean>
typealias ButtonsInfo = List<MainActivityViewModel.ButtonInfo>

@AppScope
class MainActivityViewModel @Inject constructor(
    private val saveController: SaveController
) {
    enum class ButtonType {
        OrdinaryButton,
        NewGameButton,
        LoadGameButton
    }

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
