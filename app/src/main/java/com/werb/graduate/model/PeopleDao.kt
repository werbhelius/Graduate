package com.werb.graduate.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * Created by wanbo on 2020/6/4.
 */
@Dao
interface PeopleDao {

    @Query("SELECT * FROM People")
    fun getAll(): List<People>

    @Insert
    fun insert(people: People)

    @Delete
    fun delete(people: People)
}