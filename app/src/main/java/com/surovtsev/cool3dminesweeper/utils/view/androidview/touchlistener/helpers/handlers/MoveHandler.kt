package com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.handlers

import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.receivers.MoveReceiver
import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.receivers.RotationReceiver
import com.surovtsev.cool3dminesweeper.utils.view.androidview.touchlistener.helpers.receivers.ScaleReceiver

interface MoveHandler:
    RotationReceiver,
    ScaleReceiver,
    MoveReceiver
