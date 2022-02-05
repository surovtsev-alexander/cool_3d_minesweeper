package com.surovtsev.gamelogic.utils.utils.gles

import com.surovtsev.core.models.game.cellpointers.PointedCell

interface TextureUpdater {
    fun updateTexture(pointedCell: PointedCell)
}
