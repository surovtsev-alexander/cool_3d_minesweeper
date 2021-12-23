package com.surovtsev.cool3dminesweeper.unused.test

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.surovtsev.cool3dminesweeper.dagger.AppScope
import javax.inject.Inject

@AppScope
class DataFetcher @Inject constructor()

class Config(
    val message: String
)

interface TestViewModelAssistedFactory<T: ViewModel> {
    fun create(savedStateHandle: SavedStateHandle): T
}

class TestClass constructor(
    val savedStateHandle: SavedStateHandle,
    context: Context,
): ViewModel() {

//    @AssistedFactory
//    interface Factory: TestViewModelAssistedFactory<TestClass>
}