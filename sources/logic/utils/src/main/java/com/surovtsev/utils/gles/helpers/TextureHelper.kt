package com.surovtsev.utils.gles.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils
import com.surovtsev.utils.constants.Constants
import com.surovtsev.utils.loggerconfig.UtilsLoggerConfig
import logcat.LogPriority
import logcat.logcat
import java.nio.ByteBuffer
import java.nio.ByteOrder

object TextureHelper {
    fun deleteTexture(
        textureId: Int
    ) {
        logcat { "deleteTexture: $textureId" }
        val texturesBuffer = ByteBuffer
            .allocateDirect(1 * Constants.BytesPerInt)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
            .put(intArrayOf(textureId))

        texturesBuffer.position(0)

        glDeleteTextures(1, texturesBuffer)
    }

    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureObjectIds = intArrayOf(0)

        glGenTextures(1, textureObjectIds, 0)

        if (textureObjectIds[0] == 0) {
            if (UtilsLoggerConfig.ON) {
                logcat(priority = LogPriority.WARN) { "Could not generate a new OpenGL texture for object." }
            }

            return -1
        }

        val options = BitmapFactory.Options()
        options.inScaled = false

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

        if (bitmap == null) {
            if (UtilsLoggerConfig.ON) {
                logcat(priority = LogPriority.WARN) { "Resource ID $resourceId could not be decoded." }
            }

            glDeleteTextures(1, textureObjectIds, 0)
            return 0
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0])

        glTexParameteri(
            GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
            GL_LINEAR_MIPMAP_LINEAR
        )
        glTexParameteri(
            GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,
            GL_LINEAR
        )

        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)

        glGenerateMipmap(GL_TEXTURE_2D)

        bitmap.recycle()

        glBindTexture(GL_TEXTURE_2D, 0)

        return textureObjectIds[0]
    }

    @Suppress("unused")
    fun loadCubeMap(context: Context, cubeResources: IntArray): Int {
        val textureObjectIds = intArrayOf(0)

        glGenTextures(1, textureObjectIds, 0)

        if (textureObjectIds[0] == 0) {
            if (UtilsLoggerConfig.ON) {
                logcat(priority = LogPriority.WARN) { "Could not generate a new OpenGL texture object." }
            }
            return 0
        }

        val options = BitmapFactory.Options()
        options.inScaled = false

        val cubeBitmaps = Array<Bitmap?>(6) { null }

        for (i in 0 until 6) {
            cubeBitmaps[i] = BitmapFactory.decodeResource(
                context.resources, cubeResources[i], options
            )

            if (cubeBitmaps[i] == null) {
                if (UtilsLoggerConfig.ON) {
                    logcat(priority = LogPriority.WARN) {
                        "Resource ID ${cubeResources[i]} could not be decoded"
                    }
                    glDeleteTextures(1, textureObjectIds, 0)
                    return 0
                }
            }
        }

        glBindTexture(GL_TEXTURE_CUBE_MAP, textureObjectIds[0])

        glTexParameteri(
            GL_TEXTURE_CUBE_MAP,
            GL_TEXTURE_MIN_FILTER, GL_LINEAR
        )
        glTexParameteri(
            GL_TEXTURE_CUBE_MAP,
            GL_TEXTURE_MAG_FILTER, GL_LINEAR
        )

        GLUtils.texImage2D(
            GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0,
            cubeBitmaps[0]!!, 0
        )
        GLUtils.texImage2D(
            GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0,
            cubeBitmaps[1]!!, 0
        )
        GLUtils.texImage2D(
            GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0,
            cubeBitmaps[2]!!, 0
        )
        GLUtils.texImage2D(
            GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0,
            cubeBitmaps[3]!!, 0
        )
        GLUtils.texImage2D(
            GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0,
            cubeBitmaps[4]!!, 0
        )
        GLUtils.texImage2D(
            GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0,
            cubeBitmaps[5]!!, 0
        )

        glBindTexture(GL_TEXTURE_2D, 0)

        cubeBitmaps.forEach { it?.recycle() }

        return textureObjectIds[0]
    }
}