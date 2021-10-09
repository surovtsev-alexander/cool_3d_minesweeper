package com.surovtsev.cool3dminesweeper.utils.gles.interfaces

import com.surovtsev.cool3dminesweeper.models.game.cellpointers.PointedCell

interface TextureUpdater {
    fun updateTexture(pointedCell: PointedCell)
}
