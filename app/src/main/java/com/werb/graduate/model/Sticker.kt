package com.werb.graduate.model

import android.net.Uri

/**
 * Created by wanbo on 2020/6/3.
 */
data class Sticker(var localImageName: String = "", var localImageUri: Uri? = null, var isAddImage: Boolean = false)