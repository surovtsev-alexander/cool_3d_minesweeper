package com.surovtsev.utils.dagger.savedstateviewmodelfactory

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

class SavedStateViewModelFactory<T: ViewModel>(
    savedStateRegistryOwner: SavedStateRegistryOwner,
    private val create: (handle: SavedStateHandle) -> T,
): AbstractSavedStateViewModelFactory(savedStateRegistryOwner, null) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        return create.invoke(handle) as T
    }
}