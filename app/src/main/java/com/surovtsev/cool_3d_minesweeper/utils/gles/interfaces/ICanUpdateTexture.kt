package com.surovtsev.cool_3d_minesweeper.utils.gles.interfaces

import com.surovtsev.cool_3d_minesweeper.models.game.cube.cells.cell_pointers.PointedCell

interface ICanUpdateTexture {
    fun updateTexture(pointedCell: PointedCell)
}
