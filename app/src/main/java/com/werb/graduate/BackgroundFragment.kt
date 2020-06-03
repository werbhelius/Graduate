package com.werb.graduate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.werb.graduate.databinding.FragmentBackgroundBinding
import com.werb.graduate.holder.StickerHolder
import com.werb.graduate.model.StickersManager
import com.werb.library.MoreAdapter

/**
 * Created by wanbo on 2020/6/2.
 */
class BackgroundFragment: Fragment() {

    private var _binding: FragmentBackgroundBinding? = null
    private val binding get() = _binding!!
    private val adapter = MoreAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBackgroundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.list.setHasFixedSize(true)
        binding.list.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        adapter.apply {
            register(StickerHolder::class.java)
            attachTo(binding.list)
        }

        adapter.loadData(StickersManager.backgroundStickers)
    }

}