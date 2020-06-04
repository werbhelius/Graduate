package com.werb.graduate

import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.FileProvider
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.werb.graduate.databinding.ActivityMainBinding
import com.werb.graduate.events.AddBackgroundEvent
import com.werb.graduate.exts.getImage
import ja.burhanrashid52.photoeditor.PhotoEditor
import kotlinx.android.synthetic.main.layout_sticker.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileInputStream


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val editManager = EditManager()
    private lateinit var mPhotoEditor: PhotoEditor
    val FILE_PROVIDER_AUTHORITY = "com.werb.graduate.fileprovider"

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
        binding.backgroundBtn.setOnClickListener {
            binding.viewPager.setCurrentItem(0, true)
        }
        binding.charactersBtn.setOnClickListener {
            binding.viewPager.setCurrentItem(1, true)
        }
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = MainPagerAdapter(this)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                when(position) {
                    0 -> {
                        binding.backgroundBtn.alpha = 1f
                        binding.charactersBtn.alpha = 0.4f
                    }
                    1 -> {
                        binding.charactersBtn.alpha = 1f
                        binding.backgroundBtn.alpha = 0.4f
                    }
                }
            }
        })
    }

    private fun setupEditManager() {
        binding.photoEditorView.source.scaleType = ImageView.ScaleType.CENTER_CROP
        editManager.onRatioChange = {
            TransitionManager.beginDelayedTransition(binding.root)
            val set = ConstraintSet()
            set.clone(binding.root)
            set.setDimensionRatio(binding.photoEditorView.id, it.str)
            set.applyTo(binding.root)
            updateLayoutRatioColor(it)
        }
        editManager.imageRatio = ImageRatio.ONE_ONE
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
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.colorAccent))
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.color_000000))
            }
            ImageRatio.FOUR_THREE -> {
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.colorAccent))
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.color_000000))
            }
            ImageRatio.THREE_TWO -> {
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.colorAccent))
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.color_000000))
            }
            ImageRatio.SIXTEEN_NINE -> {
                binding.layout169Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN)
                binding.layout169Text.setTextColor(resources.getColor(R.color.colorAccent))
                binding.layout32Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout32Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout43Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout43Text.setTextColor(resources.getColor(R.color.color_000000))
                binding.layout11Image.colorFilter = PorterDuffColorFilter(resources.getColor(R.color.color_000000), PorterDuff.Mode.SRC_IN)
                binding.layout11Text.setTextColor(resources.getColor(R.color.color_000000))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddBackgroundEvent(event: AddBackgroundEvent) {
        event.sticker.localImageUri?.also { uri ->
            Glide.with(this)
                .load(uri)
                .transform(CenterCrop())
                .into(binding.photoEditorView.source)
        } ?: run {
            binding.photoEditorView.source.setImageDrawable(resources.getDrawable(getImage(event.sticker.localImageName)))
        }
    }

}