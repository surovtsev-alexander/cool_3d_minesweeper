package com.surovtsev.cool_3d_minesweeper.gl_helpers.objects.cubes

import com.surovtsev.cool_3d_minesweeper.math.Point3d

data class CubesConfig(val counts: Point3d<Short>,
                       val dimensions: Point3d<Float>,
                       val gaps: Point3d<Float>
)
