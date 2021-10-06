package com.surovtsev.cool3dminesweeper.utils.gles.interfaces

import com.surovtsev.cool3dminesweeper.models.game.cell_pointers.PointedCell

interface ICanUpdateTexture {
    fun updateTexture(pointedCell: PointedCell)
}
