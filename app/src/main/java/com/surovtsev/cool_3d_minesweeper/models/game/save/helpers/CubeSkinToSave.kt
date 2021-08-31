package com.surovtsev.cool_3d_minesweeper.models.game.save.helpers

import android.util.Log
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.GameLogic
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.game_logic.helpers.NeighboursCalculator
import com.surovtsev.cool_3d_minesweeper.controllers.minesweeper.scene.texture_coordinates_helper.TextureCoordinatesHelper
import com.surovtsev.cool_3d_minesweeper.models.game.cell_pointers.CellIndex
import com.surovtsev.cool_3d_minesweeper.models.game.skin.cube.CubeSkin

class CubeSkinToSave(
    private val bombs: String,
    private val marked: String,
    private val closed: String,
    private val empty: String,
) {
    companion object {
        const val SpaceChar = ' '

        fun createObject(cubeSkin: CubeSkin): CubeSkinToSave {
            val cellCount = cubeSkin.cellCount

            val resArr = { CharArray(cellCount) }
            val xx =
                {tc: Char, fc: Char ->
                    { flag: Boolean ->
                        if (flag) tc else fc
                    }
            }


            val bombs = resArr()
            val marked = resArr()
            val closed = resArr()
            val empty = resArr()

            val bXX = xx('b', SpaceChar)
            val mXX = xx('m', SpaceChar)
            val oXX = xx('c', SpaceChar)
            val eXX = xx('e', SpaceChar)

            val skins = cubeSkin.skins
            cubeSkin.iterateCubes { xyz ->
                val skin = xyz.getValue(skins)
                val id = xyz.id

                bombs[id] = bXX(skin.isBomb)
                marked[id] = mXX(skin.isMarked())
                closed[id] = oXX(skin.isClosed())
                empty[id] = eXX(skin.isEmpty())
            }


            return CubeSkinToSave(
                bombs.joinToString(""),
                marked.joinToString(""),
                closed.joinToString(""),
                empty.joinToString(""),
            )
        }
    }

    fun applySavedData(
        cubeSkin: CubeSkin,
        gameLogic: GameLogic) {

        val bombsList = mutableListOf<CellIndex>()

        val skins = cubeSkin.skins
        cubeSkin.iterateCubes { xyz ->
            val skin = xyz.getValue(skins)
            val id = xyz.id

            if (bombs[id] != SpaceChar) {
                skin.isBomb = true
                bombsList.add(xyz)
            }
        }

        Log.d("TEST+++", "bombs:\n$bombs")
        Log.d("TEST+++", "marked:\n$marked")
        Log.d("TEST+++", "closed:\n$closed")
        Log.d("TEST+++", "empty:\n$empty")

        Log.d("TEST+++", "bombsList.count ${bombsList.count()}")
        NeighboursCalculator.fillNeighbours(cubeSkin, bombsList)

        var closedBombs = bombsList.count()
        cubeSkin.iterateCubes { xyz ->
            val skin = xyz.getValue(skins)
            val id = xyz.id

            if (marked[id] != SpaceChar) {
                skin.setTexture(TextureCoordinatesHelper.TextureType.MARKED)
            } else {
                if (closed[id] == SpaceChar) {
                    if (skin.isBomb) {
                        skin.setTexture(TextureCoordinatesHelper.TextureType.EXPLODED_BOMB)
                    } else {
                        skin.setNumbers()
                    }
                }
                if (empty[id] != SpaceChar) {
                    if (skin.isBomb) {
                        closedBombs--
                    }
                    skin.setTexture(TextureCoordinatesHelper.TextureType.EMPTY)
                }
            }
        }

        gameLogic.setBombsLeft(closedBombs)
    }
}