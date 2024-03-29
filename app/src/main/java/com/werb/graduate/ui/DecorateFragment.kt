package com.werb.graduate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.werb.graduate.R
import com.werb.graduate.databinding.FragmentAvatarBinding
import com.werb.graduate.databinding.FragmentClothBinding
import com.werb.graduate.databinding.FragmentDecorateBinding
import com.werb.graduate.databinding.FragmentPropBinding
import com.werb.graduate.events.AddClothToPeopleEvent
import com.werb.graduate.events.AddDecorateToPeopleEvent
import com.werb.graduate.events.ChangeAddPeopleModeEvent
import com.werb.graduate.holder.StickerHolder
import com.werb.graduate.model.Sticker
import com.werb.graduate.model.StickersManager
import com.werb.library.MoreAdapter
import com.werb.library.action.MoreClickListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by wanbo on 2020/6/9.
 */
class DecorateFragment: Fragment() {

    private var _binding: FragmentDecorateBinding? = null
    private val binding get() = _binding!!
    private val adapter = MoreAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDecorateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        EventBus.getDefault().unregister(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        binding.listDecorate.setHasFixedSize(true)
        binding.listDecorate.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        adapter.apply {
            register(StickerHolder::class.java, clickListener = onclick)
            attachTo(binding.listDecorate)
        }

        StickersManager.getHat {
            adapter.loadData(it)
        }
    }

    private val onclick = object : MoreClickListener() {
        override fun onItemClick(view: View, position: Int) {
            val sticker = view.tag as Sticker
            when(view.id) {
                R.id.displayImage -> {
                    EventBus.getDefault().post(AddDecorateToPeopleEvent(sticker))
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onChangeAddPeopleModeEvent(event: ChangeAddPeopleModeEvent) {
        adapter.removeAllData()
        StickersManager.getHat {
            adapter.loadData(it)
        }
    }

}