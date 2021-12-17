package com.surovtsev.gamelogic.dagger

import dagger.Module
import dagger.hilt.DefineComponent
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@GameLogicScope
@DefineComponent(
    parent = ViewModelComponent::class
)
interface GameLogicComponent {

    @DefineComponent.Builder
    interface Builder {

        fun build(): GameLogicComponent
    }
}

@InstallIn(GameLogicComponent::class)
@EntryPoint
@GameLogicScope
interface GameLogicComponentEntryPoint {

}

@Module
@InstallIn(GameLogicComponent::class)
object GameLogicComponentModule {

}
