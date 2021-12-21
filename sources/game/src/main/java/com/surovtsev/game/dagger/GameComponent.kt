package com.surovtsev.game.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.game.minesweeper.MinesweeperController
import com.surovtsev.game.minesweeper.gamelogic.GameLogic
import com.surovtsev.game.minesweeper.gamelogic.helpers.BombsLeftFlow
import com.surovtsev.game.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.game.minesweeper.gamelogic.helpers.GameLogicStateHelper
import com.surovtsev.game.minesweeper.gamelogic.helpers.GameStatusWithElapsedFlow
import com.surovtsev.game.minesweeper.helpers.GameConfigFactory
import com.surovtsev.game.minesweeper.interaction.move.MoveHandlerImp
import com.surovtsev.game.minesweeper.interaction.touch.TouchHandlerImp
import com.surovtsev.game.minesweeper.scene.Scene
import com.surovtsev.game.models.game.camerainfo.CameraInfo
import com.surovtsev.game.models.game.config.GameConfig
import com.surovtsev.game.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.game.models.game.gamestatus.GameStatusHelper
import com.surovtsev.game.models.game.interaction.*
import com.surovtsev.game.models.game.save.Save
import com.surovtsev.game.utils.gles.model.pointer.Pointer
import com.surovtsev.game.utils.gles.model.pointer.PointerImp
import com.surovtsev.game.utils.utils.gles.interfaces.OpenGLEventsHandler
import com.surovtsev.game.viewmodel.helpers.*
import com.surovtsev.game.views.glesrenderer.GLESRenderer
import com.surovtsev.game.views.opengl.CubeOpenGLModel
import com.surovtsev.utils.coroutines.CustomCoroutineScope
import com.surovtsev.utils.timers.TimeSpanHelperImp
import dagger.*
import javax.inject.Named

@GameScope
@Component(
    dependencies = [
        AppComponentEntryPoint::class,
        TimeSpanComponentEntryPoint::class,
    ],
    modules = [
        GameModule::class,
        GameEventsModule::class,
        GameControlsModule::class,
        GameControllerModule::class,
        SceneSettingsModule::class,
        GameControllerBindModule::class,
        InteractionModule::class,
    ]
)
interface GameComponent {
    val markingEvent: MarkingEvent
    val minesweeperController: MinesweeperController
    val gameRenderer: GLESRenderer
    val gameScreenEvents: GameScreenEvents
    val gameControls: GameControls
    val gameConfig: GameConfig
    val settingsDao: SettingsDao
    val rankingDao: RankingDao
    val rankingListHelper: RankingListHelper

    val touchHandlerImp: TouchHandlerImp
    val moveHandlerImp: MoveHandlerImp
    val timeSpanHelperImp: TimeSpanHelperImp

    val bombsLeftFlow: BombsLeftFlow

    val customCoroutineScope: CustomCoroutineScope

    @Component.Builder
    interface Builder {
        fun appComponentEntryPoint(appComponentEntryPoint: AppComponentEntryPoint): Builder
        fun timeSpanComponentEntryPoint(timeSpanComponentEntryPoint: TimeSpanComponentEntryPoint): Builder
        fun loadGame(@BindsInstance loadGame: Boolean): Builder
//        fun context(@BindsInstance context: Context): Builder
//        fun customCoroutineScope(@BindsInstance customCoroutineScope: CustomCoroutineScope): Builder
        fun build(): GameComponent
    }
}


@Module
object GameModule {
}

@Module
object GameEventsModule {
    @GameScope
    @Provides
    @Named(GameScreenEventsNames.ShowDialog)
    fun provideShowDialog(): ShowDialogEvent {
        return ShowDialogEvent(false)
    }

    @GameScope
    @Provides
    @Named(GameScreenEventsNames.GameStatus)
    fun provideGameStatusEvent(): GameStatusEvent {
        return GameStatusEvent(GameStatusHelper.initStatus)
    }

    @GameScope
    @Provides
    @Named(GameScreenEventsNames.LastWinPlace)
    fun provideLastWindPlaceEvent(): LastWinPlaceEvent {
        return LastWinPlaceEvent(Place.NoPlace)
    }
}

@Module
object GameControlsModule {
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

@Module
object GameControllerModule {
    @GameScope
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

    @GameScope
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

    @GameScope
    @Provides
    fun provideCameraInfo(
        save: Save?
    ): CameraInfo {
        return save?.cameraInfoToSave?.getCameraInfo()
            ?: CameraInfo()
    }

    @GameScope
    @Provides
    fun provideGameLogic(
        save: Save?,
        cubeInfo: CubeInfo,
        gameConfig: GameConfig,
        cubeOpenGLModel: CubeOpenGLModel,
        gameLogicStateHelper: GameLogicStateHelper,
    ): GameLogic {
        val res  =
            GameLogic(
                cubeInfo.cubeSkin,
                cubeOpenGLModel,
                gameConfig,
                gameLogicStateHelper,
            )
        if (save != null) {
            save.gameLogicToSave.applySavedData(res)

            save.cubeSkinToSave.applySavedData(
                cubeInfo.cubeSkin,
                res
            )
        }

        return res
    }

    @GameScope
    @Provides
    fun provideCubeCoordinates(
        gameConfig: GameConfig
    ): CubeCoordinates {
        return CubeCoordinates.createObject(gameConfig)
    }
}

@Module
interface GameControllerBindModule {
    @GameScope
    @Binds
    fun bindPointer(pointer: PointerImp): Pointer

    @GameScope
    @Binds
    fun bindOpenGLEventsHandler(
        minesweeperController: MinesweeperController
    ): OpenGLEventsHandler
}

@Module
object SceneSettingsModule {
    @Provides
    @Named(Scene.PointerEnabledName)
    fun providePointerEnabled() = false
}

@Module
object InteractionModule {
    @GameScope
    @Provides
    fun provideBombsLeftValue(
        gameLogic: GameLogic
    ): BombsLeftFlow {
        return gameLogic.bombsLeftFlow
    }

    @GameScope
    @Provides
    fun provideGameStatusWithElapsedFlow(
        gameLogicStateHelper: GameLogicStateHelper,
    ): GameStatusWithElapsedFlow {
        return gameLogicStateHelper.gameStatusWithElapsedFlow
    }
}
