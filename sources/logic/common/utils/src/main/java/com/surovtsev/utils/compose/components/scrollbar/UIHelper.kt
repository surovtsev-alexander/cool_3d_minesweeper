package com.surovtsev.utils.compose.components.scrollbar

import android.util.DisplayMetrics
import android.util.TypedValue

object UIHelper {
    fun calculateDIPCoefficient(
        displayMetrics: DisplayMetrics,
    ): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        1f,
        displayMetrics
    )
}
