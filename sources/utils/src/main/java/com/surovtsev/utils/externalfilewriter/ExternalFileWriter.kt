package com.surovtsev.utils.externalfilewriter

import android.os.Environment
import android.util.Log
import logcat.LogPriority
import logcat.logcat
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

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
            val outputStreamWriter = OutputStreamWriter(
                fileOutputStream
            )
            outputStreamWriter.write(data)
            outputStreamWriter.close()
            fileOutputStream.close()
        } catch (e: Exception) {
            Log.e("ExternalFileWriter", "error: \'${e.message}\' while writing file to external storage:\n${e.printStackTrace()}")
            logcat(LogPriority.ERROR) { "error: \'${e.message}\' while writing file to external storage:\n${e.printStackTrace()}" }

            throw e
        }
    }
}