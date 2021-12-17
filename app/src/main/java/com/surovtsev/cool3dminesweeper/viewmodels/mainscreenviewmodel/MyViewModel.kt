package com.surovtsev.cool3dminesweeper.viewmodels.mainscreenviewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class MyViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    @Assisted private val screenId: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun build(stateHandle: SavedStateHandle, screenId: String): MyViewModel
    }
}