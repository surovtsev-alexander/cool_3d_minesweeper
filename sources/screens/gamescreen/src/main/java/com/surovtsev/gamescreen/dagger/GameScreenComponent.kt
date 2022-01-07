package com.surovtsev.gamescreen.dagger

import com.surovtsev.core.dagger.components.GameScreenEntryPoint
import com.surovtsev.utils.gles.renderer.GLESRenderer
import com.surovtsev.utils.gles.renderer.ScreenResolutionFlow
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
interface GameScreenComponent: GameScreenEntryPoint {
    val gLESRenderer: GLESRenderer
}

@Module
object GameScreenModule {
    @GameScreenScope
    @Provides
    fun provideGLESRenderer(
    ): GLESRenderer {
        return GLESRenderer()
    }

    @GameScreenScope
    @Provides
    fun provideScreenResolutionFlow(
        gLESRenderer: GLESRenderer,
    ): ScreenResolutionFlow {
        return gLESRenderer.screenResolutionFlow
    }
}
