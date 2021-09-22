package com.surovtsev.cool_3d_minesweeper.dagger.componentsHolder

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.dagger.app.DaggerAppComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.GameComponent
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller.GameControllerComponent

class DaggerComponentsHolder(
    context: Context
) {
    val appComponent = DaggerAppComponent
        .builder()
        .context(context)
        .build()
    var gameComponent: GameComponent? = null
        private set

    var gameControllerComponent: GameControllerComponent? = null
        private set

    fun createAndGetGameComponent(loadGame: Boolean): GameComponent {
        emptyGameComponent()
        val res = appComponent
            .gameComponent()
            .loadGame(loadGame)
            .build()
        gameComponent = res
        return res
    }

    fun createAndGetGameControllerComponent(): GameControllerComponent {
        emptyGameControllerComponent()
        val res = gameComponent!!
            .gameControllerComponent()
        gameControllerComponent = res
        return res
    }

    private fun emptyGameComponent() {
        emptyGameControllerComponent()
        gameComponent = null
    }

    private fun emptyGameControllerComponent() {
        gameControllerComponent = null
    }
}
