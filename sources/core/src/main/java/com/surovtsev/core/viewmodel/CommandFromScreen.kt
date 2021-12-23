package com.surovtsev.core.viewmodel

import androidx.lifecycle.LifecycleOwner

typealias HandleScreenLeavingCommandFactory<C> = (owner: LifecycleOwner) -> C

interface CommandFromScreen {
    open class HandleScreenLeaving(val owner: LifecycleOwner): CommandFromScreen

    interface CloseError: CommandFromScreen
    interface CloseErrorAndFinish: CloseError

    interface Init: CommandFromScreen
    interface Finish: CommandFromScreen

    class BaseCommands <T: CommandFromScreen>(
        val init: Init,
        val closeError: CloseError,
        val closeErrorAndFinish: CloseErrorAndFinish,
        val handleScreenLeavingCommandFactory: HandleScreenLeavingCommandFactory<T>,
    )
}
