package com.surovtsev.cool_3d_minesweeper.util

import android.content.Context
import android.content.res.Resources
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.lang.StringBuilder

class TextResourceReader {

    companion object {

        fun readTextFileFromResource(
            context: Context,
            resourceId: Int
        ): String {
            val body = StringBuilder()

            try {

                val inputStream = context.resources.openRawResource(resourceId)
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)

                var nextLine: String? = null

                while (true) {
                    nextLine = bufferedReader.readLine()

                    if (nextLine == null) break

                    body.append(nextLine)
                    body.append('\n')
                }
            } catch (e: IOException) {
                throw RuntimeException("Could not open resource file")
            } catch (nfe: Resources.NotFoundException) {
                throw RuntimeException("Resource file not found", nfe)
            }

            return body.toString()
        }
    }
}