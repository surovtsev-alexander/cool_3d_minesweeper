package com.surovtsev.game.utils.utils.gles.interfaces

import com.surovtsev.game.models.game.cellpointers.PointedCell

interface TextureUpdater {
    fun updateTexture(pointedCell: PointedCell)
}
