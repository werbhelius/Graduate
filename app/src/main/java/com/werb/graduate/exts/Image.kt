package com.werb.graduate.exts

import android.content.Context

/**
 * Created by wanbo on 2020/6/4.
 */

fun Context.getImage(path: String): Int {
    return resources.getIdentifier(path, "drawable", packageName)
}