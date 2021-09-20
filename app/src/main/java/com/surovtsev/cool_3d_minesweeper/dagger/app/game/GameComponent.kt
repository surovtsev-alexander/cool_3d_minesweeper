package com.surovtsev.cool_3d_minesweeper.dagger

import android.content.Context
import android.opengl.GLSurfaceView
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.interfaces.IGameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller.GameControllerComponent
import com.surovtsev.cool_3d_minesweeper.model_views.GameActivityModelView
import com.surovtsev.cool_3d_minesweeper.views.activities.GameActivity
import dagger.*
import javax.inject.Scope

@GameScope
@Subcomponent(modules = [GameModule::class])
interface GameComponent {
    val gameActivityModelView: GameActivityModelView
    val gLSurfaceView: GLSurfaceView

    @Subcomponent.Builder
    interface Builder {
        @BindsInstance
        fun loadGame(loadGame: Boolean): Builder

        fun build(): GameComponent
    }

    fun inject(gameActivity: GameActivity)

    fun gameControllerComponent(): GameControllerComponent.Builder
}

@Module
object GameModule {
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

    @Provides
    @GameScope
    fun provideGLSurfaceView(
        context: Context
    ): GLSurfaceView {
        return GLSurfaceView(context)
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameScope