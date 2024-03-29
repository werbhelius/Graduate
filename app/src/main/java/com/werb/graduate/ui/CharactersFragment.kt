package com.werb.graduate.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.werb.graduate.R
import com.werb.graduate.databinding.FragmentCharactersBinding
import com.werb.graduate.events.AddBackgroundEvent
import com.werb.graduate.events.AddNewAvatarEvent
import com.werb.graduate.events.AddPeopleToBgEvent
import com.werb.graduate.events.AddPeopleToListEvent
import com.werb.graduate.exts.syncAction
import com.werb.graduate.holder.StickerHolder
import com.werb.graduate.model.Sticker
import com.werb.graduate.model.StickersManager
import com.werb.library.MoreAdapter
import com.werb.library.action.MoreClickListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by wanbo on 2020/6/2.
 */
class CharactersFragment: Fragment() {

    private var _binding: FragmentCharactersBinding? = null
    private val binding get() = _binding!!
    private val adapter = MoreAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCharactersBinding.inflate(inflater, container, false)
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
        binding.listPeople.setHasFixedSize(true)
        binding.listPeople.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        adapter.apply {
            register(StickerHolder::class.java, clickListener = onclick)
            attachTo(binding.listPeople)
        }

        StickersManager.getPeoples { peoples ->
            adapter.loadData(peoples)
        }
    }

    private val onclick = object : MoreClickListener() {
        override fun onItemClick(view: View, position: Int) {
            val sticker = view.tag as Sticker
            when(view.id) {
                R.id.displayImage -> {
                    if (sticker.isAddImage) {
                        openAddPeople()
                    } else {
                        EventBus.getDefault().post(AddPeopleToBgEvent(sticker))
                    }
                }
            }
        }

        override fun onItemLongClick(view: View, position: Int): Boolean {
            val sticker = view.tag as Sticker
            when(view.id) {
                R.id.displayImage -> {
                    sticker.localImageUri?.also {
                        deleteLocalFile(sticker)
                    }
                }
            }
            return true
        }
    }

    private fun deleteLocalFile(sticker: Sticker) {
        AlertDialog.Builder(requireContext())
            .setMessage("删除这张图片？")
            .setPositiveButton("确定"
            ) { dialog, which ->
                StickersManager.deletePeople(sticker) {
                    StickersManager.getPeoples { list ->
                        adapter.removeAllData()
                        adapter.loadData(list)
                    }
                }
            }
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }.create().show()
    }

    private fun openAddPeople() {
        val intent = Intent(requireContext(), AddPeopleActivity::class.java)
        startActivity(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddPeopleListEvent(event: AddPeopleToListEvent) {
        StickersManager.getPeoples {
            adapter.removeAllData()
            adapter.loadData(it)
        }
    }

}