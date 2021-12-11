package com.surovtsev.touchlistener.helpers.receivers

import com.surovtsev.touchlistener.helpers.holders.MovementHolder
import com.surovtsev.core.utils.statehelpers.Releasable
import glm_.vec2.Vec2

interface TouchReceiver: TouchListenerReceiver, Releasable {
    fun down(pos: Vec2, movementHolderSaver: MovementHolder)
    fun up()
}
