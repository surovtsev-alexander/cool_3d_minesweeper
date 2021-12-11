package com.surovtsev.touchlistener.helpers.receivers

interface ScaleReceiver: TouchListenerReceiver {
    fun scale(factor: Float)
}