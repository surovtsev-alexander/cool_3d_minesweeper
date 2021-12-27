package com.surovtsev.gamelogic.utils.utils.gles

import com.surovtsev.gamelogic.models.game.cellpointers.PointedCell

interface TextureUpdater {
    fun updateTexture(pointedCell: PointedCell)
}
