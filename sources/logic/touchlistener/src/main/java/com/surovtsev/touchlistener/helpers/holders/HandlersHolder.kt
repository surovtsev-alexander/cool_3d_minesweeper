package com.surovtsev.touchlistener.helpers.holders

import com.surovtsev.touchlistener.helpers.handlers.MoveHandler
import com.surovtsev.touchlistener.helpers.handlers.TouchHandler

interface HandlersHolder {
    val touchHandler: TouchHandler?
    val moveHandler: MoveHandler?
}
