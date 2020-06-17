package com.werb.graduate

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport

/**
 * Created by wanbo on 2020/6/4.
 */
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        CrashReport.initCrashReport(applicationContext, "a46ffd9041", false)
    }

    companion object {
        lateinit var instance: MyApp
        val AUTHORITY = "com.werb.graduate.fileprovider"
    }

}