package com.surovtsev.touchlistener.helpers.holders

import com.surovtsev.touchlistener.dagger.TouchListenerScope
import com.surovtsev.touchlistener.helpers.handlers.MoveHandler
import com.surovtsev.touchlistener.helpers.handlers.TouchHandler
import javax.inject.Inject

@TouchListenerScope
class HandlersHolderImp @Inject constructor(
): HandlersHolder {
    override var touchHandler: TouchHandler? = null
    override var moveHandler: MoveHandler? = null
}