package com.surovtsev.cool_3d_minesweeper.dagger

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.model_views.GameActivityModelView
import com.surovtsev.cool_3d_minesweeper.views.activities.GameActivity
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Scope

@Component(modules = [GameModule::class])
@GameScope
interface GameComponent {
    val minesweeperController: MinesweeperController

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun gameEventsReceiver(gameEventsReceiver: IGameEventsReceiver): Builder

        fun build(): GameComponent
    }

    fun inject(gameActivityModelView: GameActivityModelView)
}

@Module
object GameModule {
    @Provides
    @GameScope
    fun provideMineSweeperController(
        context: Context,
        gameEventsReceiver: IGameEventsReceiver
    ): MinesweeperController {
        return MinesweeperController(
            context,
            gameEventsReceiver
        )
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameScope