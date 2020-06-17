package com.werb.graduate.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.werb.azure.Azure
import com.werb.graduate.EditManager
import com.werb.graduate.ImageRatio
import com.werb.graduate.R
import com.werb.graduate.adapter.MainPagerAdapter
import com.werb.graduate.databinding.ActivityMainBinding
import com.werb.graduate.events.AddBackgroundEvent
import com.werb.graduate.events.AddPeopleToBgEvent
import com.werb.graduate.events.AddPropEvent
import com.werb.graduate.exts.getImage
import com.werb.graduate.exts.syncAction
import com.werb.graduate.model.StickersManager
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.SaveSettings
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


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
        binding.saveBtn.setOnClickListener {
            Azure(this)
                .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    if (it) {
                        saveToFinish()
                    } else {
                        Toast.makeText(this, "请在系统设置开启存储权限后重试", Toast.LENGTH_SHORT).show()
                    }
                }.request()
        }
        binding.deleteBtn.setOnClickListener {
            AlertDialog.Builder(this)
                .setMessage("清空当前操作？")
                .setPositiveButton("确定"
                ) { dialog, which ->
                    mPhotoEditor.clearAllViews()
                }
                .setNegativeButton("取消") { dialog, which ->
                    dialog.dismiss()
                }.create().show()
        }

        mPhotoEditor = PhotoEditor.Builder(this, binding.photoEditorView).build()

        StickersManager.getBackgrounds { backgrounds ->
            syncAction({
                if (backgrounds.isNotEmpty()) {
                    EventBus.getDefault().post(AddBackgroundEvent(backgrounds[1]))
                }
            })
        }

        StickersManager.getPeoples {
            syncAction({
                if (it.isNotEmpty()) {
                    EventBus.getDefault().post(AddPeopleToBgEvent(it[1]))
                }
            })

        }
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

    @SuppressLint("MissingPermission")
    private fun saveToFinish() {
        binding.photoEditorView.buildDrawingCache()
        val bitmap = binding.photoEditorView.drawingCache
        bitmap?.also {
            saveImage(it, UUID.randomUUID().toString() + ".jpg")
            Toast.makeText(this@MainActivity, "保存成功，请在系统相册查看。", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImage(bitmap: Bitmap, name: String) {
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            imageUri?.also {
                fos = resolver.openOutputStream(imageUri)
            }
        } else {
            val imagesDir: String =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString()
            val image = File(imagesDir, name)
            fos = FileOutputStream(image)
        }
        fos?.also {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onAddPropToBg(event: AddPropEvent) {
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