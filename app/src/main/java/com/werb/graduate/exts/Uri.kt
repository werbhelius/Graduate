package com.werb.graduate.exts

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns

/**
 * Created by wanbo on 2020/6/4.
 */

fun Uri.getFileName(context: Context, block:(String) -> Unit) {
    let { returnUri ->
        context.contentResolver.query(returnUri, null, null, null, null)
    }?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        val fileName = cursor.getString(nameIndex)
        block.invoke(fileName)
    }

}

fun Uri.getFilePath(context: Context, block:(String) -> Unit) {
    let { returnUri ->
        context.contentResolver.query(returnUri, null, null, null, null)
    }?.use { cursor ->
        val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst()
        val fileName = cursor.getString(nameIndex)
        block.invoke(fileName)
    }

}