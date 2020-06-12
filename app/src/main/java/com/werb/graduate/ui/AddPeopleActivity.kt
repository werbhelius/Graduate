package com.werb.graduate.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.werb.graduate.adapter.AddPeoplePagerAdapter
import com.werb.graduate.databinding.ActivityAddPeopleBinding
import com.werb.graduate.events.AddBackgroundEvent
import com.werb.graduate.events.ChangeAddPeopleModeEvent
import com.werb.graduate.events.LoadingEvent
import com.werb.graduate.model.StickersManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by wanbo on 2020/6/10.
 */
class AddPeopleActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddPeopleBinding

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

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
        val intent = Intent(this, AddPeopleSettingActivity::class.java)
        startActivity(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadingEvent(event: LoadingEvent) {
        if (event.loading) {
            binding.loadingGroup.visibility = View.VISIBLE
        } else {
            binding.loadingGroup.visibility = View.GONE
        }
    }

}