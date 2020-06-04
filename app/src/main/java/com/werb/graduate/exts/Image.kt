package com.werb.graduate.exts

import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by wanbo on 2020/6/4.
 */

fun Context.getImage(path: String): Int {
    return resources.getIdentifier(path, "drawable", packageName)
}

fun saveBitmap(bitmap: Bitmap, path: String, complete: () -> Unit = {}) {
    val file = File(path)
    var bos: BufferedOutputStream? = null
    try {
        bos = BufferedOutputStream(FileOutputStream(file))
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        bos.flush()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            bos?.close()
        } catch (e: Exception) {
        }
        Handler(Looper.getMainLooper()).post {
            complete.invoke()
        }
    }
}