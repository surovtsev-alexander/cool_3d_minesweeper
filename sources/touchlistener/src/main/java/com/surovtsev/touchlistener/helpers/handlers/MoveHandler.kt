package com.surovtsev.touchlistener.helpers.handlers

import com.surovtsev.touchlistener.helpers.receivers.MoveReceiver
import com.surovtsev.touchlistener.helpers.receivers.RotationReceiver
import com.surovtsev.touchlistener.helpers.receivers.ScaleReceiver

interface MoveHandler:
    RotationReceiver,
    ScaleReceiver,
    MoveReceiver
