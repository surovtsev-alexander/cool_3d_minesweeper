package com.surovtsev.cool_3d_minesweeper.models.game.config

data class GameSettingsMap(
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

        private val paramNames = arrayOf(
            xCount,
            yCount,
            zCount,
            bombsPercentage
        )

        private val dimParamsCount = 3

        val borders = (
                paramNames.take(dimParamsCount).map {
                    it to (3 to 25)
                } + paramNames.drop(dimParamsCount).map {
                    it to (10 to 90)
                }).toMap()

        fun createObject(part: GameSettingsMap?): GameSettingsMap {
            if (part == null) {
                return GameSettingsMap(defaultValues)
            }

            val x = defaultValues.map { (k, v) ->
                k to if (part.settingsMap.containsKey(k)) {
                    part.settingsMap[k]!!
                } else {
                    v
                }
            }.toMap()

            return GameSettingsMap(x)
        }
    }
}

