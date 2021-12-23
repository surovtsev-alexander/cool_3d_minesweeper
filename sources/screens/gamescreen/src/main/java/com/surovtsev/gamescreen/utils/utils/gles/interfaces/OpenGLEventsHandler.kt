package com.surovtsev.gamescreen.utils.utils.gles.interfaces

interface OpenGLEventsHandler {
    fun onSurfaceCreated()
    fun onSurfaceChanged(width: Int, height: Int)
    fun onDrawFrame()
}
