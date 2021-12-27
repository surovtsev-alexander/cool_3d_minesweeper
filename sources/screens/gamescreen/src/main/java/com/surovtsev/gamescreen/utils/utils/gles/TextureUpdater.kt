package com.surovtsev.gamescreen.utils.utils.gles

import com.surovtsev.gamescreen.models.game.cellpointers.PointedCell

interface TextureUpdater {
    fun updateTexture(pointedCell: PointedCell)
}
