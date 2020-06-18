package com.werb.graduate.model

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.werb.graduate.MyApp


/**
 * Created by wanbo on 2020/6/18.
 */
object PreferencesStore {

    private val mSharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(MyApp.instance)

    private val SHOW_SETTING = "show_setting"

    var showSetting: Boolean
        get() = mSharedPreferences.getBoolean(SHOW_SETTING, false)
        set(value) {
            boolean(SHOW_SETTING, value)
        }

    private fun boolean(key: String, value: Boolean): Boolean {
        val editor = mSharedPreferences.edit()
        editor.putBoolean(key, value)
        return editor.commit()
    }

}