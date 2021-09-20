package com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.model_views.GameActivityModelView
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@GameControllerScope
@Subcomponent(modules = [GameControllerModule::class])
interface GameControllerComponent {

    @Subcomponent.Builder
    interface Builder {

        @BindsInstance
        fun gameEventsReceiver(gameEventsReceiver: IGameEventsReceiver): Builder

        fun build(): GameControllerComponent
    }

    fun inject(gameActivityModelView: GameActivityModelView)
}

@Module
object GameControllerModule {

    @Provides
    @GameControllerScope
    fun provideMineSweeperController(
        context: Context,
        gameEventsReceiver: IGameEventsReceiver,
        loadGame: Boolean
    ): MinesweeperController {
        return MinesweeperController(
            context,
            gameEventsReceiver,
            loadGame
        )
    }

}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameControllerScope
