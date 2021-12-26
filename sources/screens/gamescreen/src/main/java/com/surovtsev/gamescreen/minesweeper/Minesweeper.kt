package com.surovtsev.gamescreen.minesweeper

import com.surovtsev.gamescreen.dagger.GameScope
import com.surovtsev.gamescreen.minesweeper.commandhandler.CommandHandler
import javax.inject.Inject

@GameScope
class Minesweeper @Inject constructor(
    val openGLEventsHandler: MinesweeperOpenGLEventsHandler,
    val commandHandler: CommandHandler,
)
