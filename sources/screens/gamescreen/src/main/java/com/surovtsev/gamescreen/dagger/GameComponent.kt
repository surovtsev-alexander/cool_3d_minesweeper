package com.surovtsev.gamescreen.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.gamescreen.minesweeper.MinesweeperController
import com.surovtsev.gamescreen.minesweeper.gamelogic.GameLogic
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.BombsLeftFlow
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.GameLogicStateHelper
import com.surovtsev.gamescreen.minesweeper.gamelogic.helpers.GameStatusWithElapsedFlow
import com.surovtsev.gamescreen.minesweeper.helpers.GameConfigFactory
import com.surovtsev.gamescreen.minesweeper.interaction.move.MoveHandlerImp
import com.surovtsev.gamescreen.minesweeper.interaction.touch.TouchHandlerImp
import com.surovtsev.gamescreen.minesweeper.scene.Scene
import com.surovtsev.gamescreen.models.game.camerainfo.CameraInfo
import com.surovtsev.gamescreen.models.game.config.GameConfig
import com.surovtsev.gamescreen.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.gamescreen.models.game.gamestatus.GameStatusHelper
import com.surovtsev.gamescreen.models.game.interaction.*
import com.surovtsev.gamescreen.models.game.save.Save
import com.surovtsev.gamescreen.utils.gles.model.pointer.Pointer
import com.surovtsev.gamescreen.utils.gles.model.pointer.PointerImp
import com.surovtsev.gamescreen.utils.utils.gles.interfaces.OpenGLEventsHandler
import com.surovtsev.gamescreen.viewmodel.helpers.*
import com.surovtsev.gamescreen.views.glesrenderer.GLESRenderer
import com.surovtsev.gamescreen.views.opengl.CubeOpenGLModel
import com.surovtsev.utils.coroutines.customcoroutinescope.CustomCoroutineScope
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
