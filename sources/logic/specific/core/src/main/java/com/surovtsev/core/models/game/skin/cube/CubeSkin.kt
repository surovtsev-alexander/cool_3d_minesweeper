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


package com.surovtsev.core.models.game.skin.cube

import com.surovtsev.core.models.game.cellpointers.CellIndex
import com.surovtsev.core.models.game.cellpointers.Range3D
import com.surovtsev.core.models.game.cellpointers.PointedCell
import com.surovtsev.core.models.game.skin.cube.cell.CellSkin

class CubeSkin constructor(
    cellsRange: Range3D,
) {
    val skins: List<List<List<CellSkin>>> = cellsRange.create3DList { CellSkin() }

    val skinsWithIndexes = cellsRange.createObjWithCellIndexList(skins)

    fun getPointedCell(p: CellIndex) =
        PointedCell(
            p,
            p.getValue(skins)
        )
}
