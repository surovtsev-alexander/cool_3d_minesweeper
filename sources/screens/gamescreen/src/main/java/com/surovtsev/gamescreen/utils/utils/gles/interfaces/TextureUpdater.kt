package com.surovtsev.gamescreen.utils.utils.gles.interfaces

import com.surovtsev.gamescreen.models.game.cellpointers.PointedCell

interface TextureUpdater {
    fun updateTexture(pointedCell: PointedCell)
}
