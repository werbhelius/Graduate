package com.werb.graduate.ui

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.werb.graduate.adapter.AddPeoplePagerAdapter
import com.werb.graduate.databinding.ActivityAddPeopleBinding
import com.werb.graduate.events.AddBackgroundEvent
import com.werb.graduate.events.ChangeAddPeopleModeEvent
import com.werb.graduate.events.LoadingEvent
import com.werb.graduate.exts.getImage
import com.werb.graduate.model.StickersManager
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.R
import ja.burhanrashid52.photoeditor.ViewType
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by wanbo on 2020/6/10.
 */
class AddPeopleActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddPeopleBinding
    private lateinit var mPhotoEditor: PhotoEditor
    private var hatImageView: ImageView? = null
    private var closeImageView: ImageView? = null
    private var avatarImageView: ImageView? = null

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
        binding.returnBtn.setOnClickListener {
            finish()
        }
        mPhotoEditor = PhotoEditor.Builder(this, binding.photoEditorView).build()
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
            closeImageView = rootView.findViewById(R.id.imgPhotoEditorImage)
            closeImageView?.also {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadingEvent(event: LoadingEvent) {
        if (event.loading) {
            binding.loadingGroup.visibility = View.VISIBLE
        } else {
            binding.loadingGroup.visibility = View.GONE
        }
    }

}