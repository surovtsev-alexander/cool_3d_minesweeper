package com.surovtsev.gamelogic.minesweeper

import com.surovtsev.gamestate.dagger.GameScope
import com.surovtsev.gamelogic.minesweeper.gamelogic.helpers.GameStatusHolderBridge
import com.surovtsev.gamelogic.minesweeper.helpers.MinesweeperGameStatusReceiver
import com.surovtsev.gamelogic.minesweeper.interaction.commandhandler.CommandHandler
import com.surovtsev.gamelogic.minesweeper.interaction.opengleventshandler.MinesweeperOpenGLEventsHandler
import javax.inject.Inject

@GameScope
class Minesweeper @Inject constructor(
    /* Do not delete this. It is used:
    - to add new record into Ranking table when game is won;
    - to notify view about game status change.
*/
    private val minesweeperGameStatusReceiver: MinesweeperGameStatusReceiver,
    private val gameStatusHolderBridge: GameStatusHolderBridge,
    val openGLEventsHandler: MinesweeperOpenGLEventsHandler,
    val commandHandler: CommandHandler,
)
