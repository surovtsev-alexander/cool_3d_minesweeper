/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamestate.logic.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.gamestate.logic.GameState
import com.surovtsev.core.helpers.gamelogic.CubeCoordinates
import com.surovtsev.core.helpers.gamelogic.NeighboursCalculator
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.core.models.game.skin.cube.CubeSkin
import com.surovtsev.gamestate.logic.models.game.aabb.tree.AABBTree
import com.surovtsev.gamestate.logic.models.game.cubeinfo.CubeInfo
import com.surovtsev.gamestate.logic.models.game.gamestatus.GameStatusHolder
import com.surovtsev.gamestate.logic.models.game.save.Save
import com.surovtsev.gamestate.logic.models.game.spaceborders.cube.CubeSpaceBorder
import com.surovtsev.utils.math.camerainfo.CameraInfo
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import kotlin.system.measureTimeMillis

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
        save: Save?,
    ): GameState {
        val res = GameState(
            gameConfig,
            cubeInfo,
            cameraInfo,
            gameStatusHolder,
        )

        if (save != null) {
            save.gameLogicToSave.applySavedData(
                res,
            )

            save.cubeSkinToSave.applySavedData(
                res.cubeInfo,
                res.gameStatusHolder,
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
            gameConfig.cellsRange
        )
    }

    @GameStateScope
    @Provides
    fun provideNeighboursCalculator(
        gameConfig: GameConfig,
        cubeSkin: CubeSkin,
    ): NeighboursCalculator = NeighboursCalculator(
        gameConfig,
        cubeSkin,
    )

    @GameStateScope
    @Provides
    fun provideAABBTree(
        gameConfig: GameConfig,
        cubeSpaceBorder: CubeSpaceBorder,
    ): AABBTree {
        val res: AABBTree

        val calculationTime = measureTimeMillis {
            res = AABBTree(
                gameConfig,
                cubeSpaceBorder,
            )
        }

        println("provideAABBTree; calculationTime: $calculationTime")
        return res
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
            ?: GameConfig(
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
