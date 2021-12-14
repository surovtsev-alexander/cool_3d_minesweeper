package com.surovtsev.utils.viewmodel

sealed class ScreenState<T>(
    val screenData: T
) {
    class Loading<T>(
        screenData: T
    ): ScreenState<T>(screenData)

    class Error<T>(
        screenData: T,
        val message: String
    ): ScreenState<T>(screenData)

    class Idle<T>(
        screenData: T
    ): ScreenState<T>(screenData)
}
