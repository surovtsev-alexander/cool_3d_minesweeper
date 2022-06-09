/*
MIT License

Copyright (c) [2022] [Alexander Surovtsev]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */


package com.surovtsev.gamescreen.viewmodel.helpers.finitestatemachine.eventhandler

import com.surovtsev.finitestatemachine.stateholder.FSMStateFlow
import com.surovtsev.gamelogic.dagger.GameComponent
import com.surovtsev.gamescreen.dagger.GameScreenScope
import com.surovtsev.restartablecoroutinescope.dagger.RestartableCoroutineScopeComponent
import com.surovtsev.timespan.dagger.TimeSpanComponent
import com.surovtsev.touchlistener.dagger.TouchListenerComponent
import com.surovtsev.utils.gles.renderer.GLESRenderer
import javax.inject.Inject


@GameScreenScope
class EventHandlerParameters @Inject constructor(
    val fsmStateFlow: FSMStateFlow,
    val restartableCoroutineScopeComponent: RestartableCoroutineScopeComponent,
    val gLESRenderer: GLESRenderer,
    val gameComponent: GameComponent,
    val timeSpanComponent: TimeSpanComponent,
    val touchListenerComponent: TouchListenerComponent,
)
