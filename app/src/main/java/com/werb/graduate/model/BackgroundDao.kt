package com.werb.graduate.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Created by wanbo on 2020/6/4.
 */
@Dao
interface BackgroundDao {

    @Query("SELECT * FROM Background")
    fun getAll(): List<Background>

    @Insert
    fun insert(background: Background)

    @Query("DELETE FROM background WHERE image_uri = :image_uri")
    fun delete(image_uri: String)
}