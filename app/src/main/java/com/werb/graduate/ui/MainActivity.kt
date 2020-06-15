package com.werb.graduate.ui

import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.werb.graduate.EditManager
import com.werb.graduate.ImageRatio
import com.werb.graduate.adapter.MainPagerAdapter
import com.werb.graduate.R
import com.werb.graduate.databinding.ActivityMainBinding
import com.werb.graduate.events.AddBackgroundEvent
import com.werb.graduate.events.AddPeopleToBgEvent
import com.werb.graduate.exts.getImage
import com.werb.graduate.model.StickersManager
import ja.burhanrashid52.photoeditor.PhotoEditor
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val editManager = EditManager()
    private lateinit var mPhotoEditor: PhotoEditor

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
        setupEditManager()
    }

    private fun setupUI() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    private fun setupTab() {
        binding.tabLayout.setSelectedTabIndicatorHeight(0)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = StickersManager.mainTabText[position]
        }.attach()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter =
            MainPagerAdapter(this)
    }

    private fun setupEditManager() {
        editManager.onRatioChange = {
            if (it != ImageRatio.ORIGINAL) {
                TransitionManager.beginDelayedTransition(binding.root)
                val set = ConstraintSet()
                set.clone(binding.root)
                set.setDimensionRatio(binding.photoEditorView.id, it.str)
                set.applyTo(binding.root)
            }
            updateLayoutRatioColor(it)
        }
        binding.layout11.setOnClickListener {
            editManager.imageRatio = ImageRatio.ONE_ONE
        }
        binding.layout43.setOnClickListener {
            editManager.imageRatio = ImageRatio.FOUR_THREE
        }
        binding.layout32.setOnClickListener {
            editManager.imageRatio = ImageRatio.THREE_TWO
        }
        binding.layout169.setOnClickListener {
            editManager.imageRatio = ImageRatio.SIXTEEN_NINE
        }

        mPhotoEditor = PhotoEditor.Builder(this, binding.photoEditorView).build()
    }

    private fun updateLayoutRatioColor(imageRatio: ImageRatio) {
        when(imageRatio) {
            ImageRatio.ONE_ONE -> {
                binding.photoEditorView.source.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.colorAccent
                ), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.colorAccent))
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.color_000000))
            }
            ImageRatio.FOUR_THREE -> {
                binding.photoEditorView.source.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.colorAccent
                ), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.colorAccent))
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.color_000000))
            }
            ImageRatio.THREE_TWO -> {
                binding.photoEditorView.source.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.colorAccent
                ), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.colorAccent))
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.color_000000))
            }
            ImageRatio.SIXTEEN_NINE -> {
                binding.photoEditorView.source.scaleType = ImageView.ScaleType.CENTER_CROP
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.colorAccent
                ), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.colorAccent))
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.color_000000))
            }
            ImageRatio.ORIGINAL -> {
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(
                    R.color.color_000000
                ), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.color_000000))
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddBackgroundEvent(event: AddBackgroundEvent) {
        editManager.imageRatio = ImageRatio.ORIGINAL
        binding.photoEditorView.source.scaleType = ImageView.ScaleType.FIT_CENTER
        event.sticker.localImageUri?.also { uri ->
            contentResolver.openInputStream(uri)?.also { stream ->
                val bounds = BitmapFactory.Options()
                bounds.inJustDecodeBounds = true
                BitmapFactory.decodeStream(stream, null, bounds)
                val set = ConstraintSet()
                set.clone(binding.root)
                set.setDimensionRatio(binding.photoEditorView.id, "${bounds.outWidth}:${bounds.outHeight}")
                set.applyTo(binding.root)
                Glide.with(this)
                    .load(uri)
                    .into(binding.photoEditorView.source)
            }
        } ?: run {
            val bounds = BitmapFactory.Options()
            bounds.inJustDecodeBounds = true
            BitmapFactory.decodeResource(resources, getImage(event.sticker.localImageName), bounds)
            val set = ConstraintSet()
            set.clone(binding.root)
            set.setDimensionRatio(binding.photoEditorView.id, "${bounds.outWidth}:${bounds.outHeight}")
            set.applyTo(binding.root)
            binding.photoEditorView.source.setImageDrawable(resources.getDrawable(getImage(event.sticker.localImageName)))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddPeopleToBg(event: AddPeopleToBgEvent) {
        event.sticker.localImageUri?.also { uri ->
            contentResolver.openInputStream(uri)?.also { stream ->
                val bmp = BitmapFactory.decodeStream(stream)
                mPhotoEditor.addImage(bmp)
            }
        } ?: run {
            val bmp = BitmapFactory.decodeResource(resources, getImage(event.sticker.localImageName))
            mPhotoEditor.addImage(bmp)
        }
    }

}