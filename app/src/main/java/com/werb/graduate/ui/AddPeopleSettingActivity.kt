package com.werb.graduate.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup.OnPositionChangedListener
import com.werb.graduate.MyApp
import com.werb.graduate.databinding.ActivityAddPeopleSettingBinding
import com.werb.graduate.events.ChangeAddPeopleModeEvent
import com.werb.graduate.exts.APP_VERSION_NAME
import com.werb.graduate.model.StickersManager
import org.greenrobot.eventbus.EventBus


/**
 * Created by wanbo on 2020/6/11.
 */
class AddPeopleSettingActivity:  AppCompatActivity(){

    private lateinit var binding: ActivityAddPeopleSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding = ActivityAddPeopleSettingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.returnBtn.setOnClickListener {
            finish()
        }

        binding.xingbieSementedBtnGroup.setPosition(StickersManager.addPeopleMode.xingbie.position, false)
        binding.xueweiSementedBtnGroup.setPosition(StickersManager.addPeopleMode.xuewei.position, false)
        binding.xuekeSementedBtnGroup.setPosition(StickersManager.addPeopleMode.xueke.position, false)

        binding.xingbieSementedBtnGroup.onPositionChangedListener = OnPositionChangedListener {
            StickersManager.addPeopleMode.setXingbie(it)
        }

        binding.xueweiSementedBtnGroup.onPositionChangedListener = OnPositionChangedListener {
            StickersManager.addPeopleMode.setXuewei(it)
        }

        binding.xuekeSementedBtnGroup.onPositionChangedListener = OnPositionChangedListener {
            StickersManager.addPeopleMode.setXueke(it)
        }
        binding.contactUs.setOnClickListener {
            contactUs()
        }
    }

    override fun finish() {
        EventBus.getDefault().post(ChangeAddPeopleModeEvent())
        super.finish()
    }

    private fun contactUs() {
        val content = "\n\n\n\n\n\n\n\n\n\nOS:Android\nPhone Brand:${Build.BRAND}\nSystem Version:${Build.VERSION.RELEASE}\nDevice Model:${Build.MODEL}\nApp Version:${APP_VERSION_NAME}"
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:werbhelius@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "毕业啦——意见&反馈") // 主题
        intent.putExtra(Intent.EXTRA_TEXT, content) // 正文
        startActivity(Intent.createChooser(intent, "请选择邮件类应用"))
    }

}