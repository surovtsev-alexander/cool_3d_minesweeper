package com.surovtsev.core.viewmodel.templatescreenviewmodel.finitestatemachine.screendata

import com.surovtsev.finitestatemachine.state.data.Data


sealed interface ScreenData: Data.UserData {
    interface UserData: ScreenData
}