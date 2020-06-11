package com.werb.graduate.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Created by wanbo on 2020/6/4.
 */
@Dao
interface AvatarDao {

    @Query("SELECT * FROM Avatar")
    fun getAll(): List<Avatar>

    @Insert
    fun insert(avatar: Avatar)

    @Delete
    fun delete(avatar: Avatar)
}