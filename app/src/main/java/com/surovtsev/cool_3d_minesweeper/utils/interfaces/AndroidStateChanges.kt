package com.surovtsev.cool_3d_minesweeper.utils.interfaces

interface IHandlePauseResume {
    fun onPause()
    fun onResume()
}

interface IHandlePauseResumeDestroy:
        IHandlePauseResume
{
    fun onDestroy()
}

