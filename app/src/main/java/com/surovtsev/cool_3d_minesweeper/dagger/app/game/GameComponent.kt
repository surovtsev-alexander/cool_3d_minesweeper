package com.surovtsev.cool_3d_minesweeper.dagger

import android.content.Context
import android.opengl.GLSurfaceView
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller.GameControllerComponent
import com.surovtsev.cool_3d_minesweeper.model_views.GameActivityModelView
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.*
import com.surovtsev.cool_3d_minesweeper.utils.data_constructions.MyLiveData
import com.surovtsev.cool_3d_minesweeper.views.activities.GameActivity
import dagger.*
import javax.inject.Named
import javax.inject.Scope

@GameScope
@Subcomponent(modules = [GameModule::class])
interface GameComponent {
    val gameActivityModelView: GameActivityModelView
    val gLSurfaceView: GLSurfaceView
    val gameEventsReceiver: GameEventsReceiver

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
    ): GameActivityModelView {
        return GameActivityModelView(
            context
        )
    }

    @Provides
    @GameScope
    fun provideGLSurfaceView(
        context: Context
    ): GLSurfaceView {
        return GLSurfaceView(context)
    }

    @Provides
    @GameScope
    @Named(GameViewEventsNames.Marking)
    fun provideMarking(): MarkingEvent {
        return MyLiveData(false)
    }

    @Provides
    @GameScope
    @Named(GameViewEventsNames.ElapsedTime)
    fun provideElapsedTime(): ElapsedTimeEvent {
        return MyLiveData(0L)
    }

    @Provides
    @GameScope
    @Named(GameViewEventsNames.BombsLeft)
    fun provideBombsLeft(): BombsLeftEvent {
        return MyLiveData(0)
    }

    @Provides
    @GameScope
    @Named(GameViewEventsNames.ShowDialog)
    fun provideShowDialog(): ShowDialogEvent {
        return MyLiveData(false)
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameScope