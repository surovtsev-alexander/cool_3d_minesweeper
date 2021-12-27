package com.surovtsev.gamescreen.dagger

import com.surovtsev.utils.gles.renderer.GLESRenderer
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
interface GameScreenComponent {
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
}
