package com.surovtsev.finitestatemachine.state.data

sealed interface Data {
    object NoData: Data, InitializationIsNotFinished

    interface UserData: Data
}

interface InitializationIsNotFinished
