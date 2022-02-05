package com.surovtsev.gamelogic.minesweeper.interaction.gameinprogressflow

import kotlinx.coroutines.flow.StateFlow


typealias GameNotPausedFlow = StateFlow<Boolean>
