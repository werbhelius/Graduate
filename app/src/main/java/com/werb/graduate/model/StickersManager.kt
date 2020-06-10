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
    private var charactersStickers = mutableListOf<Sticker>()

    var decorationsStickers = listOf(
        Sticker(localImageName = "decoration1"),
        Sticker(localImageName = "decoration2"),
        Sticker(localImageName = "decoration3"),
        Sticker(localImageName = "decoration4"),
        Sticker(localImageName = "decoration5"),
        Sticker(localImageName = "decoration6"),
        Sticker(localImageName = "decoration7"),
        Sticker(localImageName = "decoration8"),
        Sticker(localImageName = "decoration9"))

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