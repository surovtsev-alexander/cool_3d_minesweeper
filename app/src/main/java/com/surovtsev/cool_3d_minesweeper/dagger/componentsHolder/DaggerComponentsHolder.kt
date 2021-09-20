package com.surovtsev.cool_3d_minesweeper.dagger.componentsHolder

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.dagger.DaggerAppComponent
import com.surovtsev.cool_3d_minesweeper.dagger.GameComponent

class DaggerComponentsHolder(
    context: Context
) {
    val appComponent = DaggerAppComponent
        .builder()
        .context(context)
        .build()
    var gameComponent: GameComponent? = null
        private set

    fun createAndGetGameComponent(context: Context, loadGame: Boolean): GameComponent {
        emptyGameComponent()
        val res = appComponent
            .gameComponent()
            .loadGame(loadGame)
            .build()
        gameComponent = res
        return res
    }

    fun emptyGameComponent() {
        gameComponent = null
    }
}