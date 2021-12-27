package com.surovtsev.gamescreen.minesweeper

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.interaction.commandhandler.CommandHandler
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.GameLogicStateHelper
import com.surovtsev.gamescreen.minesweeper.helpers.MinesweeperGameStatusReceiver
import com.surovtsev.gamescreen.minesweeper.interaction.opengleventshandler.MinesweeperOpenGLEventsHandler
import javax.inject.Inject

@GameScope
class Minesweeper @Inject constructor(
    /* Do not delete this. It is used:
    - to add new record into Ranking table when game is won;
    - to notify view about game status change.
*/
    private val minesweeperGameStatusReceiver: MinesweeperGameStatusReceiver,
    val openGLEventsHandler: MinesweeperOpenGLEventsHandler,
    val commandHandler: CommandHandler,
    val gameLogicStateHelper: GameLogicStateHelper,
)
