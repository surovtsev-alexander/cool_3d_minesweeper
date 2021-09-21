package com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.model_views.GameActivityModelView
import com.surovtsev.cool_3d_minesweeper.models.game.interaction.GameControls
import com.surovtsev.cool_3d_minesweeper.models.game.save.Save
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.Updatable
import com.surovtsev.cool_3d_minesweeper.utils.state_helpers.UpdatableOnOffSwitch
import com.surovtsev.cool_3d_minesweeper.utils.time.TimeSpanHelper
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@GameControllerScope
@Subcomponent(modules = [GameControllerModule::class])
interface GameControllerComponent {
    val minesweeperController: MinesweeperController

    @Subcomponent.Builder
    interface Builder {

        fun build(): GameControllerComponent
    }

    fun inject(gameActivityModelView: GameActivityModelView)
}

//@Module
//abstract class GameControllerSupportModule {
//    @Binds
//    @GameControllerScope
//    abstract fun bindGameEventsReceiver(
//        gameActivityModelView: GameActivityModelView
//    ): IGameEventsReceiver
//}

@Module
object GameControllerModule {

    @GameControllerScope
    @Provides
    fun provideTimeSpanHelper(): TimeSpanHelper {
        return TimeSpanHelper()
    }

    @GameControllerScope
    @Provides
    fun provideSaveController(
        context: Context
    ): SaveController {
        return SaveController(context)
    }

    @GameControllerScope
    @Provides
    fun provideSave(
        saveController: SaveController,
        loadGame: Boolean
    ): Save? {
        val save = if (loadGame) {
            saveController.tryToLoad<Save>(
                SaveTypes.SaveGameJson
            )
        } else {
            null
        }
        return save
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameControllerScope
