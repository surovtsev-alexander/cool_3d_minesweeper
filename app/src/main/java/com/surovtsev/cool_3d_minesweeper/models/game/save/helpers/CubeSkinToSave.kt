package com.surovtsev.cool_3d_minesweeper.models.game.save.helpers

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.models.game.config.GameConfig
import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.CubeSkin

class CubeSkinToSave(
    val emptyCells: String,
    val bombs: String,
    val marked: String
) {
    companion object {
        fun createObject(cubeSkin: CubeSkin): CubeSkinToSave {
            val cellCount = cubeSkin.cellCount

            val resArr = { CharArray(cellCount) }
            val xx =
                {tc: Char, fc: Char ->
                    { flag: Boolean ->
                        if (flag) tc else fc
                    }
            }


            val emptyCells = resArr()
            val bombs = resArr()
            val marked = resArr()

            val eXX = xx('e', ' ')
            val bXX = xx('b', ' ')
            val mXX = xx('m', ' ')

            val skins = cubeSkin.skins
            cubeSkin.iterateCubes { xyz ->
                val skin = xyz.getValue(skins)
                val id = xyz.id

                emptyCells[id] = eXX(skin.isEmpty())
                bombs[id] = bXX(skin.isBomb)
                marked[id] = mXX(skin.isMarked())
            }


            return CubeSkinToSave(
                emptyCells.joinToString(""),
                bombs.joinToString(""),
                marked.joinToString("")
            )
        }
    }

    fun applySavedData(cubeSkin: CubeSkin, gameConfig: GameConfig) {

    }
}