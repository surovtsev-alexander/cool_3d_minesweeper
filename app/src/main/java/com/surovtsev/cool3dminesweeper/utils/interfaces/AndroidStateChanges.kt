package com.surovtsev.cool3dminesweeper.utils.interfaces

interface IHandlePauseResume {
    fun onPause()
    fun onResume()
}

interface IHandlePauseResumeDestroy:
        IHandlePauseResume
{
    fun onDestroy()
}

interface IHandlePauseResumeDestroyKeyDown:
        IHandlePauseResumeDestroy
{
    fun onKeyDown(keyCode: Int): Boolean
}

