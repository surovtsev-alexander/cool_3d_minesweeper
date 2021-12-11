package com.surovtsev.utils.timers

interface TimeSpanHelper: Tickable, TimeUpdater {
    val timeAfterDeviceStartup: Long
}