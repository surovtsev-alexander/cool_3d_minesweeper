package com.surovtsev.utils.viewmodel

sealed class ScreenState<T>(
    val rankingScreenData: T
) {
    class Loading<T>(
        rankingScreenData: T
    ): ScreenState<T>(rankingScreenData)

    class Error<T>(
        rankingScreenData: T,
        val message: String
    ): ScreenState<T>(rankingScreenData)

    class Idle<T>(
        rankingScreenData: T
    ): ScreenState<T>(rankingScreenData)
}
