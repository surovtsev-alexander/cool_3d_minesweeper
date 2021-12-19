package com.surovtsev.core.viewmodel


interface ScreenData {
    interface InitializationIsNotFinished

    object NoData: InitializationIsNotFinished
}