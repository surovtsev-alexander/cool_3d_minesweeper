package com.surovtsev.cool_3d_minesweeper.model_views

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatus
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData

class GameActivityModelView(
    val context: Context
): IGameEventsReceiver {

    val marking = MyLiveData(false)


    val minesweeperController = MinesweeperController(
        context,
        this,
        false)

    fun removeMarkedBombs() {

    }

    fun removeZeroBorders() {

    }

    override fun bombCountUpdated() {

    }

    override fun timeUpdated() {

    }

    override fun gameStatusUpdated(newStatus: GameStatus) {

    }
}