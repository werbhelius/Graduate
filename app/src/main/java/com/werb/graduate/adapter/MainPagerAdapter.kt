package com.werb.graduate.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.werb.graduate.ui.BackgroundFragment
import com.werb.graduate.ui.CharactersFragment
import com.werb.graduate.ui.PropFragment

/**
 * Created by wanbo on 2020/6/2.
 */
class MainPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                BackgroundFragment()
            }
            1 -> {
                CharactersFragment()
            }
            2 -> {
                PropFragment()
            }
            else -> Fragment()
        }
    }
}