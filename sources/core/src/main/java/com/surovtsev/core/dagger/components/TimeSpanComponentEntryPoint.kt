package com.surovtsev.core.dagger.components

import com.surovtsev.utils.coroutines.CustomCoroutineScope
import com.surovtsev.utils.timers.TimeSpan

interface TimeSpanComponentEntryPoint {
    val timeSpan: TimeSpan
    val customCoroutineScope: CustomCoroutineScope
}