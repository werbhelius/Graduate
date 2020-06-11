package com.werb.graduate.model

import android.net.Uri
import com.werb.graduate.exts.asyncAction
import com.werb.graduate.exts.syncAction

/**
 * Created by wanbo on 2020/6/3.
 */
object StickersManager {

    var mainTabText = listOf("背景", "人物", "道具")
    var addPeopleTabText = listOf("头像", "服饰", "装饰")

    private var backgroundStickers = mutableListOf<Sticker>()
    private var peopleStickers = mutableListOf<Sticker>()
    private var avatarStickers = mutableListOf<Sticker>()

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
                if (_backgroundStickers.isNotEmpty()) {
                    backgroundStickers.addAll(1, _backgroundStickers)
                }
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

    fun getPeoples(block:(List<Sticker>) -> Unit) {
        peopleStickers = mutableListOf(Sticker(isAddImage = true),
            Sticker(localImageName = "people1"))
        AppDatabase.getInstance().asyncAction { database ->
            val peoples = database.peopleDao().getAll().reversed()
            syncAction( {
                val _peoplesStickers = peoples.map { Sticker(localImageUri = Uri.parse(it.imageUri)) }
                if (_peoplesStickers.isNotEmpty()) {
                    peopleStickers.addAll(1, _peoplesStickers)
                }
                block.invoke(peopleStickers)
            })
        }
    }

    fun addPeople(uri: Uri, success: () -> Unit) {
        peopleStickers.add(1, Sticker(localImageUri = uri))
        AppDatabase.getInstance().asyncAction { database ->
            database.peopleDao().insert(People(imageUri = uri.toString()))
            success()
        }
    }

    fun getAvatars(block:(List<Sticker>) -> Unit) {
        avatarStickers = mutableListOf(Sticker(isAddImage = true),
            Sticker(localImageName = "avatar1"))
        AppDatabase.getInstance().asyncAction { database ->
            val avatars = database.avatarDao().getAll().reversed()
            syncAction( {
                val _avatarStickers = avatars.map { Sticker(localImageUri = Uri.parse(it.imageUri)) }
                if (_avatarStickers.isNotEmpty()) {
                    avatarStickers.addAll(1, _avatarStickers)
                }
                block.invoke(avatarStickers)
            })
        }
    }

    fun addAvatar(uri: Uri, success: () -> Unit) {
        avatarStickers.add(1, Sticker(localImageUri = uri))
        AppDatabase.getInstance().asyncAction { database ->
            database.avatarDao().insert(Avatar(imageUri = uri.toString()))
            success()
        }
    }

}