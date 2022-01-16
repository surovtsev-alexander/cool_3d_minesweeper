package com.surovtsev.core.viewmodel

import com.surovtsev.finitestatemachine.state.data.Data


interface ScreenData: Data {
    interface InitializationIsNotFinished: ScreenData

    interface NoData: InitializationIsNotFinished
}