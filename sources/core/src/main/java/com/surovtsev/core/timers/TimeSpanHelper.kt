package com.surovtsev.core.timers

interface TimeSpanHelper: Tickable, TimeUpdater {
    val timeAfterDeviceStartup: Long
}