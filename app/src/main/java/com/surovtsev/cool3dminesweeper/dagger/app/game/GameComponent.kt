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
import com.surovtsev.cool3dminesweeper.dagger.app.GameScope
import com.surovtsev.cool3dminesweeper.viewmodels.gamescreenviewmodel.helpers.*
import com.surovtsev.cool3dminesweeper.models.game.camerainfo.CameraInfo
import com.surovtsev.cool3dminesweeper.models.game.config.GameConfig
import com.surovtsev.cool3dminesweeper.models.game.gameobjectsholder.GameObjectsHolder
import com.surovtsev.cool3dminesweeper.models.game.gamestatus.GameStatusHelper
import com.surovtsev.cool3dminesweeper.models.game.interaction.*
import com.surovtsev.cool3dminesweeper.models.game.save.Save
import com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.TouchListener
import com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers.ClickAndRotationHelper
import com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers.ScalingHelper
import com.surovtsev.cool3dminesweeper.utils.androidview.touchlistener.helpers.TouchHelper
import com.surovtsev.cool3dminesweeper.utils.gles.interfaces.IHandleOpenGLEvents
import com.surovtsev.cool3dminesweeper.utils.gles.model.pointer.IPointer
import com.surovtsev.cool3dminesweeper.utils.gles.model.pointer.Pointer
import com.surovtsev.cool3dminesweeper.views.glesrenderer.GLESRenderer
import com.surovtsev.cool3dminesweeper.views.opengl.CubeView
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import glm_.vec2.Vec2
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
    val loadGame: Boolean
    val markingEvent: MarkingEvent
    val minesweeperController: MinesweeperController
    val gameRenderer: GLESRenderer
    val gLSurfaceView: GLSurfaceView
    val gameScreenEvents: GameScreenEvents
    val gameControls: GameControls
}


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
        gameObjectsHolder: GameObjectsHolder,
        gameConfig: GameConfig,
        gameScreenEventsReceiver: GameScreenEventsReceiver,
        cubeView: CubeView,
        gameLogicStateHelper: GameLogicStateHelper
    ): GameLogic {
        val res  =
            GameLogic(
                gameObjectsHolder.cubeSkin,
                cubeView,
                gameConfig,
                gameScreenEventsReceiver,
                gameLogicStateHelper
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

    @GameScope
    @Provides
    fun provideCubeCoordinates(
        gameConfig: GameConfig
    ): CubeCoordinates {
        return CubeCoordinates.createObject(gameConfig)
    }

    @Named(TouchListener.PrevPointerCount)
    @GameScope
    @Provides
    fun providePrevPointerCount() = 0
}

@Module
@InstallIn(GameComponent::class)
interface GameControllerBindModule {
    @GameScope
    @Binds
    fun getIPointer(pointer: Pointer): IPointer

    @GameScope
    @Binds
    fun getIHandleOpenGLEvent(
        minesweeperController: MinesweeperController
    ): IHandleOpenGLEvents

    @GameScope
    @Binds
    fun getCurrTouchHelper(
        clickAndRotationHelper: ClickAndRotationHelper
    ): TouchHelper
}

@Module
@InstallIn(GameComponent::class)
object TouchHelperModule {
    @GameScope
    @Provides
    @Named(ScalingHelper.PrevDistance)
    fun getPrevDistance() = 0f
}

@Module
@InstallIn(GameComponent::class)
object ScalingHelperModule {
    @Provides
    @Named(TouchHelper.PrevCenter)
    fun providePrevCenter() = Vec2()
}

@Module
@InstallIn(GameComponent::class)
object ClickAndRotationHelperModule {
    @Provides
    @Named(ClickAndRotationHelper.Prev)
    fun providePrev() = Vec2()

    @Provides
    @Named(ClickAndRotationHelper.Movement)
    fun provideMovement() = 0f

    @GameScope
    @Provides
    @Named(ClickAndRotationHelper.Downed)
    fun provideDowned() = false
}
