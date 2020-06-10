package com.werb.graduate.model

import android.net.Uri
import com.werb.graduate.exts.asyncAction
import com.werb.graduate.exts.syncAction

/**
 * Created by wanbo on 2020/6/3.
 */
object StickersManager {

    var mainTabText = listOf("背景", "人物", "道具")

    private var backgroundStickers = mutableListOf<Sticker>()

    var propsStickers = listOf(
        Sticker(localImageName = "prop1"),
        Sticker(localImageName = "prop2"),
        Sticker(localImageName = "prop3"),
        Sticker(localImageName = "prop4"),
        Sticker(localImageName = "prop5"),
        Sticker(localImageName = "prop6"),
        Sticker(localImageName = "prop7"),
        Sticker(localImageName = "prop8"),
        Sticker(localImageName = "prop9"),
        Sticker(localImageName = "prop10"))

    var boshiHatStickers = listOf(
        Sticker(localImageName = "boshi_hat1"),
        Sticker(localImageName = "boshi_hat2"),
        Sticker(localImageName = "boshi_hat3")
    )

    var shuoshiHatStickers = listOf(
        Sticker(localImageName = "shuoshi_hat1"),
        Sticker(localImageName = "shuoshi_hat2"),
        Sticker(localImageName = "shuoshi_hat3")
    )

    var xueshiHatStickers = listOf(
        Sticker(localImageName = "xueshi_hat1"),
        Sticker(localImageName = "xueshi_hat2"),
        Sticker(localImageName = "xueshi_hat3")
    )

    fun getBackgrounds(block:(List<Sticker>) -> Unit) {
        backgroundStickers = mutableListOf(Sticker(isAddImage = true),
            Sticker(localImageName = "scenery1"),
            Sticker(localImageName = "scenery2"),
            Sticker(localImageName = "scenery3"),
            Sticker(localImageName = "scenery4"))
        AppDatabase.getInstance().asyncAction { database ->
            val backgrounds = database.userDao().getAll().reversed()
            syncAction( {
                val _backgroundStickers = backgrounds.map { Sticker(localImageUri = Uri.parse(it.imageUri)) }
                backgroundStickers.addAll(1, _backgroundStickers)
                block.invoke(backgroundStickers)
            })
        }
    }

    fun addBackground(uri: Uri, success: () -> Unit) {
        backgroundStickers.add(1, Sticker(localImageUri = uri))
        AppDatabase.getInstance().asyncAction { database ->
            database.userDao().insert(Background(imageUri = uri.toString()))
            success()
        }
    }

}