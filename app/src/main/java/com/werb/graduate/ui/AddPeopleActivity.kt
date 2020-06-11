package com.werb.graduate.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.werb.graduate.adapter.AddPeoplePagerAdapter
import com.werb.graduate.databinding.ActivityAddPeopleBinding
import com.werb.graduate.events.ChangeAddPeopleModeEvent
import com.werb.graduate.model.AddPeopleMode
import com.werb.graduate.model.StickersManager
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by wanbo on 2020/6/10.
 */
class AddPeopleActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddPeopleBinding
    private var addPeopleMode = AddPeopleMode()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupViewPager()
        setupTab()
    }

    private fun setupUI() {
        binding = ActivityAddPeopleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.settingBtn.setOnClickListener {
            openAddPeople()
        }
    }

    private fun setupTab() {
        binding.tabLayout.setSelectedTabIndicatorHeight(0)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = StickersManager.addPeopleTabText[position]
        }.attach()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter =
            AddPeoplePagerAdapter(this)
    }

    private fun openAddPeople() {
        val intent = Intent(this, AddPeopleSettingActivity::class.java).apply {
            putExtra("add_people_mode", addPeopleMode)
        }
        startActivity(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeAddPeopleModeEvent(event: ChangeAddPeopleModeEvent) {
        addPeopleMode = event.addPeopleMode
        binding.viewPager.adapter?.notifyDataSetChanged()
    }

}