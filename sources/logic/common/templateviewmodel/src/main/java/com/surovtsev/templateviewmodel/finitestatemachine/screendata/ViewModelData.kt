package com.surovtsev.templateviewmodel.finitestatemachine.screendata

import com.surovtsev.finitestatemachine.state.data.Data


sealed interface ViewModelData: Data.UserData {
    interface UserData: ViewModelData
}