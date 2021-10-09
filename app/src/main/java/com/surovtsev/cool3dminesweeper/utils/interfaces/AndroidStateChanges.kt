package com.surovtsev.cool3dminesweeper.utils.interfaces

interface PauseResumeHandler {
    fun onPause()
    fun onResume()
}

interface PauseResumeDestroyHandler:
        PauseResumeHandler
{
    fun onDestroy()
}

interface PauseResumeDestroyKeyDownHandler:
        PauseResumeDestroyHandler
{
    fun onKeyDown(keyCode: Int): Boolean
}

