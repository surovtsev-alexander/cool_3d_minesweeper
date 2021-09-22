package com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller.surface

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.models.gles.game_views_holder.GameViewsHolder
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Named
import javax.inject.Scope

@GLSurfaceControllerScope
@Subcomponent(modules = [GLSurfaceModel::class])
interface GLSurfaceComponent {
    @Subcomponent.Builder
    interface Builder {

        fun build(): GLSurfaceComponent
    }

    fun inject(minesweeperController: MinesweeperController)
}

@Module
object GLSurfaceModel {
//    @GLSurfaceControllerScope
//    @Provides
//    @Named("X")
//    fun provideX(): MinesweeperController.XX = MinesweeperController.XX("hello from gl surface controller")
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GLSurfaceControllerScope