package com.surovtsev.utils.time.elapsedformatter

import android.text.format.DateUtils

object ElapsedFormatter {
    fun formatElapsedMillis(
        elapsed: Long
    ): String {
        return DateUtils.formatElapsedTime(
            elapsed / 1000
        )
    }
}
