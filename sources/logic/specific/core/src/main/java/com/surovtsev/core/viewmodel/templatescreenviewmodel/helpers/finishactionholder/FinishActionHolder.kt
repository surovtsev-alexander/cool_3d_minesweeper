package com.surovtsev.core.viewmodel.templatescreenviewmodel.helpers.finishactionholder

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias FinishAction = () -> Unit

class FinishActionHolder {
    var finishAction: FinishAction? = null

    suspend fun finish() {
        finishAction?.let { fA ->
            withContext(Dispatchers.Main) {
                fA.invoke()
            }
        }
    }
}
