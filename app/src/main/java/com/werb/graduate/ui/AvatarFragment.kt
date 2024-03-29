package com.werb.graduate.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.werb.azure.Azure
import com.werb.graduate.R
import com.werb.graduate.databinding.FragmentAvatarBinding
import com.werb.graduate.events.AddAvatarToPeopleEvent
import com.werb.graduate.events.AddNewAvatarEvent
import com.werb.graduate.exts.getFileName
import com.werb.graduate.exts.saveBitmapToPng
import com.werb.graduate.holder.StickerHolder
import com.werb.graduate.model.Sticker
import com.werb.graduate.model.StickersManager
import com.werb.library.MoreAdapter
import com.werb.library.action.MoreClickListener
import com.yalantis.ucrop.UCrop
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
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
        EventBus.getDefault().unregister(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        binding.listAvatar.setHasFixedSize(true)
        binding.listAvatar.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        adapter.apply {
            register(StickerHolder::class.java, clickListener = onclick)
            attachTo(binding.listAvatar)
        }

        StickersManager.getAvatars { avatars ->
            adapter.removeAllData()
            adapter.loadData(avatars)
        }
    }

    private val onclick = object : MoreClickListener() {
        override fun onItemClick(view: View, position: Int) {
            val sticker = view.tag as Sticker
            when(view.id) {
                R.id.displayImage -> {
                    if (sticker.isAddImage) {
                        Azure(requireActivity())
                            .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe {
                                if (it) {
                                    openGallery()
                                } else {
                                    Toast.makeText(requireContext(), "请在系统设置开启存储权限后重试", Toast.LENGTH_SHORT).show()
                                }
                            }.request()
                    } else {
                        EventBus.getDefault().post(AddAvatarToPeopleEvent(sticker))
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
                StickersManager.deleteAvatar(sticker) {
                    StickersManager.getAvatars { list ->
                        adapter.removeAllData()
                        adapter.loadData(list)
                    }
                }
            }
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
            }.create().show()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"), PICK_REQUEST
        )
    }

    private fun openMatting(path: String) {
        val intent = Intent(requireActivity(), MattingActivity::class.java)
        intent.putExtra(MattingActivity.originalBitmapPath, path)
        startActivity(intent)
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
                                val option = BitmapFactory.Options()
                                val bmp = BitmapFactory.decodeStream(stream, null , option)
                                val outBmp: Bitmap = Bitmap.createBitmap(option.outWidth, option.outHeight , Bitmap.Config.ARGB_8888)
                                val canvas = Canvas(outBmp)
                                canvas.drawColor(Color.WHITE)
                                val dest = Rect(0, 0, option.outWidth, option.outHeight)
                                val src = Rect(0, 0, option.outWidth, option.outHeight)
                                canvas.drawBitmap(bmp!!, src, dest, null)
                                val outPath = requireContext().cacheDir.absolutePath + "/cache_${UUID.randomUUID()}.png"
                                saveBitmapToPng(outBmp, outPath) {
                                    val path = requireContext().cacheDir.absolutePath + "/cache_${UUID.randomUUID()}.png"
                                    UCrop.of(File(outPath).toUri(), Uri.parse(path))
                                        .withOptions(UCrop.Options().apply {
                                            setCompressionFormat(Bitmap.CompressFormat.PNG)
                                        })
                                        .withAspectRatio(1f, 1f)
                                        .withMaxResultSize(1024, 1024)
                                        .start(requireActivity(), this)
                                }
                            }
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    resultUri?.also { uri ->
                        openMatting(uri.path!!)
                    }
                }
                UCrop.RESULT_ERROR -> {
                    Toast.makeText(requireContext(), "剪裁失败，请重试。", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddNewAvatarEvent(event: AddNewAvatarEvent) {
        StickersManager.getAvatars {
            adapter.removeAllData()
            adapter.loadData(it)
        }
    }

}