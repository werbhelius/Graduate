package com.werb.graduate.exts

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import androidx.exifinterface.media.ExifInterface
import java.io.*


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

fun saveBitmapToPng(bitmap: Bitmap, path: String, complete: () -> Unit = {}) {
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
            complete.invoke()
        }
    }
}

@Throws(IOException::class)
fun rotateImageIfRequired(img: Bitmap, inputStream: InputStream): Bitmap? {
    val ei = ExifInterface(inputStream)
    val orientation: Int =
        ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
        else -> img
    }
}

fun rotateImage(img: Bitmap, degree: Int): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(degree.toFloat())
    val rotatedImg =
        Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    img.recycle()
    return rotatedImg
}