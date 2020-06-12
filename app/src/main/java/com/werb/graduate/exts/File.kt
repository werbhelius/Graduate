package com.werb.graduate.exts

import android.content.Context
import java.io.*

/**
 * Created by wanbo on 2020/6/12.
 */


fun createTemporalFileFrom(
    context: Context,
    inputStream: InputStream?,
    fileName: String
): File? {
    var targetFile: File? = null
    if (inputStream != null) {
        var read: Int
        val buffer = ByteArray(8 * 1024)
        targetFile = File(context.cacheDir, fileName)
        if (targetFile.exists()) {
            targetFile.delete()
        }
        val outputStream: OutputStream = FileOutputStream(targetFile)
        while (inputStream.read(buffer).also { read = it } != -1) {
            outputStream.write(buffer, 0, read)
        }
        outputStream.flush()
        try {
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return targetFile
}