package com.werb.graduate

import android.app.Application

/**
 * Created by wanbo on 2020/6/4.
 */
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MyApp
    }

}