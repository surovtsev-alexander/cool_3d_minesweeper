package com.surovtsev.gamelogic.utils.utils.gles

import com.surovtsev.gamestate.models.game.cellpointers.PointedCell

interface TextureUpdater {
    fun updateTexture(pointedCell: PointedCell)
}
