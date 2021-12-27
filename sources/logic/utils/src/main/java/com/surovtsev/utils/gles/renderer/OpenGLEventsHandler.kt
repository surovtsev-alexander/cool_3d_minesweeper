package com.surovtsev.utils.gles.renderer

interface OpenGLEventsHandler {
    fun onSurfaceCreated()
    fun onSurfaceChanged(width: Int, height: Int)
    fun onDrawFrame()
}
