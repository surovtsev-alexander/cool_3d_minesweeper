package com.surovtsev.core.viewmodel

import androidx.lifecycle.LifecycleOwner

typealias HandleScreenLeavingCommandFactory<C> = (owner: LifecycleOwner) -> C

interface CommandFromScreen {
    val setLoadingStateWhileProcessing: Boolean

    interface HandleScreenLeaving: CommandFromScreen {
        val owner: LifecycleOwner
    }

    interface CloseError: CommandFromScreen
    interface CloseErrorAndFinish: CloseError

    interface Init: CommandFromScreen
    interface Finish: CommandFromScreen

    abstract class BaseCommands <T: CommandFromScreen>(
        val init: Init,
        val closeError: CloseError,
        val closeErrorAndFinish: CloseErrorAndFinish,
        val handleScreenLeavingCommandFactory: HandleScreenLeavingCommandFactory<T>,
    )
}
