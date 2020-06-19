package com.werb.graduate.exts

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.werb.graduate.MyApp
import com.werb.graduate.R
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

fun File.mediaScan() {
    MediaScannerConnection.scanFile(MyApp.instance, arrayOf<String>(this.absolutePath), null) { path, uri ->
        Log.v("MediaScanWork", "file $path was scanned seccessfully: $uri")
    }
}

val Context.saveDir: File
    get() {
        val dir = File(Environment.getExternalStorageDirectory().absolutePath + "/${resources.getString(
            R.string.app_name)}")
        if (!dir.exists()) {
            dir.mkdir()
        }
        return dir
    }

fun saveBitmapMain(bitmap: Bitmap, path: String, complete: () -> Unit = {}) {
    val file = File(path)
    var bos: BufferedOutputStream? = null
    try {
        bos = BufferedOutputStream(FileOutputStream(file))
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos)
        bos.flush()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            bos?.close()
        } catch (e: Exception) {
        }
        Handler(Looper.getMainLooper()).post {
            file.mediaScan()
            complete.invoke()
        }
    }
}