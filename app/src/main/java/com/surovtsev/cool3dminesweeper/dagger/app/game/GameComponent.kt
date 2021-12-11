package com.surovtsev.cool3dminesweeper.dagger.app.game

import android.content.Context
import android.opengl.GLSurfaceView
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.GameLogic
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.CubeCoordinates
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.GameLogicStateHelper
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveController
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.gamelogic.helpers.save.SaveTypes
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.helpers.GameConfigFactory
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.interaction.move.MoveHandlerImp
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.interaction.touch.TouchHandlerImp
import com.surovtsev.cool3dminesweeper.controllers.minesweeper.scene.Scene
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.models.game.camerainfo.CameraInfo
import com.surovtsev.cool3dminesweeper.models.game.config.GameConfig
import com.surovtsev.cool3dminesweeper.models.game.gameobjectsholder.CubeInfo
import com.surovtsev.cool3dminesweeper.models.game.gamestatus.GameStatusHelper
import com.surovtsev.cool3dminesweeper.models.game.interaction.*
import com.surovtsev.cool3dminesweeper.models.game.save.Save
import com.surovtsev.cool3dminesweeper.models.room.dao.RankingDao
import com.surovtsev.cool3dminesweeper.models.room.dao.SettingsDao
import com.surovtsev.cool3dminesweeper.utils.gles.interfaces.OpenGLEventsHandler
import com.surovtsev.cool3dminesweeper.utils.gles.model.pointer.Pointer
import com.surovtsev.cool3dminesweeper.utils.gles.model.pointer.PointerImp
import com.surovtsev.cool3dminesweeper.utils.time.timers.TimeSpan
import com.surovtsev.cool3dminesweeper.utils.time.timers.TimeSpanHelperImp
import com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.helpers.*
import com.surovtsev.cool3dminesweeper.viewmodels.rankinscreenviewmodel.helpers.RankingListHelper
import com.surovtsev.cool3dminesweeper.views.glesrenderer.GLESRenderer
import com.surovtsev.cool3dminesweeper.views.opengl.CubeOpenGLModel
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named

@GameScope
@DefineComponent(
    parent = ViewModelComponent::class
)
interface GameComponent {

    @DefineComponent.Builder
    interface Builder {
        fun loadGame(@BindsInstance loadGame: Boolean): Builder
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
}

//@Module
//@InstallIn(GameComponent::class)
//object TouchListenerCreator {
//    @GameScope
//    @Provides
//    fun provideTouchListenerComponent(
//        timeSpanHelper: TimeSpanHelperImp,
//        moveHandlerImp: MoveHandlerImp,
//        touchHandlerImp: TouchHandlerImp,
//    ): TouchListenerComponent {
//        return DaggerTouchListenerComponent
//            .builder()
//            .timeSpanHelper(timeSpanHelper)
//            .moveHandler(moveHandlerImp)
//            .touchHandler(touchHandlerImp)
//            .build()
//    }
//}

@Module
@InstallIn(GameComponent::class)
object GameModule {
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
    @Named(GameScreenEventsNames.ElapsedTime)
    fun provideElapsedTime(): ElapsedTimeEvent {
        return ElapsedTimeEvent(0L)
    }

    @GameScope
    @Provides
    @Named(GameScreenEventsNames.BombsLeft)
    fun provideBombsLeft(): BombsLeftEvent {
        return BombsLeftEvent(0)
    }

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
        gameLogicStateHelper: GameLogicStateHelper
    ): GameLogic {
        val res  =
            GameLogic(
                cubeInfo.cubeSkin,
                cubeOpenGLModel,
                gameConfig,
                gameScreenEventsReceiver,
                gameLogicStateHelper
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
object GameLogicStateHelperModule {
    @GameScope
    @Provides
    fun provideTimeSpan(
        timeSpanHelper: TimeSpanHelperImp
    ): TimeSpan {
        return TimeSpan(1000L, timeSpanHelper)
    }
}

