package com.surovtsev.cool_3d_minesweeper.models.game.config

data class GameSettings(
    val settingsMap: Map<String, Int>
    ) {
    companion object {
        const val xCount = "x count"
        const val yCount = "y count"
        const val zCount = "z count"
        const val bombsPercentage = "bombs percentage"

        private val defaultValues = mapOf<String, Int>(
            xCount to 12,
            yCount to 12,
            zCount to 12,
            bombsPercentage to 20
        )

        fun createObject(part: GameSettings?): GameSettings {
            if (part == null) {
                return GameSettings(defaultValues)
            }

            val x = defaultValues.map { (k, v) ->
                k to if (part.settingsMap.containsKey(k)) {
                    part.settingsMap[k]!!
                } else {
                    v
                }
            }.toMap()

            return GameSettings(x)
        }
    }
}

