package com.werb.graduate.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.werb.graduate.R
import com.werb.graduate.exts.syncAction

/**
 * Created by wanbo on 2020/6/16.
 */
class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        syncAction({
            openMain()
            finish()
        }, 500)
    }

    private fun openMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}