package com.werb.graduate.exts

import android.R.attr
import android.app.Activity
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
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

fun getBitmapFromView(view: View, activity: Activity, callback: (Bitmap) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        activity.window?.let { window ->
            val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
            val locationOfViewInWindow = IntArray(2)
            view.getLocationInWindow(locationOfViewInWindow)
            try {
                PixelCopy.request(window,
                    Rect(
                        locationOfViewInWindow[0],
                        locationOfViewInWindow[1],
                        locationOfViewInWindow[0] + view.width,
                        locationOfViewInWindow[1] + view.height
                    ), bitmap, { copyResult ->
                        if (copyResult == PixelCopy.SUCCESS) {
                            callback(bitmap)
                        }
                    }, Handler()
                )
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
    } else {
        view.isDrawingCacheEnabled = true
        val bmp = view.drawingCache
        callback(bmp)
    }
}