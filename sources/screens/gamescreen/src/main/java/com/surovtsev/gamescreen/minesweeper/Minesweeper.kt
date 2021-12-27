package com.surovtsev.gamescreen.minesweeper

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.commandhandler.CommandHandler
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.GameLogicStateHelper
import javax.inject.Inject

@GameScope
class Minesweeper @Inject constructor(
    val openGLEventsHandler: MinesweeperOpenGLEventsHandler,
    val commandHandler: CommandHandler,
    val gameLogicStateHelper: GameLogicStateHelper,
)
