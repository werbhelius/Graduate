package com.werb.graduate.model

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.werb.graduate.MyApp

/**
 * Created by wanbo on 2020/6/4.
 */
@Database(entities = [Background::class, People::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): BackgroundDao
    abstract fun peopleDao(): PeopleDao

    companion object {
        private var sInstance: AppDatabase? = null
        private val dbName = "Graduate.db"

        fun getInstance(): AppDatabase {
            if (sInstance == null) {
                synchronized(AppDatabase::class.java) {
                    if (sInstance == null) {
                        sInstance = Room.databaseBuilder(MyApp.instance, AppDatabase::class.java, dbName).build()
                    }
                }
            }
            return sInstance!!
        }
    }

}