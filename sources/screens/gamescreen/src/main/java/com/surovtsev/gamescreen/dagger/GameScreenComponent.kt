package com.surovtsev.gamescreen.dagger

import com.surovtsev.core.dagger.components.CameraInfoHelperHolder
import com.surovtsev.utils.gles.renderer.GLESRenderer
import com.surovtsev.utils.math.camerainfo.CameraInfo
import com.surovtsev.utils.math.camerainfo.CameraInfoHelper
import dagger.Component
import dagger.Module
import dagger.Provides

@GameScreenScope
@Component(
    dependencies = [
    ],
    modules = [
        GameScreenModule::class,
    ]
)
interface GameScreenComponent: CameraInfoHelperHolder {
    val gLESRenderer: GLESRenderer
}

@Module
object GameScreenModule {
    @GameScreenScope
    @Provides
    fun provideGLESRenderer(
        cameraInfoHelper: CameraInfoHelper,
    ): GLESRenderer {
        return GLESRenderer(
            cameraInfoHelper
        )
    }

    @GameScreenScope
    @Provides
    fun provideCameraInfoHelper(
        cameraInfo: CameraInfo,
    ): CameraInfoHelper {
        return CameraInfoHelper(
            cameraInfo
        )
    }

    @GameScreenScope
    @Provides
    fun provideCameraInfo(
    ): CameraInfo {
        return CameraInfo()
    }
}
