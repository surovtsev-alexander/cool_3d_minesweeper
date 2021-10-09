package com.surovtsev.cool3dminesweeper.utils.gles.interfaces

interface OpenGLEventsHandler {
    fun onSurfaceCreated()
    fun onSurfaceChanged(width: Int, height: Int)
    fun onDrawFrame()
}
