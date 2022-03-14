package com.surovtsev.core.viewmodel

import com.surovtsev.finitestatemachine.state.data.Data


sealed interface ScreenData: Data.UserData {
    interface UserData: ScreenData
}