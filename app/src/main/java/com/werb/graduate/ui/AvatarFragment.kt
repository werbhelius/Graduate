package com.werb.graduate.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.werb.graduate.R
import com.werb.graduate.databinding.FragmentAvatarBinding
import com.werb.graduate.events.LoadingEvent
import com.werb.graduate.exts.*
import com.werb.graduate.holder.StickerHolder
import com.werb.graduate.model.Sticker
import com.werb.graduate.model.StickersManager
import com.werb.graduate.network.ApiClient
import com.werb.library.MoreAdapter
import com.werb.library.action.MoreClickListener
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.IOException
import java.util.*


/**
 * Created by wanbo on 2020/6/9.
 */
class AvatarFragment: Fragment() {

    private var _binding: FragmentAvatarBinding? = null
    private val binding get() = _binding!!
    private val adapter = MoreAdapter()
    private val PICK_REQUEST = 54

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvatarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listAvatar.setHasFixedSize(true)
        binding.listAvatar.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        adapter.apply {
            register(StickerHolder::class.java, clickListener = onclick)
            attachTo(binding.listAvatar)
        }

        StickersManager.getAvatars { avatars ->
            adapter.loadData(avatars)
        }
    }

    private val onclick = object : MoreClickListener() {
        override fun onItemClick(view: View, position: Int) {
            val sticker = view.tag as Sticker
            when(view.id) {
                R.id.displayImage -> {
                    if (sticker.isAddImage) {
                        openGallery()
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
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_REQUEST -> try {
                    val selectedPhotoUri = data!!.data
                    selectedPhotoUri?.also { uri ->
                        uri.getFileName(requireContext()) { name ->
                            val resolver = requireContext().contentResolver
                            resolver.openInputStream(uri).use { stream ->
                                val file = createTemporalFileFrom(requireContext(), stream, name)
                                val path = requireContext().cacheDir.absolutePath + "/cache_$name"
                                UCrop.of(file!!.toUri(), Uri.parse(path))
                                    .withAspectRatio(1f, 1f)
                                    .withMaxResultSize(1024, 1024)
                                    .start(requireActivity(), this)
                            }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    resultUri?.also { uri ->
                        GlobalScope.launch(Dispatchers.Default) {
                            ApiClient.mattingImage(resultUri) { result ->
                                val bitmap = BitmapFactory.decodeStream(result)
                                bitmap?.also {
                                    val dir =
                                        requireContext().filesDir.absolutePath + "/avatars"
                                    if (!File(dir).exists()) {
                                        File(dir).mkdirs()
                                    }
                                    val path = requireContext().filesDir.absolutePath + "/avatars/${UUID.randomUUID()}+.png}"
                                    saveBitmapToPng(bitmap, path) {
                                        StickersManager.addAvatar(File(path).toUri()){
                                            StickersManager.getAvatars { list ->
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
                UCrop.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), "剪裁失败，请重试。", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}