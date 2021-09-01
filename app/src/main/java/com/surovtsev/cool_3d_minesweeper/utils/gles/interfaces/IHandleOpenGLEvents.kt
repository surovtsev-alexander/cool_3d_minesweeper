package com.surovtsev.cool_3d_minesweeper.utils.gles.interfaces

interface IHandleOpenGLEvents {
    fun onSurfaceCreated()
    fun onSurfaceChanged(width: Int, height: Int)
    fun onDrawFrame()
}
