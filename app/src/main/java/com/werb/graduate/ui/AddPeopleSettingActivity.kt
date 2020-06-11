package com.werb.graduate.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup.OnPositionChangedListener
import com.werb.graduate.databinding.ActivityAddPeopleSettingBinding
import com.werb.graduate.events.ChangeAddPeopleModeEvent
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
    }

    override fun finish() {
        EventBus.getDefault().post(ChangeAddPeopleModeEvent())
        super.finish()
    }

}