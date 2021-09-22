package com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller

import android.content.Context
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CubeCoordinates
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.GameConfigFactory
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.MinesweeperGameStatusReceiver
import com.surovtsev.cool_3d_minesweeper.model_views.GameActivityModelView
import com.surovtsev.cool_3d_minesweeper.model_views.helpers.GameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.models.game.save.Save
import com.surovtsev.cool_3d_minesweeper.utils.time.TimeSpanHelper
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@GameControllerScope
@Subcomponent(modules = [GameControllerModule::class])
interface GameControllerComponent {
    val minesweeperController: MinesweeperController
    val gameObjectsHolder: GameObjectsHolder

    fun inject(gameActivityModelView: GameActivityModelView)
}

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
            val res = saveController.tryToLoad<Save>(
                SaveTypes.SaveGameJson
            )
            saveController.emptyData(
                SaveTypes.SaveGameJson
            )
            res
        } else {
            null
        }
        return save
    }

    @GameControllerScope
    @Provides
    fun provideGameConfig(
        save: Save?,
        saveController: SaveController
    ): GameConfig {
        return save?.gameConfig
            ?: GameConfigFactory.createGameConfig(
                saveController.loadSettingDataOrDefault()
            )
    }

    @GameControllerScope
    @Provides
    fun provideCameraInfo(
        save: Save?
    ): CameraInfo {
        return save?.cameraInfoToSave?.getCameraInfo()
            ?: CameraInfo()
    }

    @GameControllerScope
    @Provides
    fun provideGameObjectsHolder(
        gameConfig: GameConfig
    ): GameObjectsHolder {
        return GameObjectsHolder(gameConfig)
    }

    @GameControllerScope
    @Provides
    fun provideGameLogic(
        save: Save?,
        gameObjectsHolder: GameObjectsHolder,
        gameConfig: GameConfig,
        gameEventsReceiver: GameEventsReceiver,
        minesweeperGameStatusReceiver: MinesweeperGameStatusReceiver,
        timeSpanHelper: TimeSpanHelper
    ): GameLogic {
        val res  =
            GameLogic(
                gameObjectsHolder.cubeSkin,
                null,
                gameConfig,
                gameEventsReceiver,
                minesweeperGameStatusReceiver,
                timeSpanHelper
            )
        if (save != null) {
            save.gameLogicToSave.applySavedData(res)

            save.cubeSkinToSave.applySavedData(
                gameObjectsHolder.cubeSkin,
                res
            )
        }

        return res
    }

    @GameControllerScope
    @Provides
    fun provideCubeCoordinates(
        gameObjectsHolder: GameObjectsHolder
    ): CubeCoordinates {
        return gameObjectsHolder.cubeCoordinates
    }
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class GameControllerScope
