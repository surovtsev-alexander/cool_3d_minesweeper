package com.surovtsev.cool_3d_minesweeper.dagger

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.model_views.GameActivityModelView
import com.surovtsev.cool_3d_minesweeper.views.activities.GameActivity
import dagger.*
import javax.inject.Scope

@GameScope
@Subcomponent(modules = [GameModule::class])
interface GameComponent {
    val gameActivityModelView: GameActivityModelView
//    val minesweeperController: MinesweeperController

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun loadGame(loadGame: Boolean): Builder

        fun build(): GameComponent
    }

    fun inject(gameActivity: GameActivity)
}

@Module
object GameModule {
//    @Provides
//    @GameScope
//    fun provideMineSweeperController(
//        context: Context,
//        gameEventsReceiver: IGameEventsReceiver,
//        loadGame: Boolean
//    ): MinesweeperController {
//        return MinesweeperController(
//            context,
//            gameEventsReceiver,
//            loadGame
//        )
//    }
    @Provides
    @GameScope
    fun provideGameActivityModelView(
        context: Context,
        loadGame: Boolean
    ): GameActivityModelView {
        return GameActivityModelView(
            context,
            loadGame
        )
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameScope