package com.surovtsev.gamestateholder.dagger

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint
import com.surovtsev.gamestateholder.GameStateHolder
import dagger.Component

@GameStateHolderScope
@Component(
    dependencies = [
        AppComponentEntryPoint::class,
        TimeSpanComponentEntryPoint::class,
    ],
    modules = [
    ]
)

interface GameStateHolderComponent {
    val gameStateHolder: GameStateHolder
}
