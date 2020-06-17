package com.werb.graduate.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.android.material.tabs.TabLayoutMediator
import com.werb.azure.Azure
import com.werb.graduate.adapter.AddPeoplePagerAdapter
import com.werb.graduate.databinding.ActivityAddPeopleBinding
import com.werb.graduate.events.*
import com.werb.graduate.exts.getImage
import com.werb.graduate.exts.saveBitmapToPng
import com.werb.graduate.model.StickersManager
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.R
import ja.burhanrashid52.photoeditor.ViewType
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*


/**
 * Created by wanbo on 2020/6/10.
 */
class AddPeopleActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddPeopleBinding
    private lateinit var mPhotoEditor: PhotoEditor
    private var hatImageView: ImageView? = null
    private var clothImageView: ImageView? = null
    private var avatarImageView: ImageView? = null
    private var uiChange = false

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
        setDefaultView()
    }

    private fun setupUI() {
        binding = ActivityAddPeopleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.settingBtn.setOnClickListener {
            openAddPeople()
        }
        binding.restoreBtn.setOnClickListener {
            if (uiChange) {
                mPhotoEditor.clearAllViews()
                setDefaultView()
            }
        }
        binding.root.setOnClickListener {
            mPhotoEditor.removeAllSelected()
        }
        binding.photoEditorView.setOnClickListener {
            mPhotoEditor.removeAllSelected()
        }
        binding.returnBtn.setOnClickListener {
            if (uiChange) {
                AlertDialog.Builder(this)
                    .setMessage("放弃当前编辑操作？")
                    .setPositiveButton("确定"
                    ) { dialog, which ->
                        finish()
                    }
                    .setNegativeButton("取消") { dialog, which ->
                        dialog.dismiss()
                    }.create().show()
            } else {
                finish()
            }
        }
        binding.saveBtn.setOnClickListener {
            Azure(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    if (it) {
                        mPhotoEditor.removeAllSelected()
                        saveToFinish()
                    } else {
                        Toast.makeText(this, "请在系统设置开启存储权限后重试", Toast.LENGTH_SHORT).show()
                    }
                }.request()
        }
        mPhotoEditor = PhotoEditor.Builder(this, binding.photoEditorView).build()
    }

    override fun onBackPressed() {
        if (uiChange) {
            AlertDialog.Builder(this)
                .setMessage("放弃当前编辑操作？")
                .setPositiveButton("确定"
                ) { dialog, which ->
                    finish()
                }
                .setNegativeButton("取消") { dialog, which ->
                    dialog.dismiss()
                }.create().show()
        } else {
            finish()
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

    private fun setDefaultView() {

        // 服饰
        StickersManager.getCloths {
            val sticker = it.first()
            val bmp = BitmapFactory.decodeResource(resources, getImage(sticker.localImageName))
            val rootView = mPhotoEditor.addImageWithReturn(bmp)
            clothImageView = rootView.findViewById(R.id.imgPhotoEditorImage)
            clothImageView?.also {
                mPhotoEditor.addPeopleViewToParent(rootView, ViewType.IMAGE, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 450)
            }
        }

        // 头像
        StickersManager.getAvatars {
            val sticker = it[1]
            sticker.localImageUri?.let { uri ->
                contentResolver.openInputStream(uri)?.also { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    val rootView = mPhotoEditor.addImageWithReturn(bmp)
                    avatarImageView = rootView.findViewById(R.id.imgPhotoEditorImage)
                    avatarImageView?.also {
                        mPhotoEditor.addPeopleViewToParent(rootView, ViewType.IMAGE, 500, 500, 100)
                    }
                }
            } ?: let {
                val bmp = BitmapFactory.decodeResource(resources, getImage(sticker.localImageName))
                val rootView = mPhotoEditor.addImageWithReturn(bmp)
                avatarImageView = rootView.findViewById(R.id.imgPhotoEditorImage)
                avatarImageView?.also {
                    mPhotoEditor.addPeopleViewToParent(rootView, ViewType.IMAGE, 500, 500, 100)
                }
            }

            // 装饰
            StickersManager.getHat {
                val stickerHat = it.first()
                val bmp = BitmapFactory.decodeResource(resources, getImage(stickerHat.localImageName))
                val rootView = mPhotoEditor.addImageWithReturn(bmp)
                hatImageView = rootView.findViewById(R.id.imgPhotoEditorImage)
                hatImageView?.also {
                    mPhotoEditor.addPeopleViewToParent(rootView, ViewType.IMAGE, 350, 350, -20)
                }
            }

        }
    }

    @SuppressLint("MissingPermission")
    private fun saveToFinish() {
        val dir = filesDir.absolutePath + "/peoples"
        if (!File(dir).exists()) {
            File(dir).mkdirs()
        }
        val path = filesDir.absolutePath + "/peoples/${UUID.randomUUID()}.png"
        binding.photoEditorView.isDrawingCacheEnabled = true
        val bitmap = binding.photoEditorView.drawingCache
        saveBitmapToPng(bitmap, path) {
            StickersManager.addPeople(File(path).toUri()){
                binding.photoEditorView.isDrawingCacheEnabled = false
                EventBus.getDefault().post(AddPeopleToListEvent())
                finish()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadingEvent(event: LoadingEvent) {
        if (event.loading) {
            binding.loadingGroup.visibility = View.VISIBLE
        } else {
            binding.loadingGroup.visibility = View.GONE
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddAvatarEvent(event: AddAvatarToPeopleEvent) {
        event.sticker.localImageUri?.also {
            contentResolver.openInputStream(it)?.also { stream ->
                val bmp = BitmapFactory.decodeStream(stream)
                avatarImageView?.setImageBitmap(bmp)
                uiChange = true
            }
        } ?: run {
            val bmp = BitmapFactory.decodeResource(resources, getImage(event.sticker.localImageName))
            avatarImageView?.setImageBitmap(bmp)
            uiChange = true
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddClothEvent(event: AddClothToPeopleEvent) {
        val bmp = BitmapFactory.decodeResource(resources, getImage(event.sticker.localImageName))
        clothImageView?.setImageBitmap(bmp)
        uiChange = true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddDecorateEvent(event: AddDecorateToPeopleEvent) {
        val bmp = BitmapFactory.decodeResource(resources, getImage(event.sticker.localImageName))
        hatImageView?.setImageBitmap(bmp)
        uiChange = true
    }

}