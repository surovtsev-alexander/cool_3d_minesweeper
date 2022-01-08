package com.surovtsev.gamestate.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.gamestate.GameState
import com.surovtsev.core.helpers.gamelogic.CubeCoordinates
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.core.models.game.skin.cube.CubeSkin
import com.surovtsev.gamestate.helpers.GameConfigFactory
import com.surovtsev.gamestate.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.gamestate.models.game.gamestatus.GameStatusHolder
import com.surovtsev.gamestate.models.game.save.Save
import com.surovtsev.gamestate.models.game.spaceborders.cube.CubeSpaceBorder
import com.surovtsev.utils.coroutines.customcoroutinescope.subscription.SubscriptionsHolder
import com.surovtsev.utils.math.camerainfo.CameraInfo
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides

@GameStateScope
@Component(
    dependencies = [
        AppComponentEntryPoint::class,
        TimeSpanComponentEntryPoint::class,
    ],
    modules = [
        GameStateModule::class,
    ]
)
interface GameStateComponent {
    val gameState: GameState

    @Component.Builder
    interface Builder {
        fun appComponentEntryPoint(appComponentEntryPoint: AppComponentEntryPoint): Builder
        fun timeSpanComponentEntryPoint(timeSpanComponentEntryPoint: TimeSpanComponentEntryPoint): Builder

        fun loadGame(@BindsInstance loadGame: Boolean): Builder

        fun build(): GameStateComponent
    }
}

@Module
object GameStateModule {
    @GameStateScope
    @Provides
    fun provideGameState(
        gameConfig: GameConfig,
        cubeInfo: CubeInfo,
        cameraInfo: CameraInfo,
        gameStatusHolder: GameStatusHolder,
        cubeSpaceBorder: CubeSpaceBorder,
        save: Save?,
    ): GameState {
        val res = GameState(
            gameConfig,
            cubeInfo,
            cameraInfo,
            gameStatusHolder,
            cubeSpaceBorder,
        )

        if (save != null) {
            save.gameLogicToSave.applySavedData(
                res,
            )

            save.cubeSkinToSave.applySavedData(
                res.cubeInfo.cubeSkin,
                res.gameStatusHolder
            )
        }

        return res
    }

    @GameStateScope
    @Provides
    fun provideCubeSkin(
        gameConfig: GameConfig
    ): CubeSkin {
        return CubeSkin(
            gameConfig
        )
    }


    @GameStateScope
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

    @GameStateScope
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

    @GameStateScope
    @Provides
    fun provideCameraInfo(
        save: Save?
    ): CameraInfo {
        return save?.cameraInfoToSave?.getCameraInfo()
            ?: CameraInfo()
    }

    @GameStateScope
    @Provides
    fun provideCubeCoordinates(
        gameConfig: GameConfig
    ): CubeCoordinates {
        return CubeCoordinates.createObject(gameConfig)
    }
}