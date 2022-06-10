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


package com.surovtsev.gamestate.logic.models.game.save

import com.surovtsev.gamestate.logic.GameState
import com.surovtsev.core.models.game.config.GameConfig
import com.surovtsev.gamestate.logic.models.game.save.helpers.CameraInfoToSave
import com.surovtsev.gamestate.logic.models.game.save.helpers.CubeSkinToSave
import com.surovtsev.gamestate.logic.models.game.save.helpers.GameLogicToSave
import com.surovtsev.utils.timers.async.AsyncTimeSpan

class Save(
    val gameConfig: GameConfig,
    val cameraInfoToSave: CameraInfoToSave,
    val gameLogicToSave: GameLogicToSave,
    val cubeSkinToSave: CubeSkinToSave,
) {
    companion object {
        fun createObject(
            gameState: GameState,
            asyncTimeSpan: AsyncTimeSpan,
        ): Save {
            return Save(
                gameState.gameConfig,
                CameraInfoToSave.createObject(gameState.cameraInfo),
                GameLogicToSave.createObject(gameState, asyncTimeSpan),
                CubeSkinToSave.createObject(gameState.gameConfig, gameState.cubeInfo.cubeSkin)
            )
        }
    }
}