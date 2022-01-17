package com.surovtsev.core.dagger.dependencies

import com.surovtsev.core.dagger.components.AppComponentEntryPoint
import com.surovtsev.core.dagger.components.TimeSpanComponentEntryPoint

data class GameStateDependencies(
    val appComponentEntryPoint: AppComponentEntryPoint,
    val timeSpanComponentEntryPoint: TimeSpanComponentEntryPoint,
)
