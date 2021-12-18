package com.surovtsev.core.dagger.viewmodelassistedfactory

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.surovtsev.core.dagger.components.RootComponent

interface ViewModelAssistedFactory<T: ViewModel> {
    fun build(stateHandle: SavedStateHandle, context: Context, rootComponent: RootComponent): T
}
