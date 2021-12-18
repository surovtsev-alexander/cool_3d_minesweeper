package com.surovtsev.cool3dminesweeper.unused.test

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.surovtsev.core.dagger.components.RootComponent
import com.surovtsev.core.dagger.viewmodelassistedfactory.ViewModelAssistedFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject


class MyViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    @Assisted context: Context,
    @Assisted rootComponent: RootComponent,
) : ViewModel() {

    @AssistedFactory
    interface Factory: ViewModelAssistedFactory<MyViewModel>
}