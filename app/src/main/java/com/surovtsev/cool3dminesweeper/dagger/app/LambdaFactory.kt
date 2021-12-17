package com.surovtsev.cool3dminesweeper.dagger.app

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.surovtsev.cool3dminesweeper.test.TestViewModelAssistedFactory
import com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel.ViewModelAssistedFactory

class LambdaFactory<T: ViewModel>(
//    private val viewModelFactory: TestViewModelAssistedFactory<T>,
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