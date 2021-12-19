package com.surovtsev.core.viewmodel

interface CommandFromScreen {
    interface CloseError: CommandFromScreen
    interface Init: CommandFromScreen
}
