package com.surovtsev.cool_3d_minesweeper.dagger.app.game.controller

import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.MinesweeperController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.CubeCoordinates
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.GameLogicStateHelper
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveController
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.save.SaveTypes
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.helpers.GameConfigFactory
import com.surovtsev.cool_3d_minesweeper.dagger.app.GameControllerScope
import com.surovtsev.cool_3d_minesweeper.model_views.game_activity_view_model.GameActivityViewModel
import com.surovtsev.cool_3d_minesweeper.model_views.game_activity_view_model.helpers.GameEventsReceiver
import com.surovtsev.cool_3d_minesweeper.models.game.camera_info.CameraInfo
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.game_objects_holder.GameObjectsHolder
import com.surovtsev.cool_3d_minesweeper.models.game.save.Save
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.TouchListener
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.ClickAndRotationHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.ScalingHelper
import com.surovtsev.cool_3d_minesweeper.utils.android_view.touch_listener.helpers.TouchHelper
import com.surovtsev.cool_3d_minesweeper.utils.gles.interfaces.IHandleOpenGLEvents
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.IPointer
import com.surovtsev.cool_3d_minesweeper.utils.gles.model.pointer.Pointer
import com.surovtsev.cool_3d_minesweeper.views.opengl.CubeView
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import glm_.vec2.Vec2
import javax.inject.Named

@GameControllerScope
@Subcomponent(modules = [
    GameControllerModule::class,
    GameControllerBindModule::class,
    TouchHelperModule::class,
    ScalingHelperModule::class,
    ClickAndRotationHelperModule::class
])
interface GameControllerComponent {
    val minesweeperController: MinesweeperController

    fun inject(gameActivityViewModel: GameActivityViewModel)
}

@Module
object GameControllerModule {
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
    fun provideGameLogic(
        save: Save?,
        gameObjectsHolder: GameObjectsHolder,
        gameConfig: GameConfig,
        gameEventsReceiver: GameEventsReceiver,
        cubeView: CubeView,
        gameLogicStateHelper: GameLogicStateHelper
    ): GameLogic {
        val res  =
            GameLogic(
                gameObjectsHolder.cubeSkin,
                cubeView,
                gameConfig,
                gameEventsReceiver,
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

    @GameControllerScope
    @Provides
    fun provideCubeCoordinates(
        gameConfig: GameConfig
    ): CubeCoordinates {
        return CubeCoordinates.createObject(gameConfig)
    }

    @Named(TouchListener.PrevPointerCount)
    @GameControllerScope
    @Provides
    fun providePrevPointerCount() = 0
}

@Suppress("unused")
@Module
interface GameControllerBindModule {
    @GameControllerScope
    @Binds
    fun getIPointer(pointer: Pointer): IPointer

    @GameControllerScope
    @Binds
    fun getIHandleOpenGLEvent(
        minesweeperController: MinesweeperController
    ): IHandleOpenGLEvents

    @GameControllerScope
    @Binds
    fun getCurrTouchHelper(
        clickAndRotationHelper: ClickAndRotationHelper
    ): TouchHelper
}

@Module
object TouchHelperModule {
    @GameControllerScope
    @Provides
    @Named(ScalingHelper.PrevDistance)
    fun getPrevDistance() = 0f
}

@Module
object ScalingHelperModule {
    @Provides
    @Named(TouchHelper.PrevCenter)
    fun providePrevCenter() = Vec2()
}

@Module
object ClickAndRotationHelperModule {
    @Provides
    @Named(ClickAndRotationHelper.Prev)
    fun providePrev() = Vec2()

    @Provides
    @Named(ClickAndRotationHelper.Movement)
    fun provideMovement() = 0f

    @GameControllerScope
    @Provides
    @Named(ClickAndRotationHelper.Downed)
    fun provideDowned() = false
}
