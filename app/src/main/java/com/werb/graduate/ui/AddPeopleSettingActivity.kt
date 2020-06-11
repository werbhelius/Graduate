package com.werb.graduate.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.addisonelliott.segmentedbutton.SegmentedButtonGroup.OnPositionChangedListener
import com.werb.graduate.databinding.ActivityAddPeopleSettingBinding
import com.werb.graduate.events.ChangeAddPeopleModeEvent
import com.werb.graduate.model.AddPeopleMode
import org.greenrobot.eventbus.EventBus


/**
 * Created by wanbo on 2020/6/11.
 */
class AddPeopleSettingActivity:  AppCompatActivity(){

    private lateinit var binding: ActivityAddPeopleSettingBinding
    private var  addPeopleMode = AddPeopleMode()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPeopleMode = intent.getSerializableExtra("add_people_mode") as AddPeopleMode
        setupUI()
    }

    private fun setupUI() {
        binding = ActivityAddPeopleSettingBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.xingbieSementedBtnGroup.setPosition(addPeopleMode.xingbie.position, false)
        binding.xueweiSementedBtnGroup.setPosition(addPeopleMode.xuewei.position, false)
        binding.xuekeSementedBtnGroup.setPosition(addPeopleMode.xueke.position, false)

        binding.xingbieSementedBtnGroup.onPositionChangedListener = OnPositionChangedListener {
            addPeopleMode.setXingbie(it)
        }

        binding.xueweiSementedBtnGroup.onPositionChangedListener = OnPositionChangedListener {
            addPeopleMode.setXuewei(it)
        }

        binding.xuekeSementedBtnGroup.onPositionChangedListener = OnPositionChangedListener {
            addPeopleMode.setXueke(it)
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().post(ChangeAddPeopleModeEvent(addPeopleMode))
    }

}