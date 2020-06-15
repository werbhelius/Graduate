package com.werb.graduate.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.werb.graduate.R
import com.werb.graduate.databinding.FragmentBackgroundBinding
import com.werb.graduate.events.AddBackgroundEvent
import com.werb.graduate.exts.getFileName
import com.werb.graduate.exts.rotateImageIfRequired
import com.werb.graduate.exts.saveBitmap
import com.werb.graduate.exts.syncAction
import com.werb.graduate.holder.StickerHolder
import com.werb.graduate.model.Sticker
import com.werb.graduate.model.StickersManager
import com.werb.library.MoreAdapter
import com.werb.library.action.MoreClickListener
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.IOException


/**
 * Created by wanbo on 2020/6/2.
 */
class BackgroundFragment: Fragment() {

    private var _binding: FragmentBackgroundBinding? = null
    private val binding get() = _binding!!
    private val adapter = MoreAdapter()
    private val PICK_REQUEST = 53


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
            register(StickerHolder::class.java, clickListener = onclick)
            attachTo(binding.list)
        }

        StickersManager.getBackgrounds { backgrounds ->
            adapter.loadData(backgrounds)
            syncAction({
                if (backgrounds.isNotEmpty()) {
                    EventBus.getDefault().post(AddBackgroundEvent(backgrounds[1]))
                }
            })
        }
    }

    private val onclick = object : MoreClickListener() {
        override fun onItemClick(view: View, position: Int) {
            val sticker = view.tag as Sticker
            when(view.id) {
                R.id.displayImage -> {
                    if (sticker.isAddImage) {
                        openGallery()
                    } else {
                        EventBus.getDefault().post(AddBackgroundEvent(sticker))
                    }
                }
            }
        }
    }

    fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"), PICK_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_REQUEST -> try {
                    val selectedPhotoUri = data!!.data
                    selectedPhotoUri?.also { uri ->
                        uri.getFileName(requireContext()) {
                            val dir = requireContext().filesDir.absolutePath + "/backgrounds"
                            if (!File(dir).exists()) {
                                File(dir).mkdirs()
                            }
                            val path = requireContext().filesDir.absolutePath + "/backgrounds/$it"
                            if (!File(path).exists()) {
                                val resolver = requireContext().contentResolver
                                resolver.openInputStream(uri).use { stream ->
                                    val bmp = resolver.openInputStream(uri).use { bmpIn ->
                                        BitmapFactory.decodeStream(bmpIn)
                                    }
                                    val bitmap = rotateImageIfRequired(bmp,
                                        stream!!
                                    )
                                    bitmap?.also {
                                        saveBitmap(bitmap, path) {
                                            StickersManager.addBackground(File(path).toUri()){
                                                StickersManager.getBackgrounds { list ->
                                                    adapter.removeAllData()
                                                    adapter.loadData(list)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

}