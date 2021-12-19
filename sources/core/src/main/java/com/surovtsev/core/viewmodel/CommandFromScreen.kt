package com.surovtsev.core.viewmodel

interface CommandFromScreen {

    interface CloseError: CommandFromScreen
    interface CloseErrorAndFinish: CloseError

    interface Init: CommandFromScreen

}
