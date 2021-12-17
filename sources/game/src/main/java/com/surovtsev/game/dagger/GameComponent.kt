package com.surovtsev.game.dagger

import android.content.Context
import android.opengl.GLSurfaceView
import com.surovtsev.core.helpers.RankingListHelper
import com.surovtsev.core.room.dao.RankingDao
import com.surovtsev.core.room.dao.SettingsDao
import com.surovtsev.core.savecontroller.SaveController
import com.surovtsev.core.savecontroller.SaveTypes
import com.surovtsev.game.minesweeper.MinesweeperController
import com.surovtsev.game.minesweeper.gamelogic.GameLogic
import com.surovtsev.game.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.game.minesweeper.gamelogic.helpers.GameLogicStateHelper
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
import com.surovtsev.game.viewmodel.GameScreenInitialState
import com.surovtsev.game.viewmodel.GameScreenStateHolder
import com.surovtsev.game.viewmodel.GameScreenStateValue
import com.surovtsev.game.viewmodel.helpers.*
import com.surovtsev.game.views.glesrenderer.GLESRenderer
import com.surovtsev.game.views.opengl.CubeOpenGLModel
import com.surovtsev.utils.coroutines.CustomCoroutineScope
import com.surovtsev.utils.timers.TimeSpan
import com.surovtsev.utils.timers.TimeSpanHelperImp
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Named

@GameScope
@DefineComponent(
    parent = ViewModelComponent::class
)
interface GameComponent {

    @DefineComponent.Builder
    interface Builder {
        fun loadGame(@BindsInstance loadGame: Boolean): Builder
        fun customCoroutineScope(@BindsInstance customCoroutineScope: CustomCoroutineScope): Builder
        fun build(): GameComponent
    }
}

@InstallIn(GameComponent::class)
@EntryPoint
@GameScope
interface GameComponentEntryPoint {
    val markingEvent: MarkingEvent
    val minesweeperController: MinesweeperController
    val gameRenderer: GLESRenderer
    val gLSurfaceView: GLSurfaceView
    val gameScreenEvents: GameScreenEvents
    val gameControls: GameControls
    val gameConfig: GameConfig
    val settingsDao: SettingsDao
    val rankingDao: RankingDao
    val rankingListHelper: RankingListHelper

    val touchHandlerImp: TouchHandlerImp
    val moveHandlerImp: MoveHandlerImp
    val timeSpanHelperImp: TimeSpanHelperImp

    val gameScreenStateHolder: GameScreenStateHolder
    val gameScreenStateValue: GameScreenStateValue

    val bombsLeftValue: BombsLeftValue

    val timeSpan: TimeSpan
}

@Module
@InstallIn(GameComponent::class)
object GameModule {

    @GameScope
    @Provides
    fun provideGameScreenStateHolder(
    ): GameScreenStateHolder {
        return GameScreenStateHolder(
            GameScreenInitialState
        )
    }

    @GameScope
    @Provides
    fun provideGameScreenStateValue(
        gameScreenStateHolder: GameScreenStateHolder
    ): GameScreenStateValue {
        return gameScreenStateHolder
    }

    @GameScope
    @Provides
    fun provideGLSurfaceView(
        @ApplicationContext context: Context
    ): GLSurfaceView {
        return GLSurfaceView(context)
    }

}

@Module
@InstallIn(GameComponent::class)
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
@InstallIn(GameComponent::class)
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
@InstallIn(GameComponent::class)
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
        gameScreenEventsReceiver: GameScreenEventsReceiver,
        cubeOpenGLModel: CubeOpenGLModel,
        gameLogicStateHelper: GameLogicStateHelper,
        bombsLeftData: BombsLeftData,
        bombsleftValue: BombsLeftValue
    ): GameLogic {
        val res  =
            GameLogic(
                cubeInfo.cubeSkin,
                cubeOpenGLModel,
                gameConfig,
                gameScreenEventsReceiver,
                gameLogicStateHelper,
                bombsLeftData,
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
@InstallIn(GameComponent::class)
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
@InstallIn(GameComponent::class)
object SceneSettingsModule {
    @Provides
    @Named(Scene.PointerEnabledName)
    fun providePointerEnabled() = false
}

@Module
@InstallIn(GameComponent::class)
object InteractionModule {
    @GameScope
    @Provides
    fun provideBombsLeftData(
    ): BombsLeftData {
        return MutableStateFlow(0)
    }

    @GameScope
    @Provides
    fun provideBombsLeftValue(
        bombsLeftData: BombsLeftData
    ): BombsLeftValue {
        return bombsLeftData.asStateFlow()
    }

    @GameScope
    @Provides
    fun provideTimeSpan(
        timeSpanHelper: TimeSpanHelperImp,
    ): TimeSpan {
        return TimeSpan(
            1000L,
            timeSpanHelper,
        )
    }
}
