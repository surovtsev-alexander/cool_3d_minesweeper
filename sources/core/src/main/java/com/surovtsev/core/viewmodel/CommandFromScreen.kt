package com.surovtsev.core.viewmodel

import androidx.lifecycle.LifecycleOwner

interface CommandFromScreen {
    open class HandleScreenLeaving(val owner: LifecycleOwner): CommandFromScreen

    interface CloseError: CommandFromScreen
    interface CloseErrorAndFinish: CloseError

    interface Init: CommandFromScreen

}
