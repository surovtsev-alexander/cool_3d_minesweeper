package com.surovtsev.cool_3d_minesweeper.dagger.app.game

import android.content.Context
import android.opengl.GLSurfaceView
import com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller.GameControllerComponent
import com.surovtsev.cool_3d_minesweeper.model_views.GameActivityModelView
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.*
import com.surovtsev.cool_3d_minesweeper.models.game.game_status.GameStatusHelper
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.GameControlsNames
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.MarkOnShortTapControl
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.RemoveMarkedBombsControl
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.RemoveZeroBordersControl
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
    val markingEvent: MarkingEvent
    val gameViewEvents: GameViewEvents

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
    @Named(GameViewEventsNames.ElapsedTime)
    fun provideElapsedTime(): ElapsedTimeEvent {
        return ElapsedTimeEvent(0L)
    }

    @Provides
    @GameScope
    @Named(GameViewEventsNames.BombsLeft)
    fun provideBombsLeft(): BombsLeftEvent {
        return BombsLeftEvent(0)
    }

    @Provides
    @GameScope
    @Named(GameViewEventsNames.ShowDialog)
    fun provideShowDialog(): ShowDialogEvent {
        return ShowDialogEvent(false)
    }

    @Provides
    @GameScope
    @Named(GameViewEventsNames.GameStatus)
    fun provideGameStatusEvent(): GameStatusEvent {
        return GameStatusEvent(GameStatusHelper.initStatus)
    }

    @GameScope
    @Provides
    @Named(GameControlsNames.RemoveMarkedBombs)
    fun provideRemoveBombsControl(): RemoveMarkedBombsControl {
        return RemoveMarkedBombsControl(false)
    }

    @GameScope
    @Provides
    @Named(GameControlsNames.RemoveZeroBorders)
    fun provideRemoveZeroBordersControl(): RemoveZeroBordersControl {
        return RemoveZeroBordersControl(false)
    }

    @GameScope
    @Provides
    @Named(GameControlsNames.MarkOnShortTap)
    fun provideMarkOnShortTapControl(): MarkOnShortTapControl {
        return MarkOnShortTapControl()
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameScope