package com.surovtsev.cool3dminesweeper.utils.externalfilewriter

import android.os.Environment
import logcat.LogPriority
import logcat.logcat
import java.io.File
import java.io.FileOutputStream

object ExternalFileWriter {
    fun writeFile(
        fileName: String,
        data: String
    ) {
        try {
            val externalFileName = File(
                Environment.getExternalStorageDirectory(),
                fileName
            )

            logcat { "externalFileName: $externalFileName" }

            val fileOutputStream = FileOutputStream(
                externalFileName
            )
            fileOutputStream.write(
                data.toByteArray()
            )
            fileOutputStream.close()
        } catch (e: Exception) {
            logcat(LogPriority.ERROR) { "error: \'${e.message}\' while writing file to external storage:\n${e.printStackTrace()}" }
            throw e
        }
    }
}