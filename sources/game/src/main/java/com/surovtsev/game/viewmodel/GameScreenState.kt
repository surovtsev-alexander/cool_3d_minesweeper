package com.surovtsev.game.viewmodel

sealed class GameScreenState(
    val screenData: GameScreenData
) {

    class IDLE(
        screenData: GameScreenData
    ): GameScreenState(
        screenData
    )

    class Loading(
        screenData: GameScreenData
    ): GameScreenState(
        screenData
    )

    class Error(
        screenData: GameScreenData,
        val message: String
    ): GameScreenState (
        screenData
    )

    class Paused(
        screenData: GameScreenData
    ): GameScreenState(
        screenData
    )

    class MainMenu(
        screenData: GameScreenData
    ): GameScreenState(
        screenData
    )
}

val GameScreenInitialState = GameScreenState.IDLE(
    GameScreenData.NoData
)
