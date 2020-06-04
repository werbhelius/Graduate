package com.werb.graduate.model

import android.net.Uri

/**
 * Created by wanbo on 2020/6/3.
 */
object StickersManager {

    var backgroundStickers = mutableListOf(Sticker(isAddImage = true), Sticker(localImageName = "scenery1"))
    var charactersStickers = listOf(Sticker(isAddImage = true))

    fun addBackground(uri: Uri) {
        backgroundStickers.add(1, Sticker(localImageUri = uri))
    }

}